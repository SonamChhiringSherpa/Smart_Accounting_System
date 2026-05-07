package com.AccountingManagementSystem.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;

import com.AccountingManagementSystem.dao.TransactionDao;
import com.AccountingManagementSystem.model.Transaction;

/**
 * Servlet implementation class TransactionController
 */
@WebServlet({"/add-transaction"})
public class TransactionController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TransactionDao dao = new TransactionDao();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TransactionController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.equals("/add-transaction")) {
            request.getRequestDispatcher("/WEB-INF/pages/add-transaction.jsp")
                   .forward(request, response);

        
        }
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");

        Transaction t = new Transaction();
        t.setUserId(userId);
        t.setType(request.getParameter("type"));
        t.setAmount(Double.parseDouble(request.getParameter("amount")));
        t.setCategory(request.getParameter("category"));
        t.setDate(Date.valueOf(request.getParameter("date")));
        t.setDescription(request.getParameter("description"));

        dao.addTransaction(t);

        response.sendRedirect("transactions");
        
	}

}
