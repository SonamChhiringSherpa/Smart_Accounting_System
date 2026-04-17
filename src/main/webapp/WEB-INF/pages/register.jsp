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

        <form action="register" method="post">

            <label>Full Name</label>
            <input type="text" name="fullName" placeholder="Enter your full name" required>

            <label>Username</label>
            <input type="text" name="username" placeholder="Choose a username" required>

            <label>Email</label>
            <input type="email" name="email" placeholder="Enter your email" required>

            <label>Password</label>
            <input type="password" name="password" placeholder="Create a password" required>

            <button type="submit">Register</button>
        </form>

        <p class="login">
            Already have an account? <a href="login">Login</a>
        </p>
    </div>

<p class="back"><a href="landing">back to home</a></p>
 
</div>

</body>
</html>