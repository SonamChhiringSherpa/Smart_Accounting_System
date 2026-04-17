package com.AccountingManagementSystem.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.AccountingManagementSystem.config.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * AuthController handles Login, Register, and Logout
 */
@WebServlet({ "/login", "/register", "/logout" ,"/admin"})
public class AuthController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String AdminName = "admin";
	private static final String AdminPassword = "1111";

	// ===================== GET =====================
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();

		if (path.equals("/login")) {
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);

		} else if (path.equals("/register")) {
			request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);

		} else if (path.equals("/logout")) {
			handleLogout(request, response);
		}
	else if (path.equals("/admin")) {
		request.getRequestDispatcher("/WEB-INF/pages/admin.jsp").forward(request, response);
	}
		
	}

	// ===================== POST =====================
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String path = request.getServletPath();

		if (path.equals("/login")) {
			handleLogin(request, response);

		} else if (path.equals("/register")) {
			handleRegister(request, response);
		}
	}

	// ===================== LOGIN =====================
	private void handleLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if (AdminName.equals(username) && AdminPassword.equals(password)) {
		    HttpSession session = request.getSession();
		    session.setAttribute("role", "admin");
		    session.setAttribute("adminName", username);

		    response.sendRedirect(request.getContextPath() + "/admin");
		    return;
		}
		try {
			Connection conn = DBConnection.getConnection();

			String sql = "SELECT * FROM users WHERE username=? OR email=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, username);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String dbPassword = rs.getString("password");

				String hashedInput = Integer.toHexString(password.hashCode());

				if (dbPassword.equals(hashedInput)) {

					// ✅ Session
					HttpSession session = request.getSession();
					session.setAttribute("user", rs.getString("username"));

					// ✅ Cookie
					Cookie userCookie = new Cookie("user_id", String.valueOf(rs.getInt("id")));
					userCookie.setMaxAge(60 * 60 * 24);
					response.addCookie(userCookie);

					response.sendRedirect(request.getContextPath() + "/home");

				} else {
					request.setAttribute("error", "Invalid password");
					request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
				}

			} else {
				request.setAttribute("error", "User not found");
				request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ===================== REGISTER =====================
	private void handleRegister(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String fullName = request.getParameter("fullName");
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		try {
			Connection conn = DBConnection.getConnection();

			String sql = "INSERT INTO users(full_name, username, email, password) VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, fullName);
			ps.setString(2, username);
			ps.setString(3, email);

			String hashedPassword = Integer.toHexString(password.hashCode());
			ps.setString(4, hashedPassword);

			int result = ps.executeUpdate();

			if (result > 0) {
				response.sendRedirect("login?success=true");
			} else {
				request.setAttribute("error", "Registration failed");
				request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ===================== LOGOUT =====================
	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}

		Cookie cookie = new Cookie("user_id", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		response.sendRedirect("login");
	}
}