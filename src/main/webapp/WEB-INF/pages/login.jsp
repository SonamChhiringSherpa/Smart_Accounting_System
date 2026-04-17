<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
	<div class="page">

 
    <div class="header">
        <h2>Smart Accounting</h2>
        <p>Sign in to your account</p>
    </div>

    <!-- Login Card -->
    <div class="card">
        <h3>Login</h3>

       <form action="${pageContext.request.contextPath}/login" method="post">
            
            <label>Email / Username</label>
            <input type="text" name="username" placeholder="Enter your email or username" required>

            <label>Password</label>
            <input type="password" name="password" placeholder="Enter your password" required>

            <button type="submit">Login</button>
        </form>

        <p class="register">
            Don’t have an account? <a href="register">Create account</a>
        </p>
    </div>

<p class="back"><a href="landing">back to home</a></p>
</div>


</body>
</html>