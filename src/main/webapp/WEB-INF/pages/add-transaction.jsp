<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Transaction</title>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/add-transaction.css">

</head>
<body>

<div class="layout">

    <!-- 🔵 SIDEBAR -->
    <div class="sidebar">
        <h2>Smart Accounting</h2>

        <ul>
            <li><a href="home">Dashboard</a></li>
            <li class="active"><a href="#">Add Transaction</a></li>
            <li><a href="transactions">Transactions</a></li>
            <li><a href="#">Reports</a></li>
            <li><a href="#">Analytics</a></li>
        </ul>
    </div>

    <!-- 🔵 MAIN CONTENT -->
    <div class="main">

        <!-- 🔷 HEADER -->
        <div class="header">
            <input type="text" placeholder="Search transactions...">

            <div class="user">
                <span><%= session.getAttribute("user") %></span>
            </div>
        </div>

        <!-- 🔷 FORM SECTION -->
        <div class="content">

            <h2>Add Transaction</h2>
            <p>Record a new financial entry</p>

            <div class="card">

                <!-- 🔘 Tabs -->
                <div class="tabs">
                    <button type="button" class="active" onclick="setType('income')">Income</button>
                    <button type="button" onclick="setType('expense')">Expense</button>
                </div>

                <!-- 📌 FORM -->
                <form action="addTransaction" method="post">

                    <!-- Hidden type -->
                    <input type="hidden" name="type" id="type" value="income">

                    <label>Amount *</label>
                    <input type="number" step="0.01" name="amount" placeholder="$ 0.00" required>

                    <label>Category *</label>
                    <input type="text" name="category" placeholder="Enter category" required>

                    <label>Date *</label>
                    <input type="date" name="date" required>

                    <label>Description (optional)</label>
                    <textarea name="description" placeholder="Add a note..."></textarea>

                    <div class="actions">
                        <button type="reset" class="cancel">Cancel</button>
                        <button type="submit" class="submit">Add Transaction</button>
                    </div>

                </form>

            </div>

        </div>

    </div>
</div>

<script>
function setType(value) {
    document.getElementById("type").value = value;

    let buttons = document.querySelectorAll(".tabs button");
    buttons.forEach(btn => btn.classList.remove("active"));

    event.target.classList.add("active");
}
</script>

</body>
</html>