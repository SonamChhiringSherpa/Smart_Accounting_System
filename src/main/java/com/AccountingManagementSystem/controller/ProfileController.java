package com.AccountingManagementSystem.controller;

import java.io.IOException;

import com.AccountingManagementSystem.dao.UserDao;
import com.AccountingManagementSystem.model.User;
import com.AccountingManagementSystem.service.CsrfService;
import com.AccountingManagementSystem.service.PasswordService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet({ "/profile", "/update-profile" })
public class ProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserDao userDao = new UserDao();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		showProfile(request, response, null, null);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!CsrfService.isValid(request)) {
			showProfile(request, response, "Security check failed. Please try again.", null);
			return;
		}

		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");
		User user = userDao.getUserById(userId);

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/logout");
			return;
		}

		String username = clean(request.getParameter("username"));
		String email = clean(request.getParameter("email"));
		String currentPassword = request.getParameter("currentPassword");
		String newPassword = request.getParameter("newPassword");
		String confirmPassword = request.getParameter("confirmPassword");

		if (username.isEmpty() || email.isEmpty() || !email.contains("@")) {
			showProfile(request, response, "Please enter a valid username and email.", null);
			return;
		}

		if (!PasswordService.checkPassword(currentPassword, user.getPassword())) {
			showProfile(request, response, "Current password is incorrect.", null);
			return;
		}

		if (userDao.isUsernameOrEmailTaken(username, email, userId)) {
			showProfile(request, response, "Username or email is already used by another account.", null);
			return;
		}

		String hashedPassword = null;
		boolean wantsPasswordChange = newPassword != null && !newPassword.trim().isEmpty();

		if (wantsPasswordChange) {
			if (!newPassword.equals(confirmPassword)) {
				showProfile(request, response, "New password and confirmation do not match.", null);
				return;
			}

			if (!PasswordService.isStrongPassword(newPassword)) {
				showProfile(request, response,
						"New password must be 8+ chars with uppercase, lowercase, number, and special character.", null);
				return;
			}

			hashedPassword = PasswordService.hashPassword(newPassword);
		}

		if (userDao.updateCredentials(userId, username, email, hashedPassword)) {
			session.setAttribute("user", username);
			showProfile(request, response, null, "Profile updated successfully.");
		} else {
			showProfile(request, response, "Could not update profile. Please try again.", null);
		}
	}

	private void showProfile(HttpServletRequest request, HttpServletResponse response, String error, String success)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");
		User user = userDao.getUserById(userId);

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/logout");
			return;
		}

		request.setAttribute("csrfToken", CsrfService.getToken(request));
		request.setAttribute("profileUser", user);
		request.setAttribute("error", error);
		request.setAttribute("success", success);
		request.getRequestDispatcher("/WEB-INF/pages/profile.jsp").forward(request, response);
	}

	private String clean(String value) {
		return value == null ? "" : value.trim();
	}
}
