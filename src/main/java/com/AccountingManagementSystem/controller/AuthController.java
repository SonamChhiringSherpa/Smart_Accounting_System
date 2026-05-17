package com.AccountingManagementSystem.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.AccountingManagementSystem.config.DBConnection;
import com.AccountingManagementSystem.dao.UserDao;
import com.AccountingManagementSystem.model.User;
import com.AccountingManagementSystem.service.CsrfService;
import com.AccountingManagementSystem.service.EmailService;
import com.AccountingManagementSystem.service.OtpService;
import com.AccountingManagementSystem.service.PasswordService;
import com.AccountingManagementSystem.service.TokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// This servlet controls login, registration, logout, and the secure forgot password flow.
@WebServlet({ "/login", "/register", "/logout", "/forgot-password", "/verify-otp", "/reset-password" })
public class AuthController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String AdminName = "admin";
	private static final String AdminPassword = "1111";
	private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;
	private static final int MAX_LOGIN_ATTEMPTS = 5;
	private static final long LOGIN_LOCKOUT_TIME = 15 * 60 * 1000;
	private static final long RESET_TOKEN_EXPIRY_TIME = 60 * 60 * 1000;
	private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000;
	private static final long RESEND_COOLDOWN_TIME = 60 * 1000;
	private static final int MAX_OTP_ATTEMPTS = 5;
	private static final int MAX_RESET_REQUESTS = 3;
	private static final long RATE_LIMIT_TIME = 15 * 60 * 1000;

	private static final Map<String, RateInfo> RATE_LIMITS = new ConcurrentHashMap<>();
	private final UserDao userDao = new UserDao();
	private final EmailService emailService = new EmailService();

	// doGet opens authentication JSP pages.
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		request.setAttribute("csrfToken", CsrfService.getToken(request));

		if (path.equals("/login")) {
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
		} else if (path.equals("/register")) {
			request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
		} else if (path.equals("/logout")) {
			handleLogout(request, response);
		} else if (path.equals("/forgot-password")) {
			request.getRequestDispatcher("/WEB-INF/pages/forgot-password.jsp").forward(request, response);
		} else if (path.equals("/verify-otp")) {
			showOtpPage(request, response, null, null);
		} else if (path.equals("/reset-password")) {
			showResetPasswordPage(request, response, null, null, request.getParameter("token"));
		}
	}

	// doPost handles submitted authentication forms.
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();

		if (!CsrfService.isValid(request)) {
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Security check failed. Please try again.");
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
			return;
		}

		if (path.equals("/login")) {
			handleLogin(request, response);
		} else if (path.equals("/register")) {
			handleRegister(request, response);
		} else if (path.equals("/forgot-password")) {
			handleForgotPassword(request, response);
		} else if (path.equals("/verify-otp")) {
			handleOtpPost(request, response);
		} else if (path.equals("/reset-password")) {
			handlePasswordReset(request, response);
		}
	}

	// This method checks login details and starts a user session.
	private void handleLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if (AdminName.equals(username) && AdminPassword.equals(password)) {
			HttpSession session = startFreshSession(request);
			session.setAttribute("role", "admin");
			session.setAttribute("adminName", username);
			response.sendRedirect(request.getContextPath() + "/admin");
			return;
		}

		User user = userDao.getUserByLogin(username);

		if (user == null) {
			showLoginError(request, response, "Invalid username/email or password.");
			return;
		}

		Timestamp lockUntil = userDao.getLockUntil(user.getId());
		long now = System.currentTimeMillis();

		if (lockUntil != null && lockUntil.getTime() > now) {
			showLoginError(request, response, "This account is temporarily locked. Please try again after " + lockUntil + ".");
			return;
		}

		if (!PasswordService.checkPassword(password, user.getPassword())) {
			userDao.recordFailedLogin(user.getId(), LOGIN_LOCKOUT_TIME);
			int attempts = userDao.getFailedLoginAttempts(user.getId());

			if (attempts >= MAX_LOGIN_ATTEMPTS) {
				lockUntil = userDao.getLockUntil(user.getId());
				showLoginError(request, response, "Too many failed attempts. This account is locked until " + lockUntil + ".");
			} else {
				showLoginError(request, response,
						"Invalid username/email or password. Attempts left: " + (MAX_LOGIN_ATTEMPTS - attempts) + ".");
			}
			return;
		}

		userDao.clearLoginLockout(user.getId());
		HttpSession session = startFreshSession(request);
		session.setAttribute("user", user.getUsername());
		session.setAttribute("userId", user.getId());

		Cookie userCookie = new Cookie("user_id", String.valueOf(user.getId()));
		userCookie.setHttpOnly(true);
		userCookie.setMaxAge(SESSION_TIMEOUT_SECONDS);
		response.addCookie(userCookie);

		response.sendRedirect(request.getContextPath() + "/home");
	}

	// This method saves a new user account with a BCrypt password hash.
	private void handleRegister(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String fullName = request.getParameter("fullName");
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");

		if (!password.equals(confirmPassword)) {
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Passwords do not match.");
			request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
			return;
		}

		if (!PasswordService.isStrongPassword(password)) {
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Password must be 8+ chars with uppercase, lowercase, number, and special character.");
			request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
			return;
		}

		if (userDao.isUsernameOrEmailTaken(username, email, 0)) {
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Username or email is already registered.");
			request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
			return;
		}

		try (Connection conn = DBConnection.getConnection()) {
			String sql = "INSERT INTO users(full_name, username, email, password) VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, fullName);
			ps.setString(2, username);
			ps.setString(3, email);
			ps.setString(4, PasswordService.hashPassword(password));

			if (ps.executeUpdate() > 0) {
				ResultSet keys = ps.getGeneratedKeys();
				int userId = 0;

				if (keys.next()) {
					userId = keys.getInt(1);
				}

				HttpSession session = startFreshSession(request);
				session.setAttribute("user", username);
				session.setAttribute("userId", userId);

				Cookie userCookie = new Cookie("user_id", String.valueOf(userId));
				userCookie.setHttpOnly(true);
				userCookie.setMaxAge(SESSION_TIMEOUT_SECONDS);
				response.addCookie(userCookie);

				response.sendRedirect(request.getContextPath() + "/home");
			} else {
				request.setAttribute("csrfToken", CsrfService.getToken(request));
				request.setAttribute("error", "Registration failed.");
				request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
			}
		} catch (Exception e) {
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Something went wrong. Please try again.");
			request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
		}
	}

	// This method validates email, creates an OTP, and sends it to Gmail.
	private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = cleanEmail(request.getParameter("email"));
		String key = getRateLimitKey(request, email);

		if (isRateLimited(key)) {
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Too many reset requests. Please try again later.");
			request.getRequestDispatcher("/WEB-INF/pages/forgot-password.jsp").forward(request, response);
			return;
		}

		User user = userDao.getUserByEmail(email);

		if (user == null) {
			addRateLimitAttempt(key);
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Invalid email. No account found with this address.");
			request.getRequestDispatcher("/WEB-INF/pages/forgot-password.jsp").forward(request, response);
			return;
		}

		String otp = OtpService.generateOtp();

		if (!emailService.sendOtpEmail(email, otp)) {
			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("error", "Could not send OTP. Please check email service settings.");
			request.getRequestDispatcher("/WEB-INF/pages/forgot-password.jsp").forward(request, response);
			return;
		}

		addRateLimitAttempt(key);
		saveOtpInSession(request, email, otp);
		response.sendRedirect(request.getContextPath() + "/verify-otp");
	}

	private void handleOtpPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");

		if ("resend".equals(action)) {
			resendOtp(request, response);
		} else {
			verifyOtp(request, response);
		}
	}

	private void verifyOtp(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("resetEmail") == null) {
			response.sendRedirect(request.getContextPath() + "/forgot-password");
			return;
		}

		String email = (String) session.getAttribute("resetEmail");
		String otpHash = (String) session.getAttribute("otpHash");
		Long expiresAtValue = (Long) session.getAttribute("otpExpiresAt");
		Integer attemptsValue = (Integer) session.getAttribute("otpAttempts");

		if (otpHash == null || expiresAtValue == null || attemptsValue == null) {
			showOtpPage(request, response, "Please request a new OTP.", null);
			return;
		}

		long expiresAt = expiresAtValue;
		int attempts = attemptsValue;

		if (System.currentTimeMillis() > expiresAt) {
			clearOtpSession(session);
			showOtpPage(request, response, "Expired OTP. Please request a new OTP.", null);
			return;
		}

		if (attempts >= MAX_OTP_ATTEMPTS) {
			clearOtpSession(session);
			showOtpPage(request, response, "Maximum OTP attempts reached. Please request a new OTP.", null);
			return;
		}

		String typedOtp = request.getParameter("otp");
		attempts++;
		session.setAttribute("otpAttempts", attempts);

		if (!OtpService.matches(email, typedOtp, otpHash)) {
			showOtpPage(request, response, "Incorrect OTP. Attempts left: " + (MAX_OTP_ATTEMPTS - attempts), null);
			return;
		}

		session.setAttribute("otpVerified", true);
		session.removeAttribute("otpHash");
		session.removeAttribute("otpExpiresAt");
		session.removeAttribute("otpAttempts");
		response.sendRedirect(request.getContextPath() + "/reset-password");
	}

	private void resendOtp(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("resetEmail") == null) {
			response.sendRedirect(request.getContextPath() + "/forgot-password");
			return;
		}

		Long resendAvailableAtValue = (Long) session.getAttribute("resendAvailableAt");
		long resendAvailableAt = resendAvailableAtValue == null ? 0 : resendAvailableAtValue;

		if (System.currentTimeMillis() < resendAvailableAt) {
			showOtpPage(request, response, "Please wait before requesting another OTP.", null);
			return;
		}

		String email = (String) session.getAttribute("resetEmail");
		String key = getRateLimitKey(request, email);

		if (isRateLimited(key)) {
			showOtpPage(request, response, "Too many OTP requests. Please try again later.", null);
			return;
		}

		String otp = OtpService.generateOtp();

		if (!emailService.sendOtpEmail(email, otp)) {
			showOtpPage(request, response, "Could not send OTP. Please check email service settings.", null);
			return;
		}

		addRateLimitAttempt(key);
		saveOtpInSession(request, email, otp);
		showOtpPage(request, response, null, "A new OTP has been sent to your email.");
	}

	// This method updates the password after reset-token verification.
	private void handlePasswordReset(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String token = request.getParameter("token");
		User resetUser = getResetUser(token);
		HttpSession session = request.getSession(false);
		boolean otpVerified = session != null && Boolean.TRUE.equals(session.getAttribute("otpVerified"));

		if (resetUser == null && !otpVerified) {
			showResetPasswordPage(request, response, "This reset link is invalid or expired.", null, token);
			return;
		}

		String password = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");

		if (!PasswordService.isStrongPassword(password)) {
			showResetPasswordPage(request, response,
					"Password must be 8+ chars with uppercase, lowercase, number, and special character.", null, token);
			return;
		}

		if (!password.equals(confirmPassword)) {
			showResetPasswordPage(request, response, "Password mismatch. Please confirm the same password.", null, token);
			return;
		}

		String hashedPassword = PasswordService.hashPassword(password);

		String resetEmail = resetUser == null ? (String) session.getAttribute("resetEmail") : resetUser.getEmail();
		int resetUserId = resetUser == null ? 0 : resetUser.getId();

		if (userDao.updatePasswordByEmail(resetEmail, hashedPassword)) {
			if (resetUserId > 0) {
				userDao.clearPasswordResetToken(resetUserId);
			}

			if (session != null) {
				clearOtpSession(session);
				session.removeAttribute("resetEmail");
				session.removeAttribute("otpVerified");
			}

			request.setAttribute("csrfToken", CsrfService.getToken(request));
			request.setAttribute("success", "Password reset successful. You can login now.");
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
		} else {
			showResetPasswordPage(request, response, "Something went wrong. Please try again.", null, token);
		}
	}

	// This method opens the new password page only with a valid reset token.
	private void showResetPasswordPage(HttpServletRequest request, HttpServletResponse response, String error, String success,
			String token)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		boolean otpVerified = session != null && Boolean.TRUE.equals(session.getAttribute("otpVerified"));

		if ((token == null || token.trim().isEmpty()) && !otpVerified) {
			response.sendRedirect(request.getContextPath() + "/forgot-password");
			return;
		}

		if (error == null && !otpVerified && getResetUser(token) == null) {
			error = "This reset link is invalid or expired.";
		}

		request.setAttribute("csrfToken", CsrfService.getToken(request));
		request.setAttribute("resetToken", token);
		request.setAttribute("error", error);
		request.setAttribute("success", success);
		request.getRequestDispatcher("/WEB-INF/pages/reset-password.jsp").forward(request, response);
	}

	private void showOtpPage(HttpServletRequest request, HttpServletResponse response, String error, String success)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("resetEmail") == null) {
			response.sendRedirect(request.getContextPath() + "/forgot-password");
			return;
		}

		long now = System.currentTimeMillis();
		Long resendAvailableAtValue = (Long) session.getAttribute("resendAvailableAt");
		Long expiresAtValue = (Long) session.getAttribute("otpExpiresAt");
		long resendAvailableAt = resendAvailableAtValue == null ? now : resendAvailableAtValue;
		long expiresAt = expiresAtValue == null ? now : expiresAtValue;

		request.setAttribute("csrfToken", CsrfService.getToken(request));
		request.setAttribute("resetEmail", session.getAttribute("resetEmail"));
		request.setAttribute("resendSeconds", Math.max(0, (resendAvailableAt - now) / 1000));
		request.setAttribute("expirySeconds", Math.max(0, (expiresAt - now) / 1000));
		request.setAttribute("error", error);
		request.setAttribute("success", success);
		request.getRequestDispatcher("/WEB-INF/pages/verify-otp.jsp").forward(request, response);
	}

	private User getResetUser(String token) {
		if (token == null || token.trim().isEmpty()) {
			return null;
		}

		return userDao.getUserByValidResetToken(TokenService.hashToken(token.trim()));
	}

	private void saveOtpInSession(HttpServletRequest request, String email, String otp) {
		HttpSession session = request.getSession();
		long now = System.currentTimeMillis();
		session.setAttribute("resetEmail", email);
		session.setAttribute("otpHash", OtpService.hashOtp(email, otp));
		session.setAttribute("otpExpiresAt", now + OTP_EXPIRY_TIME);
		session.setAttribute("otpAttempts", 0);
		session.setAttribute("resendAvailableAt", now + RESEND_COOLDOWN_TIME);
		session.removeAttribute("otpVerified");
	}

	private void clearOtpSession(HttpSession session) {
		session.removeAttribute("otpHash");
		session.removeAttribute("otpExpiresAt");
		session.removeAttribute("otpAttempts");
		session.removeAttribute("resendAvailableAt");
	}

	// This method logs the user out and clears sensitive session values.
	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}

		Cookie cookie = new Cookie("user_id", "");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		response.sendRedirect(request.getContextPath() + "/login");
	}

	private HttpSession startFreshSession(HttpServletRequest request) {
		try {
			request.changeSessionId();
		} catch (IllegalStateException e) {
			// If there is no existing session yet, getSession creates one below.
		}

		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
		return session;
	}

	private void showLoginError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {
		request.setAttribute("csrfToken", CsrfService.getToken(request));
		request.setAttribute("error", message);
		request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
	}

	private String buildResetLink(HttpServletRequest request, String token) {
		return request.getScheme() + "://" + request.getServerName()
				+ (isDefaultPort(request) ? "" : ":" + request.getServerPort())
				+ request.getContextPath() + "/reset-password?token=" + token;
	}

	private boolean isDefaultPort(HttpServletRequest request) {
		return ("http".equals(request.getScheme()) && request.getServerPort() == 80)
				|| ("https".equals(request.getScheme()) && request.getServerPort() == 443);
	}

	// This method builds a simple rate limit key using IP and email.
	private String getRateLimitKey(HttpServletRequest request, String email) {
		String ip = request.getRemoteAddr();
		return ip + ":" + cleanEmail(email);
	}

	// This method safely cleans email input.
	private String cleanEmail(String email) {
		if (email == null) {
			return "";
		}
		return email.trim().toLowerCase();
	}

	// This method checks if too many reset requests were made.
	private boolean isRateLimited(String key) {
		RateInfo info = RATE_LIMITS.get(key);
		long now = System.currentTimeMillis();

		if (info == null || now > info.resetAt) {
			return false;
		}

		return info.count >= MAX_RESET_REQUESTS;
	}

	// This method records one reset request for rate limiting.
	private void addRateLimitAttempt(String key) {
		long now = System.currentTimeMillis();
		RateInfo info = RATE_LIMITS.get(key);

		if (info == null || now > info.resetAt) {
			info = new RateInfo();
			info.count = 1;
			info.resetAt = now + RATE_LIMIT_TIME;
		} else {
			info.count++;
		}

		RATE_LIMITS.put(key, info);
	}

	// This small class stores rate limit count and reset time.
	private static class RateInfo {
		int count;
		long resetAt;
	}
}
