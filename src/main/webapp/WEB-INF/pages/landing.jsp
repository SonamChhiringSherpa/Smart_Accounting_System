<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Smart Accounting System</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/landing.css">
</head>
<body>
	<header class="navbar">
		<div class="nav-container">
			<a class="logo" href="${pageContext.request.contextPath}/landing"><span>S</span>Smart Accounting</a>
			<nav class="nav-links">
				<a href="#features">Features</a>
				<a href="#workflow">Workflow</a>
				<a href="#security">Security</a>
			</nav>
			<div class="nav-actions">
				<a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Login</a>
				<a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Get Started</a>
			</div>
		</div>
	</header>

	<main>
		<section class="hero">
			<div class="hero-overlay"></div>
			<div class="hero-content">
				<p class="eyebrow">Accounting for everyday business decisions</p>
				<h1>Smart Accounting System</h1>
				<p class="hero-copy">Track income, control expenses, review reports, and keep your account secure from one focused dashboard.</p>
				<div class="hero-actions">
					<a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Create Account</a>
					<a href="${pageContext.request.contextPath}/login" class="btn btn-ghost">Login</a>
				</div>
			</div>
		</section>

		<section id="features" class="section">
			<div class="section-heading">
				<p class="eyebrow">What you can do</p>
				<h2>Manage transactions without clutter</h2>
			</div>
			<div class="feature-grid">
				<article class="feature-card">
					<span class="feature-icon">₹</span>
					<h3>Income and Expenses</h3>
					<p>Record daily transactions with categories, dates, and notes that are easy to review later.</p>
				</article>
				<article class="feature-card">
					<span class="feature-icon">R</span>
					<h3>Reports</h3>
					<p>Summarize financial activity for selected periods and understand where money is moving.</p>
				</article>
				<article class="feature-card">
					<span class="feature-icon">A</span>
					<h3>Analytics</h3>
					<p>Compare totals, ratios, and recent activity without needing complex spreadsheet work.</p>
				</article>
			</div>
		</section>

		<section id="workflow" class="section workflow">
			<div class="section-heading">
				<p class="eyebrow">Simple workflow</p>
				<h2>From entry to insight in minutes</h2>
			</div>
			<div class="steps">
				<div class="step"><span>1</span><p>Create your secure account</p></div>
				<div class="step"><span>2</span><p>Add income and expense records</p></div>
				<div class="step"><span>3</span><p>Review reports and analytics</p></div>
				<div class="step"><span>4</span><p>Update your profile when details change</p></div>
			</div>
		</section>

		<section id="security" class="section security-band">
			<div>
				<p class="eyebrow">Built with safer defaults</p>
				<h2>Security features are part of the flow</h2>
				<p>Password hashing, reset links, session checks, account lockout, and current-password verification help protect account access.</p>
			</div>
			<a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Start Securely</a>
		</section>
	</main>

	<footer class="footer">
		<p>© 2026 Smart Accounting System</p>
		<div>
			<a href="${pageContext.request.contextPath}/login">Login</a>
			<a href="${pageContext.request.contextPath}/register">Register</a>
		</div>
	</footer>
</body>
</html>
