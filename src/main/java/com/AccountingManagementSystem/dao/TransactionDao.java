package com.AccountingManagementSystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.AccountingManagementSystem.config.DBConnection;
import com.AccountingManagementSystem.model.Transaction;

public class TransactionDao {
	// This method saves a new transaction in the database.
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
			System.err.println("Unable to add transaction: " + e.getMessage());
		}
		return false;
	}

	// This method loads all transactions for one user.
	public List<Transaction> getTransactions(int userId) {
		return getTransactions(userId, "", "all", "date_desc");
	}

	// This method loads transactions with searching, filtering, and sorting.
	public List<Transaction> getTransactions(int userId, String searchText, String typeFilter, String sortBy) {
		List<Transaction> list = new ArrayList<>();
		String sql = "SELECT * FROM transactions WHERE user_id=?";

		if (searchText != null && !searchText.trim().isEmpty()) {
			sql += " AND (category LIKE ? OR description LIKE ? OR type LIKE ? OR date LIKE ?)";
		}

		if ("income".equals(typeFilter) || "expense".equals(typeFilter)) {
			sql += " AND type=?";
		}

		sql += " " + getSortSql(sortBy);

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			int index = 1;
			ps.setInt(index++, userId);

			if (searchText != null && !searchText.trim().isEmpty()) {
				String searchPattern = "%" + searchText.trim() + "%";
				ps.setString(index++, searchPattern);
				ps.setString(index++, searchPattern);
				ps.setString(index++, searchPattern);
				ps.setString(index++, searchPattern);
			}

			if ("income".equals(typeFilter) || "expense".equals(typeFilter)) {
				ps.setString(index++, typeFilter);
			}

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(mapTransaction(rs));
			}

		} catch (Exception e) {
			System.err.println("Unable to load transactions: " + e.getMessage());
		}
		return list;
	}

	// This method loads the latest few transactions for the dashboard.
	public List<Transaction> getRecentTransactions(int userId, int limit) {
		List<Transaction> list = new ArrayList<>();
		String sql = "SELECT * FROM transactions WHERE user_id=? ORDER BY date DESC, id DESC LIMIT ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setInt(2, limit);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(mapTransaction(rs));
			}
		} catch (Exception e) {
			System.err.println("Unable to load recent transactions: " + e.getMessage());
		}
		return list;
	}

	// This method loads one transaction before showing the edit form.
	public Transaction getTransactionById(int transactionId, int userId) {
		String sql = "SELECT * FROM transactions WHERE id=? AND user_id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, transactionId);
			ps.setInt(2, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return mapTransaction(rs);
			}
		} catch (Exception e) {
			System.err.println("Unable to find transaction: " + e.getMessage());
		}
		return null;
	}

	// This method updates an existing transaction.
	public boolean updateTransaction(Transaction t) {
		String sql = "UPDATE transactions SET type=?, amount=?, category=?, date=?, description=? WHERE id=? AND user_id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, t.getType());
			ps.setDouble(2, t.getAmount());
			ps.setString(3, t.getCategory());
			ps.setDate(4, t.getDate());
			ps.setString(5, t.getDescription());
			ps.setInt(6, t.getId());
			ps.setInt(7, t.getUserId());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to update transaction: " + e.getMessage());
		}
		return false;
	}

	// This method deletes one transaction that belongs to the logged in user.
	public boolean deleteTransaction(int transactionId, int userId) {
		String sql = "DELETE FROM transactions WHERE id=? AND user_id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, transactionId);
			ps.setInt(2, userId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to delete transaction: " + e.getMessage());
		}
		return false;
	}

	// This method counts a user's transactions for the report page.
	public int getTransactionCount(int userId) {
		String sql = "SELECT COUNT(*) FROM transactions WHERE user_id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			System.err.println("Unable to count transactions: " + e.getMessage());
		}
		return 0;
	}

	// This method calculates all income for one user.
	public double getTotalIncome(int userId) {
		return getTotal(userId, "income");
	}

	// This method calculates all expense for one user.
	public double getTotalExpense(int userId) {
		return getTotal(userId, "expense");
	}

	// This helper is used by total income and total expense.
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
			System.err.println("Unable to calculate total: " + e.getMessage());
		}
		return 0;
	}

	// This helper converts one database row into a Transaction object.
	private Transaction mapTransaction(ResultSet rs) throws SQLException {
		Transaction t = new Transaction();
		t.setId(rs.getInt("id"));
		t.setUserId(rs.getInt("user_id"));
		t.setType(rs.getString("type"));
		t.setAmount(rs.getDouble("amount"));
		t.setCategory(rs.getString("category"));
		t.setDate(rs.getDate("date"));
		t.setDescription(rs.getString("description"));
		return t;
	}

	// This helper keeps sorting safe by only allowing known columns.
	private String getSortSql(String sortBy) {
		if ("date_asc".equals(sortBy)) {
			return "ORDER BY date ASC, id ASC";
		} else if ("amount_desc".equals(sortBy)) {
			return "ORDER BY amount DESC, id DESC";
		} else if ("amount_asc".equals(sortBy)) {
			return "ORDER BY amount ASC, id ASC";
		} else if ("category_asc".equals(sortBy)) {
			return "ORDER BY category ASC, id DESC";
		} else if ("type_asc".equals(sortBy)) {
			return "ORDER BY type ASC, id DESC";
		}
		return "ORDER BY date DESC, id DESC";
	}
}
