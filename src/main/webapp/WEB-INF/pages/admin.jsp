<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.AccountingManagementSystem.model.User" %>
<%
List<User> users = (List<User>) request.getAttribute("users");
List<User> deletedUsers = (List<User>) request.getAttribute("deletedUsers");
String success = (String) request.getAttribute("success");
String error = (String) request.getAttribute("error");
String adminName = String.valueOf(session.getAttribute("adminName"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin Dashboard | Smart Accounting System</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
	<div class="app-shell">
		<aside class="sidebar">
			<h1 class="brand"><span class="brand-mark">S</span><span>Smart Accounting System</span></h1>
			<nav class="nav-menu">
				<a class="nav-link active" href="${pageContext.request.contextPath}/admin"><span class="nav-icon">A</span>Admin Dashboard</a>
				<form action="${pageContext.request.contextPath}/logout" method="get">
					<button class="nav-button" type="submit"><span class="nav-icon">L</span>Logout</button>
				</form>
			</nav>
		</aside>

		<div class="content-shell">
			<header class="topnav">
				<div class="topnav-title">Smart Accounting System</div>
				<div class="search-box">
					<input type="search" data-table-search placeholder="Search users">
				</div>
				<details class="profile-menu">
					<summary><span class="avatar">A</span><span><%= adminName %></span></summary>
					<div class="profile-dropdown">
						<a href="${pageContext.request.contextPath}/admin">Admin Panel</a>
						<form action="${pageContext.request.contextPath}/logout" method="get">
							<button type="submit">Logout</button>
						</form>
					</div>
				</details>
			</header>

			<main class="main">
				<section class="page-header">
					<div>
						<h2 class="page-title">Admin Dashboard</h2>
						<p class="page-subtitle">View, update, remove, and restore registered user records.</p>
					</div>
				</section>

				<% if ("updated".equals(success)) { %>
				<div class="alert alert-success">User record updated successfully.</div>
				<% } else if ("deleted".equals(success)) { %>
				<div class="alert alert-success">User record moved to the deleted-user queue.</div>
				<% } else if ("restored".equals(success)) { %>
				<div class="alert alert-success">Deleted user restored successfully.</div>
				<% } else if (error != null) { %>
				<div class="alert alert-error">Something went wrong. Please try again.</div>
				<% } %>

				<section class="grid stats-grid">
					<div class="card accent-profit">
						<p class="stat-label">Registered Users</p>
						<p class="stat-value"><%= users == null ? 0 : users.size() %></p>
					</div>
					<div class="card accent-income">
						<p class="stat-label">Deleted Queue</p>
						<p class="stat-value"><%= deletedUsers == null ? 0 : deletedUsers.size() %></p>
					</div>
					<div class="card accent-expense">
						<p class="stat-label">User Controls</p>
						<p class="stat-value">Active</p>
					</div>
				</section>

				<section class="panel">
					<div class="panel-header">
						<h3>User Credentials</h3>
						<a class="button-secondary" href="${pageContext.request.contextPath}/admin">Refresh</a>
					</div>
					<div class="panel-body">
						<div class="table-wrap">
							<table class="admin-table">
								<thead>
									<tr>
										<th>ID</th>
										<th>Full Name</th>
										<th>Username</th>
										<th>Email</th>
										<th>Password</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<% if (users == null || users.isEmpty()) { %>
									<tr>
										<td colspan="6" class="empty-state">No users found.</td>
									</tr>
									<% } else {
										for (User user : users) {
											String formId = "userForm" + user.getId();
									%>
									<tr data-transaction-row>
										<td><%= user.getId() %></td>
										<td>
											<input class="form-control table-input" form="<%= formId %>" type="text" name="fullName" value="<%= user.getFullName() %>" required>
										</td>
										<td>
											<input class="form-control table-input" form="<%= formId %>" type="text" name="username" value="<%= user.getUsername() %>" required>
										</td>
										<td>
											<input class="form-control table-input" form="<%= formId %>" type="email" name="email" value="<%= user.getEmail() %>" required>
										</td>
										<td>
											<div class="password-control admin-password-control">
												<input class="form-control table-input password-field" form="<%= formId %>" type="password" name="password" value="<%= user.getPassword() %>" required title="Stored BCrypt hash. Enter a new password here to change it.">
												<button class="password-toggle table-password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
											</div>
										</td>
										<td>
											<form id="<%= formId %>" action="${pageContext.request.contextPath}/update-user" method="post"></form>
											<input form="<%= formId %>" type="hidden" name="id" value="<%= user.getId() %>">
											<input form="<%= formId %>" type="hidden" name="oldPassword" value="<%= user.getPassword() %>">
											<div class="actions-cell">
												<button class="button-warning" form="<%= formId %>" type="submit">Update</button>
												<a class="button-danger" href="${pageContext.request.contextPath}/delete-user?id=<%= user.getId() %>">Delete</a>
											</div>
										</td>
									</tr>
									<% }
									} %>
								</tbody>
							</table>
						</div>
					</div>
				</section>

				<section class="panel">
					<div class="panel-header">
						<h3>Deleted Users Queue</h3>
						<a class="button-secondary" href="${pageContext.request.contextPath}/admin">Refresh</a>
					</div>
					<div class="panel-body">
						<div class="table-wrap">
							<table class="admin-table">
								<thead>
									<tr>
										<th>Queue</th>
										<th>Original ID</th>
										<th>Full Name</th>
										<th>Username</th>
										<th>Email</th>
										<th>Deleted At</th>
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									<% if (deletedUsers == null || deletedUsers.isEmpty()) { %>
									<tr>
										<td colspan="7" class="empty-state">No deleted users in the queue.</td>
									</tr>
									<% } else {
										int queueNumber = 1;
										for (User user : deletedUsers) {
									%>
									<tr data-transaction-row>
										<td><span class="queue-badge"><%= queueNumber++ %></span></td>
										<td><%= user.getId() %></td>
										<td><%= user.getFullName() %></td>
										<td><%= user.getUsername() %></td>
										<td><%= user.getEmail() %></td>
										<td><%= user.getDeletedAt() %></td>
										<td>
											<form action="${pageContext.request.contextPath}/restore-user" method="post">
												<input type="hidden" name="deletedRecordId" value="<%= user.getDeletedRecordId() %>">
												<button class="button-secondary" type="submit">Restore</button>
											</form>
										</td>
									</tr>
									<% }
									} %>
								</tbody>
							</table>
						</div>
					</div>
				</section>
			</main>
		</div>
	</div>
	<script src="${pageContext.request.contextPath}/js/app.js"></script>
	<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
