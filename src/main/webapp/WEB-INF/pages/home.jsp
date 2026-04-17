<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Home</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/home.css">
</head>
<body>
	<div class="dashboard">

		<!-- ===== SIDEBAR ===== -->
		<aside class="sidebar">
			<h2>Smart Accounting</h2>

			<a href="#" class="active">Dashboard</a> 
			<a href="#">Add Transaction</a> 
			<a href="#">Transactions</a> 
			<a href="#">Reports</a> 
			<a href="#">Analytics</a>
			<form action="logout" method="get">
				<button>Logout</button>
			</form>
		</aside>

		<!-- ===== MAIN CONTENT ===== -->
		<div class="main">

			<!-- ===== NAVBAR ===== -->
			<header class="topbar">
				<h2>Dashboard</h2>
				<div class="user">Welcome, User</div>
			</header>

			<!-- ===== CARDS ===== -->
			<div class="cards">

				<div class="card income">
					<h3>Total Income</h3>
					<p>Rs. 50,000</p>
				</div>

				<div class="card expense">
					<h3>Total Expense</h3>
					<p>Rs. 30,000</p>
				</div>

				<div class="card profit">
					<h3>Net Profit</h3>
					<p>Rs. 20,000</p>
				</div>

			</div>

			<!-- ===== RECENT TRANSACTIONS ===== -->
			<div class="table-section">
				<h3>Recent Transactions</h3>

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
						<tr>
							<td>2026-04-10</td>
							<td>Income</td>
							<td>Rs. 10,000</td>
							<td>Sales</td>
						</tr>

						<tr>
							<td>2026-04-11</td>
							<td>Expense</td>
							<td>Rs. 5,000</td>
							<td>Supplies</td>
						</tr>

						<tr>
							<td>2026-04-12</td>
							<td>Income</td>
							<td>Rs. 15,000</td>
							<td>Service</td>
						</tr>
					</tbody>
				</table>

			</div>

		</div>

	</div>
</body>
</html>