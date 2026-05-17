package com.AccountingManagementSystem.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import com.AccountingManagementSystem.dao.TransactionDao;
import com.AccountingManagementSystem.model.Transaction;

// This servlet controls all transaction pages and transaction form actions.
@WebServlet({"/add-transaction", "/transactions", "/delete-transaction", "/edit-transaction"})
public class TransactionController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final TransactionDao dao = new TransactionDao();
       
    public TransactionController() {
        super();
    }

	// doGet is used to open pages or run simple link actions like delete.
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.equals("/add-transaction")) {
            request.getRequestDispatcher("/WEB-INF/pages/add-transaction.jsp")
                   .forward(request, response);
        } else if (path.equals("/transactions")) {
        	showTransactions(request, response);
        } else if (path.equals("/delete-transaction")) {
        	deleteTransaction(request, response);
        } else if (path.equals("/edit-transaction")) {
        	showEditForm(request, response);
        }
    }

	// doPost is used when a form is submitted.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getServletPath().equals("/add-transaction")) {
			addTransaction(request, response);
		} else if (request.getServletPath().equals("/edit-transaction")) {
			updateTransaction(request, response);
		} else {
			response.sendRedirect(request.getContextPath() + "/transactions");
		}
	}

	// This method shows the transaction list with search, filter, and sorting.
	private void showTransactions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");
		String search = request.getParameter("search");
		String type = request.getParameter("type");
		String sort = request.getParameter("sort");
		List<Transaction> transactions = dao.getTransactions(userId, search, type, sort);

		request.setAttribute("transactions", transactions);
		request.setAttribute("search", search == null ? "" : search);
		request.setAttribute("type", type == null ? "all" : type);
		request.setAttribute("sort", sort == null ? "date_desc" : sort);
		request.setAttribute("success", request.getParameter("success"));
		request.setAttribute("error", request.getParameter("error"));
		request.getRequestDispatcher("/WEB-INF/pages/transactions.jsp").forward(request, response);
	}

	// This method reads the add form and saves a new transaction.
	private void addTransaction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");

		try {
			String type = request.getParameter("type");
			double amount = Double.parseDouble(request.getParameter("amount"));

			if (!"income".equals(type) && !"expense".equals(type)) {
				request.setAttribute("error", "Please select a valid transaction type.");
				request.getRequestDispatcher("/WEB-INF/pages/add-transaction.jsp").forward(request, response);
				return;
			}

			if (amount <= 0) {
				request.setAttribute("error", "Amount must be greater than zero.");
				request.getRequestDispatcher("/WEB-INF/pages/add-transaction.jsp").forward(request, response);
				return;
			}

			Transaction t = new Transaction();
			t.setUserId(userId);
			t.setType(type);
			t.setAmount(amount);
			t.setCategory(request.getParameter("category"));
			t.setDate(Date.valueOf(request.getParameter("date")));
			t.setDescription(request.getParameter("description"));

			if (dao.addTransaction(t)) {
				response.sendRedirect(request.getContextPath() + "/transactions?success=added");
			} else {
				request.setAttribute("error", "Something went wrong while saving the transaction.");
				request.getRequestDispatcher("/WEB-INF/pages/add-transaction.jsp").forward(request, response);
			}
		} catch (Exception e) {
			request.setAttribute("error", "Please check the transaction details and try again.");
			request.getRequestDispatcher("/WEB-INF/pages/add-transaction.jsp").forward(request, response);
		}
	}

	// This method opens the edit form with the selected transaction.
	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");

		try {
			int transactionId = Integer.parseInt(request.getParameter("id"));
			Transaction transaction = dao.getTransactionById(transactionId, userId);

			if (transaction == null) {
				response.sendRedirect(request.getContextPath() + "/transactions?error=notfound");
				return;
			}

			request.setAttribute("transaction", transaction);
			request.getRequestDispatcher("/WEB-INF/pages/edit-transaction.jsp").forward(request, response);
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/transactions?error=invalid");
		}
	}

	// This method reads the edit form and updates the transaction.
	private void updateTransaction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");

		try {
			String type = request.getParameter("type");
			double amount = Double.parseDouble(request.getParameter("amount"));

			if (!"income".equals(type) && !"expense".equals(type)) {
				request.setAttribute("error", "Please select a valid transaction type.");
				showEditForm(request, response);
				return;
			}

			if (amount <= 0) {
				request.setAttribute("error", "Amount must be greater than zero.");
				showEditForm(request, response);
				return;
			}

			Transaction t = new Transaction();
			t.setId(Integer.parseInt(request.getParameter("id")));
			t.setUserId(userId);
			t.setType(type);
			t.setAmount(amount);
			t.setCategory(request.getParameter("category"));
			t.setDate(Date.valueOf(request.getParameter("date")));
			t.setDescription(request.getParameter("description"));

			if (dao.updateTransaction(t)) {
				response.sendRedirect(request.getContextPath() + "/transactions?success=updated");
			} else {
				request.setAttribute("error", "Something went wrong while updating the transaction.");
				request.setAttribute("transaction", t);
				request.getRequestDispatcher("/WEB-INF/pages/edit-transaction.jsp").forward(request, response);
			}
		} catch (Exception e) {
			request.setAttribute("error", "Please check the transaction details and try again.");
			showEditForm(request, response);
		}
	}

	// This method deletes the selected transaction.
	private void deleteTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		int userId = (int) session.getAttribute("userId");

		try {
			int transactionId = Integer.parseInt(request.getParameter("id"));
			if (dao.deleteTransaction(transactionId, userId)) {
				response.sendRedirect(request.getContextPath() + "/transactions?success=deleted");
			} else {
				response.sendRedirect(request.getContextPath() + "/transactions?error=delete");
			}
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/transactions?error=invalid");
		}
	}

}
