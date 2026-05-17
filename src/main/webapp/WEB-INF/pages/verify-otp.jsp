<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
long resendSeconds = request.getAttribute("resendSeconds") == null ? 0 : (Long) request.getAttribute("resendSeconds");
long expirySeconds = request.getAttribute("expirySeconds") == null ? 0 : (Long) request.getAttribute("expirySeconds");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Verify OTP</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
	<div class="page">
		<div class="header">
			<h2>Smart Accounting</h2>
			<p>Enter the OTP sent to ${resetEmail}</p>
		</div>

		<div class="card">
			<h3>Verify OTP</h3>

			<% if (request.getAttribute("error") != null) { %>
			<p class="message error"><%= request.getAttribute("error") %></p>
			<% } else if (request.getAttribute("success") != null) { %>
			<p class="message success"><%= request.getAttribute("success") %></p>
			<% } %>

			<p class="hint">OTP expires in <span id="expiryTimer"><%= expirySeconds %></span> seconds.</p>

			<form action="${pageContext.request.contextPath}/verify-otp" method="post">
				<input type="hidden" name="csrfToken" value="${csrfToken}">
				<input type="hidden" name="action" value="verify">

				<label>6-digit OTP</label>
				<input type="text" name="otp" maxlength="6" pattern="[0-9]{6}" placeholder="Enter OTP" required>

				<button type="submit">Verify OTP</button>
			</form>

			<form action="${pageContext.request.contextPath}/verify-otp" method="post">
				<input type="hidden" name="csrfToken" value="${csrfToken}">
				<input type="hidden" name="action" value="resend">
				<button id="resendButton" type="submit" data-wait="<%= resendSeconds %>">Resend OTP</button>
			</form>

			<p class="register">
				Use another email? <a href="${pageContext.request.contextPath}/forgot-password">Start again</a>
			</p>
		</div>
	</div>

	<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
