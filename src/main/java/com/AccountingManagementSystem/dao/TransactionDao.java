package com.AccountingManagementSystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.AccountingManagementSystem.config.DBConnection;
import com.AccountingManagementSystem.model.Transaction;

public class TransactionDao {
	// ADD TRANSACTION
	public boolean addTransaction(Transaction t) {
		String sql = "INSERT INTO transactions(user_id, type, amount, category, date, description) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, t.getUserId());
			ps.setString(2, t.getType());
			ps.setDouble(3, t.getAmount());
			ps.setString(4, t.getCategory());
			ps.setDate(5, t.getDate());
			ps.setString(6, t.getDescription());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// GET USER TRANSACTIONS
	public List<Transaction> getTransactions(int userId) {
		List<Transaction> list = new ArrayList<>();
		String sql = "SELECT * FROM transactions WHERE user_id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Transaction t = new Transaction();
				t.setId(rs.getInt("id"));
				t.setType(rs.getString("type"));
				t.setAmount(rs.getDouble("amount"));
				t.setCategory(rs.getString("category"));
				t.setDate(rs.getDate("date"));
				t.setDescription(rs.getString("description"));

				list.add(t);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// TOTAL INCOME
	public double getTotalIncome(int userId) {
		return getTotal(userId, "income");
	}

	// TOTAL EXPENSE
	public double getTotalExpense(int userId) {
		return getTotal(userId, "expense");
	}

	private double getTotal(int userId, String type) {
		String sql = "SELECT SUM(amount) FROM transactions WHERE user_id=? AND type=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ps.setString(2, type);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
