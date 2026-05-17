<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.AccountingManagementSystem.model.User" %>
<%
User profileUser = (User) request.getAttribute("profileUser");
String username = String.valueOf(session.getAttribute("user"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Profile | Smart Accounting System</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
	<div class="app-shell">
		<aside class="sidebar">
			<h1 class="brand"><span class="brand-mark">S</span><span>Smart Accounting System</span></h1>
			<nav class="nav-menu">
				<a class="nav-link" href="${pageContext.request.contextPath}/home"><span class="nav-icon">D</span>Dashboard</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/add-transaction"><span class="nav-icon">+</span>Add Transaction</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/transactions"><span class="nav-icon">T</span>Transactions</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/reports"><span class="nav-icon">R</span>Reports</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/analytics"><span class="nav-icon">A</span>Analytics</a>
				<a class="nav-link active" href="${pageContext.request.contextPath}/profile"><span class="nav-icon">P</span>Profile</a>
				<form action="${pageContext.request.contextPath}/logout" method="get">
					<button class="nav-button" type="submit"><span class="nav-icon">L</span>Logout</button>
				</form>
			</nav>
		</aside>

		<div class="content-shell">
			<header class="topnav">
				<div class="topnav-title">Smart Accounting System</div>
				<details class="profile-menu">
					<summary><span class="avatar"><%= username.substring(0, 1).toUpperCase() %></span><span><%= username %></span></summary>
					<div class="profile-dropdown">
						<a href="${pageContext.request.contextPath}/profile">Profile</a>
						<form action="${pageContext.request.contextPath}/logout" method="get">
							<button type="submit">Logout</button>
						</form>
					</div>
				</details>
			</header>

			<main class="main">
				<section class="page-header">
					<div>
						<h2 class="page-title">Profile</h2>
						<p class="page-subtitle">Update your username, email, or password securely.</p>
					</div>
				</section>

				<% if (request.getAttribute("error") != null) { %>
				<div class="alert alert-error"><%= request.getAttribute("error") %></div>
				<% } else if (request.getAttribute("success") != null) { %>
				<div class="alert alert-success"><%= request.getAttribute("success") %></div>
				<% } %>

				<section class="panel form-card">
					<div class="panel-header">
						<h3>Update Credentials</h3>
					</div>
					<div class="panel-body">
						<form action="${pageContext.request.contextPath}/update-profile" method="post">
							<input type="hidden" name="csrfToken" value="${csrfToken}">
							<div class="form-grid">
								<div class="form-group">
									<label>Username</label>
									<input class="form-control" type="text" name="username" value="<%= profileUser.getUsername() %>" required>
								</div>
								<div class="form-group">
									<label>Email</label>
									<input class="form-control" type="email" name="email" value="<%= profileUser.getEmail() %>" required>
								</div>
								<div class="form-group full">
									<label>Current Password</label>
									<div class="password-control">
										<input class="form-control" type="password" name="currentPassword" placeholder="Required to save changes" required>
										<button class="password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
									</div>
								</div>
								<div class="form-group">
									<label>New Password</label>
									<div class="password-control">
										<input class="form-control" type="password" name="newPassword" placeholder="Leave blank to keep current password">
										<button class="password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
									</div>
								</div>
								<div class="form-group">
									<label>Confirm New Password</label>
									<div class="password-control">
										<input class="form-control" type="password" name="confirmPassword" placeholder="Confirm new password">
										<button class="password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
									</div>
								</div>
							</div>
							<p class="form-hint">New passwords must use 8+ characters with uppercase, lowercase, number, and special character.</p>
							<button class="button" type="submit">Save Changes</button>
						</form>
					</div>
				</section>
			</main>
		</div>
	</div>
	<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
