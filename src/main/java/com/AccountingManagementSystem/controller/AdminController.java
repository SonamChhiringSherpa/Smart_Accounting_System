package com.AccountingManagementSystem.controller;

import java.io.IOException;

import com.AccountingManagementSystem.dao.UserDao;
import com.AccountingManagementSystem.model.User;
import com.AccountingManagementSystem.service.PasswordService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// This servlet controls the admin user-management pages.
@WebServlet({ "/admin", "/update-user", "/delete-user", "/restore-user" })
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserDao userDao = new UserDao();

	// doGet opens the admin dashboard or deletes a user from a link.
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();

		if (path.equals("/admin")) {
			showAdminDashboard(request, response);
		} else if (path.equals("/delete-user")) {
			deleteUser(request, response);
		}
	}

	// doPost updates or restores user records from the admin dashboard.
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getServletPath().equals("/update-user")) {
			updateUser(request, response);
		} else if (request.getServletPath().equals("/restore-user")) {
			restoreUser(request, response);
		}
	}

	// This method loads all users and opens the admin dashboard JSP.
	private void showAdminDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("users", userDao.getAllUsers());
		request.setAttribute("deletedUsers", userDao.getDeletedUsersQueue());
		request.setAttribute("success", request.getParameter("success"));
		request.setAttribute("error", request.getParameter("error"));
		request.getRequestDispatcher("/WEB-INF/pages/admin.jsp").forward(request, response);
	}

	// This method updates one user record.
	private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			User user = new User();
			user.setId(Integer.parseInt(request.getParameter("id")));
			user.setFullName(request.getParameter("fullName"));
			user.setUsername(request.getParameter("username"));
			user.setEmail(request.getParameter("email"));

			String password = request.getParameter("password");
			String oldPassword = request.getParameter("oldPassword");

			// If the password field is unchanged, keep the old stored hash.
			if (password.equals(oldPassword)) {
				user.setPassword(oldPassword);
			} else {
				user.setPassword(PasswordService.hashPassword(password));
			}

			if (userDao.updateUser(user)) {
				response.sendRedirect(request.getContextPath() + "/admin?success=updated");
			} else {
				response.sendRedirect(request.getContextPath() + "/admin?error=update");
			}
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/admin?error=invalid");
		}
	}

	// This method removes one user record.
	private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int userId = Integer.parseInt(request.getParameter("id"));

			if (userDao.deleteUser(userId)) {
				response.sendRedirect(request.getContextPath() + "/admin?success=deleted");
			} else {
				response.sendRedirect(request.getContextPath() + "/admin?error=delete");
			}
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/admin?error=invalid");
		}
	}

	// This method restores a queued deleted user.
	private void restoreUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int deletedRecordId = Integer.parseInt(request.getParameter("deletedRecordId"));

			if (userDao.restoreDeletedUser(deletedRecordId)) {
				response.sendRedirect(request.getContextPath() + "/admin?success=restored");
			} else {
				response.sendRedirect(request.getContextPath() + "/admin?error=restore");
			}
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/admin?error=invalid");
		}
	}
}
