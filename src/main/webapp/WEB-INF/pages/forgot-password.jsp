<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Forgot Password</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
	<div class="page">
		<div class="header">
			<h2>Smart Accounting</h2>
			<p>Receive a secure OTP on your registered email</p>
		</div>

		<div class="card">
			<h3>Forgot Password</h3>

			<% if (request.getAttribute("error") != null) { %>
			<p class="message error"><%= request.getAttribute("error") %></p>
			<% } else if (request.getAttribute("success") != null) { %>
			<p class="message success"><%= request.getAttribute("success") %></p>
			<% } %>

			<form action="${pageContext.request.contextPath}/forgot-password" method="post">
				<input type="hidden" name="csrfToken" value="${csrfToken}">

				<label>Registered Email Address</label>
				<input type="email" name="email" placeholder="name@example.com" required>

				<button type="submit">Send OTP</button>
			</form>

			<p class="register">
				Remembered your password? <a href="${pageContext.request.contextPath}/login">Login</a>
			</p>
		</div>

		<p class="back"><a href="${pageContext.request.contextPath}/landing">back to home</a></p>
	</div>
</body>
</html>
