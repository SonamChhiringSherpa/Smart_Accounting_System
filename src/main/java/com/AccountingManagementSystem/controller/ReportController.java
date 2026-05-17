package com.AccountingManagementSystem.controller;

import java.io.IOException;

import com.AccountingManagementSystem.dao.TransactionDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// This servlet opens the reports page.
@WebServlet("/reports")
public class ReportController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final TransactionDao transactionDao = new TransactionDao();

	// This method loads simple report totals.
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");
		double totalIncome = transactionDao.getTotalIncome(userId);
		double totalExpense = transactionDao.getTotalExpense(userId);

		request.setAttribute("totalIncome", totalIncome);
		request.setAttribute("totalExpense", totalExpense);
		request.setAttribute("netProfit", totalIncome - totalExpense);
		request.setAttribute("transactionCount", transactionDao.getTransactionCount(userId));
		request.getRequestDispatcher("/WEB-INF/pages/reports.jsp").forward(request, response);
	}

	// The report form posts here, then reloads the report page.
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
