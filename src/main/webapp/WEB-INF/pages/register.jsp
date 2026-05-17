<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/register.css">
</head>
<body>
<div class="page">

    <!-- Header -->
    <div class="header">
        <h2>Smart Accounting</h2>
        <p>Create your free account</p>
    </div>

    <!-- Register Card -->
    <div class="card">
        <h3>Register</h3>

        <% if (request.getAttribute("error") != null) { %>
        <p class="message error"><%= request.getAttribute("error") %></p>
        <% } %>

        <form action="register" method="post">
            <input type="hidden" name="csrfToken" value="${csrfToken}">

            <label>Full Name</label>
            <input type="text" name="fullName" placeholder="Enter your full name" required>

            <label>Username</label>
            <input type="text" name="username" placeholder="Choose a username" required>

            <label>Email</label>
            <input type="email" name="email" placeholder="Enter your email" required>

            <label>Password</label>
            <div class="password-control">
                <input type="password" name="password" placeholder="Create a password" required>
                <button class="password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
            </div>

            <label>Confirm Password</label>
            <div class="password-control">
                <input type="password" name="confirmPassword" placeholder="Confirm your password" required>
                <button class="password-toggle" type="button" data-password-toggle aria-label="Show password" title="Show password"></button>
            </div>

            <p class="hint">Password must be 8+ characters with uppercase, lowercase, number, and special character.</p>

            <button type="submit">Register</button>
        </form>

        <p class="login">
            Already have an account? <a href="login">Login</a>
        </p>
    </div>

<p class="back"><a href="landing">back to home</a></p>
 
</div>

<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
