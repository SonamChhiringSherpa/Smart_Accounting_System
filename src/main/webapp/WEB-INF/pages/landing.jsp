<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Smart Accounting</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/landing.css">
</head>
<body>
<header class="navbar">
    <div class="nav-container">

        <!-- Logo -->
        <div class="logo">
             Smart Accounting
        </div>

        <!-- Navigation Links -->
        <nav class="nav-links">
            <a href="#features">Features</a>
            <a href="#how">How It Works</a>
        </nav>

        <!-- Buttons -->
        <div class="nav-actions">
            <a href="login" class="btn btn-secondary">Login</a>
            <a href="register" class="btn btn-primary">Register</a>
        </div>

    </div>
</header>

<!-- ===== HERO SECTION ===== -->
<section class="hero">
    <div class="hero-content">
        <h1>Manage Your Business Finances Easily</h1>
        <p>Track income, expenses, and generate reports all in one place.</p>

        <div class="hero-buttons">
            <a href="register" class="btn btn-primary">Get Started</a>
            <a href="login" class="btn btn-secondary">Login</a>
        </div>
    </div>
</section>

<!-- ===== FEATURES ===== -->
<section id="features" class="features">
    <h2>Features</h2>

    <div class="feature-grid">
        <div class="card">
            <h3>💰 Track Income and Expenses</h3>
            <p>Keep record of all your transactions in one place.</p>
        </div>

        <div class="card">
            <h3>📊 Generate Reports</h3>
            <p>View summaries of your financial activity instantly.</p>
        </div>

        <div class="card">
            <h3>📈 Basic Analytics</h3>
            <p>Analyze your business performance with simple insights.</p>
        </div>

        <div class="card">
            <h3>🔐 Secure System</h3>
            <p>Your data is protected with secure login and encryption.</p>
        </div>
    </div>
</section>

<!-- ===== HOW IT WORKS ===== -->
<section id="how" class="how">
    <h2>How It Works</h2>

    <div class="steps">
        <div class="step">
            <h3>1</h3>
            <p>Create Account</p>
        </div>

        <div class="step">
            <h3>2</h3>
            <p>Add Transactions</p>
        </div>

        <div class="step">
            <h3>3</h3>
            <p>View Reports</p>
        </div>

        <div class="step">
            <h3>4</h3>
            <p>Analyze Data</p>
        </div>
    </div>
</section>

<!-- ===== CTA ===== -->
<section class="cta">
    <h2>Start managing your finances today</h2>
    <a href="register" class="btn btn-primary">Create Account</a>
</section>

<footer class="footer">
    <div class="footer-container">

        <p>© 2026 Smart Accounting System</p>
        <p>College Project – DSA Module</p>

    </div>
</footer>
</body>

</html>