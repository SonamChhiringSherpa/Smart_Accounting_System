package com.AccountingManagementSystem.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.AccountingManagementSystem.config.DBConnection;
import com.AccountingManagementSystem.model.User;

public class UserDao {
	private static final String CREATE_DELETED_USERS_TABLE = "CREATE TABLE IF NOT EXISTS deleted_users ("
			+ "deleted_record_id INT AUTO_INCREMENT PRIMARY KEY, "
			+ "original_user_id INT NOT NULL, "
			+ "full_name VARCHAR(100) NOT NULL, "
			+ "username VARCHAR(100) NOT NULL, "
			+ "email VARCHAR(150) NOT NULL, "
			+ "password VARCHAR(255) NOT NULL, "
			+ "deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
			+ ")";

	private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

	// This method loads all normal users for the admin dashboard.
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		String sql = "SELECT id, full_name, username, email, password FROM users ORDER BY id DESC";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				users.add(mapUser(rs));
			}
		} catch (Exception e) {
			System.err.println("Unable to load users: " + e.getMessage());
		}
		return users;
	}

	// This method finds one user record by id.
	public User getUserById(int userId) {
		String sql = "SELECT id, full_name, username, email, password FROM users WHERE id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return mapUser(rs);
			}
		} catch (Exception e) {
			System.err.println("Unable to find user: " + e.getMessage());
		}
		return null;
	}

	public User getUserByLogin(String login) {
		String sql = "SELECT id, full_name, username, email, password FROM users WHERE username=? OR LOWER(email)=LOWER(?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, login);
			ps.setString(2, login);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return mapUser(rs);
			}
		} catch (Exception e) {
			System.err.println("Unable to find login user: " + e.getMessage());
		}
		return null;
	}

	// This method loads deleted users in FIFO queue order for the admin dashboard.
	public List<User> getDeletedUsersQueue() {
		List<User> users = new ArrayList<>();
		String sql = "SELECT deleted_record_id, original_user_id, full_name, username, email, password, deleted_at "
				+ "FROM deleted_users ORDER BY deleted_at ASC, deleted_record_id ASC";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = prepareWithDeletedUsersTable(conn, sql)) {
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				User user = mapDeletedUser(rs);
				users.add(user);
			}
		} catch (Exception e) {
			System.err.println("Unable to load deleted users: " + e.getMessage());
		}
		return users;
	}

	// This method finds one user by email for forgot password.
	public User getUserByEmail(String email) {
		String sql = "SELECT id, full_name, username, email, password FROM users WHERE LOWER(email)=LOWER(?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return mapUser(rs);
			}
		} catch (Exception e) {
			System.err.println("Unable to find user by email: " + e.getMessage());
		}
		return null;
	}

	public boolean isUsernameOrEmailTaken(String username, String email, int exceptUserId) {
		String sql = "SELECT COUNT(*) FROM users WHERE id<>? AND (username=? OR LOWER(email)=LOWER(?))";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, exceptUserId);
			ps.setString(2, username);
			ps.setString(3, email);
			ResultSet rs = ps.executeQuery();

			return rs.next() && rs.getInt(1) > 0;
		} catch (Exception e) {
			System.err.println("Unable to check duplicate user: " + e.getMessage());
		}
		return true;
	}

	// This method updates only the password for a user email.
	public boolean updatePasswordByEmail(String email, String hashedPassword) {
		String sql = "UPDATE users SET password=?, failed_login_attempts=0, lock_until=NULL, reset_token_hash=NULL, reset_token_expires_at=NULL WHERE LOWER(email)=LOWER(?)";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, hashedPassword);
			ps.setString(2, email);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to update password: " + e.getMessage());
		}
		return false;
	}

	public boolean updateCredentials(int userId, String username, String email, String hashedPassword) {
		boolean changesPassword = hashedPassword != null && !hashedPassword.trim().isEmpty();
		String sql = changesPassword
				? "UPDATE users SET username=?, email=?, password=? WHERE id=?"
				: "UPDATE users SET username=?, email=? WHERE id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			ps.setString(2, email);

			if (changesPassword) {
				ps.setString(3, hashedPassword);
				ps.setInt(4, userId);
			} else {
				ps.setInt(3, userId);
			}

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to update credentials: " + e.getMessage());
		}
		return false;
	}

	public int getFailedLoginAttempts(int userId) {
		String sql = "SELECT failed_login_attempts FROM users WHERE id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("failed_login_attempts");
			}
		} catch (Exception e) {
			System.err.println("Unable to load failed attempts: " + e.getMessage());
		}
		return 0;
	}

	public Timestamp getLockUntil(int userId) {
		String sql = "SELECT lock_until FROM users WHERE id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getTimestamp("lock_until");
			}
		} catch (Exception e) {
			System.err.println("Unable to load lock timestamp: " + e.getMessage());
		}
		return null;
	}

	public boolean recordFailedLogin(int userId, long lockoutMillis) {
		int failedAttempts = getFailedLoginAttempts(userId) + 1;
		Timestamp lockUntil = failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS
				? new Timestamp(System.currentTimeMillis() + lockoutMillis)
				: null;
		String sql = "UPDATE users SET failed_login_attempts=?, lock_until=? WHERE id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, failedAttempts);
			ps.setTimestamp(2, lockUntil);
			ps.setInt(3, userId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to record failed login: " + e.getMessage());
		}
		return false;
	}

	public boolean clearLoginLockout(int userId) {
		String sql = "UPDATE users SET failed_login_attempts=0, lock_until=NULL WHERE id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to clear login lockout: " + e.getMessage());
		}
		return false;
	}

	public boolean storePasswordResetToken(int userId, String tokenHash, Timestamp expiresAt) {
		String sql = "UPDATE users SET reset_token_hash=?, reset_token_expires_at=? WHERE id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, tokenHash);
			ps.setTimestamp(2, expiresAt);
			ps.setInt(3, userId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to store reset token: " + e.getMessage());
		}
		return false;
	}

	public User getUserByValidResetToken(String tokenHash) {
		String sql = "SELECT id, full_name, username, email, password FROM users "
				+ "WHERE reset_token_hash=? AND reset_token_expires_at > CURRENT_TIMESTAMP";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, tokenHash);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return mapUser(rs);
			}
		} catch (Exception e) {
			System.err.println("Unable to find reset token user: " + e.getMessage());
		}
		return null;
	}

	public boolean clearPasswordResetToken(int userId) {
		String sql = "UPDATE users SET reset_token_hash=NULL, reset_token_expires_at=NULL WHERE id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureUserSecurityColumns(conn);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to clear reset token: " + e.getMessage());
		}
		return false;
	}

	// This method updates a user's full name, username, email, and password hash.
	public boolean updateUser(User user) {
		String sql = "UPDATE users SET full_name=?, username=?, email=?, password=? WHERE id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, user.getFullName());
			ps.setString(2, user.getUsername());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPassword());
			ps.setInt(5, user.getId());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			System.err.println("Unable to update user: " + e.getMessage());
		}
		return false;
	}

	// This method removes a user account.
	public boolean deleteUser(int userId) {
		String insertDeletedSql = "INSERT INTO deleted_users(original_user_id, full_name, username, email, password) "
				+ "SELECT id, full_name, username, email, password FROM users WHERE id=?";
		String deleteTransactionsSql = "DELETE FROM transactions WHERE user_id=?";
		String deleteUserSql = "DELETE FROM users WHERE id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureDeletedUsersTable(conn);
			conn.setAutoCommit(false);

			PreparedStatement deletedPs = conn.prepareStatement(insertDeletedSql);
			deletedPs.setInt(1, userId);
			int copiedRows = deletedPs.executeUpdate();

			if (copiedRows == 0) {
				conn.rollback();
				return false;
			}

			PreparedStatement transactionPs = conn.prepareStatement(deleteTransactionsSql);
			transactionPs.setInt(1, userId);
			transactionPs.executeUpdate();

			PreparedStatement userPs = conn.prepareStatement(deleteUserSql);
			userPs.setInt(1, userId);
			boolean deleted = userPs.executeUpdate() > 0;

			if (deleted) {
				conn.commit();
			} else {
				conn.rollback();
			}

			return deleted;
		} catch (Exception e) {
			System.err.println("Unable to delete user: " + e.getMessage());
		}
		return false;
	}

	// This method restores one deleted user from the queue back to active users.
	public boolean restoreDeletedUser(int deletedRecordId) {
		String findDeletedSql = "SELECT deleted_record_id, original_user_id, full_name, username, email, password, deleted_at "
				+ "FROM deleted_users WHERE deleted_record_id=?";
		String conflictSql = "SELECT COUNT(*) FROM users WHERE username=? OR email=?";
		String restoreSql = "INSERT INTO users(id, full_name, username, email, password) VALUES (?, ?, ?, ?, ?)";
		String removeDeletedSql = "DELETE FROM deleted_users WHERE deleted_record_id=?";

		try (Connection conn = DBConnection.getConnection()) {
			ensureDeletedUsersTable(conn);
			conn.setAutoCommit(false);

			User deletedUser = null;
			PreparedStatement findPs = conn.prepareStatement(findDeletedSql);
			findPs.setInt(1, deletedRecordId);
			ResultSet rs = findPs.executeQuery();

			if (rs.next()) {
				deletedUser = mapDeletedUser(rs);
			}

			if (deletedUser == null) {
				conn.rollback();
				return false;
			}

			PreparedStatement conflictPs = conn.prepareStatement(conflictSql);
			conflictPs.setString(1, deletedUser.getUsername());
			conflictPs.setString(2, deletedUser.getEmail());
			ResultSet conflictRs = conflictPs.executeQuery();

			if (conflictRs.next() && conflictRs.getInt(1) > 0) {
				conn.rollback();
				return false;
			}

			PreparedStatement restorePs = conn.prepareStatement(restoreSql);
			restorePs.setInt(1, deletedUser.getId());
			restorePs.setString(2, deletedUser.getFullName());
			restorePs.setString(3, deletedUser.getUsername());
			restorePs.setString(4, deletedUser.getEmail());
			restorePs.setString(5, deletedUser.getPassword());

			if (restorePs.executeUpdate() == 0) {
				conn.rollback();
				return false;
			}

			PreparedStatement removePs = conn.prepareStatement(removeDeletedSql);
			removePs.setInt(1, deletedRecordId);

			if (removePs.executeUpdate() == 0) {
				conn.rollback();
				return false;
			}

			conn.commit();
			return true;
		} catch (Exception e) {
			System.err.println("Unable to restore deleted user: " + e.getMessage());
		}
		return false;
	}

	// This helper converts one database row into a User object.
	private User mapUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setFullName(rs.getString("full_name"));
		user.setUsername(rs.getString("username"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		return user;
	}

	private User mapDeletedUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setDeletedRecordId(rs.getInt("deleted_record_id"));
		user.setId(rs.getInt("original_user_id"));
		user.setFullName(rs.getString("full_name"));
		user.setUsername(rs.getString("username"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setDeletedAt(String.valueOf(rs.getTimestamp("deleted_at")));
		return user;
	}

	private PreparedStatement prepareWithDeletedUsersTable(Connection conn, String sql) throws SQLException {
		ensureDeletedUsersTable(conn);
		return conn.prepareStatement(sql);
	}

	private void ensureDeletedUsersTable(Connection conn) throws SQLException {
		try (PreparedStatement ps = conn.prepareStatement(CREATE_DELETED_USERS_TABLE)) {
			ps.executeUpdate();
		}
	}

	private void ensureUserSecurityColumns(Connection conn) throws SQLException {
		addColumnIfMissing(conn, "users", "failed_login_attempts", "INT NOT NULL DEFAULT 0");
		addColumnIfMissing(conn, "users", "lock_until", "TIMESTAMP NULL");
		addColumnIfMissing(conn, "users", "reset_token_hash", "VARCHAR(128) NULL");
		addColumnIfMissing(conn, "users", "reset_token_expires_at", "TIMESTAMP NULL");
	}

	private void addColumnIfMissing(Connection conn, String tableName, String columnName, String columnDefinition)
			throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();

		try (ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, columnName)) {
			if (rs.next()) {
				return;
			}
		}

		try (PreparedStatement ps = conn.prepareStatement(
				"ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition)) {
			ps.executeUpdate();
		}
	}
}
