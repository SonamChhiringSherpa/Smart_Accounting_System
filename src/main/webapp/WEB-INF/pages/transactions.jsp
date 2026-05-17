<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.DecimalFormat, java.util.List, com.AccountingManagementSystem.model.Transaction" %>
<%
DecimalFormat money = new DecimalFormat("#,##0.00");
List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
String username = String.valueOf(session.getAttribute("user"));
String success = (String) request.getAttribute("success");
String error = (String) request.getAttribute("error");
String search = String.valueOf(request.getAttribute("search"));
String type = String.valueOf(request.getAttribute("type"));
String sort = String.valueOf(request.getAttribute("sort"));
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Transactions | Smart Accounting System</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>
	<div class="app-shell">
		<aside class="sidebar">
			<h1 class="brand"><span class="brand-mark">S</span><span>Smart Accounting System</span></h1>
			<nav class="nav-menu">
				<a class="nav-link" href="${pageContext.request.contextPath}/home"><span class="nav-icon">D</span>Dashboard</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/add-transaction"><span class="nav-icon">+</span>Add Transaction</a>
				<a class="nav-link active" href="${pageContext.request.contextPath}/transactions"><span class="nav-icon">T</span>Transactions</a>
				<a class="nav-link" href="${pageContext.request.contextPath}/reports"><span class="nav-icon">R</span>Reports</a>
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
						<h2 class="page-title">Transactions</h2>
						<p class="page-subtitle">Search, filter, and manage your recorded financial entries.</p>
					</div>
					<a class="button" href="${pageContext.request.contextPath}/add-transaction">Add Transaction</a>
				</section>

				<% if ("added".equals(success)) { %>
				<div class="alert alert-success">Transaction added successfully.</div>
				<% } else if ("updated".equals(success)) { %>
				<div class="alert alert-success">Transaction updated successfully.</div>
				<% } else if ("deleted".equals(success)) { %>
				<div class="alert alert-success">Transaction deleted successfully.</div>
				<% } else if (error != null) { %>
				<div class="alert alert-error">Something went wrong. Please try again.</div>
				<% } %>

				<section class="panel">
					<div class="panel-header">
						<h3>All Transactions</h3>
					</div>
					<div class="panel-body">
						<form class="table-tools" action="${pageContext.request.contextPath}/transactions" method="get">
							<input type="search" name="search" value="<%= search %>" placeholder="Search by category, type, date, or note">
							<select name="type">
								<option value="all" <%= "all".equals(type) ? "selected" : "" %>>All types</option>
								<option value="income" <%= "income".equals(type) ? "selected" : "" %>>Income</option>
								<option value="expense" <%= "expense".equals(type) ? "selected" : "" %>>Expense</option>
							</select>
							<select name="sort">
								<option value="date_desc" <%= "date_desc".equals(sort) ? "selected" : "" %>>Newest first</option>
								<option value="date_asc" <%= "date_asc".equals(sort) ? "selected" : "" %>>Oldest first</option>
								<option value="amount_desc" <%= "amount_desc".equals(sort) ? "selected" : "" %>>Highest amount</option>
								<option value="amount_asc" <%= "amount_asc".equals(sort) ? "selected" : "" %>>Lowest amount</option>
								<option value="category_asc" <%= "category_asc".equals(sort) ? "selected" : "" %>>Category A-Z</option>
								<option value="type_asc" <%= "type_asc".equals(sort) ? "selected" : "" %>>Type A-Z</option>
							</select>
							<button class="button" type="submit">Search</button>
							<a class="button-secondary" href="${pageContext.request.contextPath}/transactions">Reset</a>
						</form>
						<div class="table-wrap">
							<table>
								<thead>
									<tr>
										<th>Date</th>
										<th>Type</th>
										<th>Amount</th>
										<th>Category</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<% if (transactions == null || transactions.isEmpty()) { %>
									<tr><td colspan="5" class="empty-state">No transactions found.</td></tr>
									<% } else {
										for (Transaction transaction : transactions) { %>
									<tr data-transaction-row data-type="<%= transaction.getType() %>">
										<td><%= transaction.getDate() %></td>
										<td><span class="badge <%= transaction.getType() %>"><%= transaction.getType() %></span></td>
										<td>Rs. <%= money.format(transaction.getAmount()) %></td>
										<td><%= transaction.getCategory() %></td>
										<td>
											<div class="actions-cell">
												<a class="button-warning" href="${pageContext.request.contextPath}/edit-transaction?id=<%= transaction.getId() %>">Edit</a>
												<a class="button-danger" href="${pageContext.request.contextPath}/delete-transaction?id=<%= transaction.getId() %>">Delete</a>
											</div>
										</td>
									</tr>
									<% }
									} %>
								</tbody>
							</table>
						</div>
					</div>
				</section>
			</main>
		</div>
	</div>
	<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
