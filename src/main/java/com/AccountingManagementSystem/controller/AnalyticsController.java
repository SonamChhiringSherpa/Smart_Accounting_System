package com.AccountingManagementSystem.controller;

import java.io.IOException;

import com.AccountingManagementSystem.dao.TransactionDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// This servlet opens the analytics page.
@WebServlet("/analytics")
public class AnalyticsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final TransactionDao transactionDao = new TransactionDao();

	// This method loads totals and simple chart values for analytics.
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");
		double totalIncome = transactionDao.getTotalIncome(userId);
		double totalExpense = transactionDao.getTotalExpense(userId);
		double expenseRatio = totalIncome > 0 ? (totalExpense / totalIncome) * 100 : 0;

		request.setAttribute("totalIncome", totalIncome);
		request.setAttribute("totalExpense", totalExpense);
		request.setAttribute("netProfit", totalIncome - totalExpense);
		request.setAttribute("expenseRatio", expenseRatio);
		request.setAttribute("recentTransactions", transactionDao.getRecentTransactions(userId, 5));
		request.getRequestDispatcher("/WEB-INF/pages/analytics.jsp").forward(request, response);
	}

	// Analytics does not need separate POST logic, so it reloads the page.
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
