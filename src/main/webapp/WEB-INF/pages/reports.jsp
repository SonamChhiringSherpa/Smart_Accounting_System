<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.DecimalFormat" %>
<%
DecimalFormat money = new DecimalFormat("#,##0.00");
double totalIncome = request.getAttribute("totalIncome") == null ? 0 : (Double) request.getAttribute("totalIncome");
double totalExpense = request.getAttribute("totalExpense") == null ? 0 : (Double) request.getAttribute("totalExpense");
double netProfit = request.getAttribute("netProfit") == null ? 0 : (Double) request.getAttribute("netProfit");
int transactionCount = request.getAttribute("transactionCount") == null ? 0 : (Integer) request.getAttribute("transactionCount");
String username = String.valueOf(session.getAttribute("user"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Reports | Smart Accounting System</title>
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
				<a class="nav-link active" href="${pageContext.request.contextPath}/reports"><span class="nav-icon">R</span>Reports</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/analytics"><span class="nav-icon">A</span>Analytics</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/profile"><span class="nav-icon">P</span>Profile</a>
				<form action="${pageContext.request.contextPath}/logout" method="get"><button class="nav-button" type="submit"><span class="nav-icon">L</span>Logout</button></form>
			</nav>
		</aside>

		<div class="content-shell">
			<header class="topnav">
				<div class="topnav-title">Smart Accounting System</div>
				<div class="search-box"><input type="search" placeholder="Search records"></div>
				<details class="profile-menu">
					<summary><span class="avatar"><%= username.substring(0, 1).toUpperCase() %></span><span><%= username %></span></summary>
					<div class="profile-dropdown">
						<a href="${pageContext.request.contextPath}/profile">Profile</a>
						<form action="${pageContext.request.contextPath}/logout" method="get"><button type="submit">Logout</button></form>
					</div>
				</details>
			</header>

			<main class="main">
				<section class="page-header">
					<div>
						<h2 class="page-title">Reports</h2>
						<p class="page-subtitle">Generate simple summaries for a selected period.</p>
					</div>
				</section>

				<section class="panel">
					<div class="panel-header">
						<h3>Date Range</h3>
					</div>
					<div class="panel-body">
						<form class="report-form" action="${pageContext.request.contextPath}/reports" method="post">
							<div>
								<label for="fromDate">From</label>
								<input id="fromDate" class="form-control" type="date" name="fromDate">
							</div>
							<div>
								<label for="toDate">To</label>
								<input id="toDate" class="form-control" type="date" name="toDate">
							</div>
							<button class="button" type="submit">Generate Report</button>
						</form>
					</div>
				</section>

				<section class="grid stats-grid">
					<div class="card accent-income">
						<p class="stat-label">Total Income</p>
						<p class="stat-value">Rs. <%= money.format(totalIncome) %></p>
					</div>
					<div class="card accent-expense">
						<p class="stat-label">Total Expense</p>
						<p class="stat-value">Rs. <%= money.format(totalExpense) %></p>
					</div>
					<div class="card accent-profit">
						<p class="stat-label">Net Profit</p>
						<p class="stat-value">Rs. <%= money.format(netProfit) %></p>
					</div>
				</section>

				<section class="panel">
					<div class="panel-header">
						<h3>Report Details</h3>
					</div>
					<div class="panel-body">
						<p class="page-subtitle">Total recorded transactions: <strong><%= transactionCount %></strong></p>
					</div>
				</section>
			</main>
		</div>
	</div>
</body>
</html>
