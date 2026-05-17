<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.AccountingManagementSystem.model.Transaction" %>
<%
Transaction transaction = (Transaction) request.getAttribute("transaction");
String username = String.valueOf(session.getAttribute("user"));
String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Edit Transaction | Smart Accounting System</title>
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
						<h2 class="page-title">Edit Transaction</h2>
						<p class="page-subtitle">Update the selected financial record.</p>
					</div>
				</section>

				<% if (error != null) { %>
				<div class="alert alert-error"><%= error %></div>
				<% } %>

				<section class="card form-card">
					<form action="${pageContext.request.contextPath}/edit-transaction" method="post">
						<input type="hidden" name="id" value="<%= transaction.getId() %>">
						<div class="form-grid">
							<div class="form-group">
								<label for="type">Transaction Type</label>
								<select id="type" name="type" class="form-control" required>
									<option value="income" <%= "income".equals(transaction.getType()) ? "selected" : "" %>>Income</option>
									<option value="expense" <%= "expense".equals(transaction.getType()) ? "selected" : "" %>>Expense</option>
								</select>
							</div>
							<div class="form-group">
								<label for="amount">Amount</label>
								<input id="amount" class="form-control" type="number" name="amount" min="0.01" step="0.01" value="<%= transaction.getAmount() %>" required>
							</div>
							<div class="form-group">
								<label for="category">Category</label>
								<input id="category" class="form-control" type="text" name="category" value="<%= transaction.getCategory() %>" required>
							</div>
							<div class="form-group">
								<label for="date">Date</label>
								<input id="date" class="form-control" type="date" name="date" value="<%= transaction.getDate() %>" required>
							</div>
							<div class="form-group full">
								<label for="description">Description</label>
								<textarea id="description" class="form-control" name="description"><%= transaction.getDescription() == null ? "" : transaction.getDescription() %></textarea>
							</div>
						</div>
						<div class="form-actions">
							<a class="button-secondary" href="${pageContext.request.contextPath}/transactions">Cancel</a>
							<button class="button" type="submit">Update Transaction</button>
						</div>
					</form>
				</section>
			</main>
		</div>
	</div>
</body>
</html>
