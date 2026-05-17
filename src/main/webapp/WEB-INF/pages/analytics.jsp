<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.DecimalFormat, java.util.List, com.AccountingManagementSystem.model.Transaction" %>
<%
DecimalFormat money = new DecimalFormat("#,##0.00");
DecimalFormat percent = new DecimalFormat("#0.0");
double totalIncome = request.getAttribute("totalIncome") == null ? 0 : (Double) request.getAttribute("totalIncome");
double totalExpense = request.getAttribute("totalExpense") == null ? 0 : (Double) request.getAttribute("totalExpense");
double netProfit = request.getAttribute("netProfit") == null ? 0 : (Double) request.getAttribute("netProfit");
double expenseRatio = request.getAttribute("expenseRatio") == null ? 0 : (Double) request.getAttribute("expenseRatio");
double maxTotal = Math.max(totalIncome, totalExpense);
double incomeBar = maxTotal > 0 ? (totalIncome / maxTotal) * 100 : 0;
double expenseBar = maxTotal > 0 ? (totalExpense / maxTotal) * 100 : 0;
List<Transaction> recentTransactions = (List<Transaction>) request.getAttribute("recentTransactions");
String username = String.valueOf(session.getAttribute("user"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Analytics | Smart Accounting System</title>
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
				<a class="nav-link" href="${pageContext.request.contextPath}/reports"><span class="nav-icon">R</span>Reports</a>
				<a class="nav-link active" href="${pageContext.request.contextPath}/analytics"><span class="nav-icon">A</span>Analytics</a>
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
						<h2 class="page-title">Analytics</h2>
						<p class="page-subtitle">Simple insights without complex charts.</p>
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
						<h3>Monthly Summary</h3>
						<strong>Expense ratio: <%= percent.format(expenseRatio) %>%</strong>
					</div>
					<div class="panel-body">
						<div class="bar-list">
							<div class="bar-row">
								<span>Income</span>
								<div class="bar-track"><div class="bar-fill income" data-bar-value="<%= incomeBar %>"></div></div>
								<strong>Rs. <%= money.format(totalIncome) %></strong>
							</div>
							<div class="bar-row">
								<span>Expense</span>
								<div class="bar-track"><div class="bar-fill expense" data-bar-value="<%= expenseBar %>"></div></div>
								<strong>Rs. <%= money.format(totalExpense) %></strong>
							</div>
						</div>
					</div>
				</section>

				<section class="panel">
					<div class="panel-header">
						<h3>Recent Trends</h3>
					</div>
					<div class="table-wrap">
						<table>
							<thead>
								<tr>
									<th>Date</th>
									<th>Type</th>
									<th>Amount</th>
									<th>Category</th>
								</tr>
							</thead>
							<tbody>
								<% if (recentTransactions == null || recentTransactions.isEmpty()) { %>
								<tr><td colspan="4" class="empty-state">Add transactions to view trends.</td></tr>
								<% } else {
									for (Transaction transaction : recentTransactions) { %>
								<tr>
									<td><%= transaction.getDate() %></td>
									<td><span class="badge <%= transaction.getType() %>"><%= transaction.getType() %></span></td>
									<td>Rs. <%= money.format(transaction.getAmount()) %></td>
									<td><%= transaction.getCategory() %></td>
								</tr>
								<% }
								} %>
							</tbody>
						</table>
					</div>
				</section>
			</main>
		</div>
	</div>
	<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
