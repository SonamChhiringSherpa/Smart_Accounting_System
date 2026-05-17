<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Create New Password</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
	<div class="page">
		<div class="header">
			<h2>Smart Accounting</h2>
			<p>Create a strong new password</p>
		</div>

		<div class="card">
			<h3>New Password</h3>

			<% if (request.getAttribute("error") != null) { %>
			<p class="message error"><%= request.getAttribute("error") %></p>
			<% } %>

			<form action="${pageContext.request.contextPath}/reset-password" method="post">
				<input type="hidden" name="csrfToken" value="${csrfToken}">
				<input type="hidden" name="token" value="${resetToken}">

				<label>New Password</label>
				<div class="password-control">
					<input type="password" name="newPassword" placeholder="Strong password" required>
					<button class="password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
				</div>

				<label>Confirm Password</label>
				<div class="password-control">
					<input type="password" name="confirmPassword" placeholder="Confirm password" required>
					<button class="password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
				</div>

				<p class="hint">Use 8+ characters with uppercase, lowercase, number, and special character.</p>

				<button type="submit">Reset Password</button>
			</form>
		</div>
	</div>
	<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
