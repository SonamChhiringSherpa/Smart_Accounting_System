package com.AccountingManagementSystem.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

// Standard BCrypt library

import com.AccountingManagementSystem.config.DBConnection;
import com.AccountingManagementSystem.model.User;

public class UserDao {

	/**
	 * Registers a new user into the database. Uses BCrypt to hash the password
	 * before saving for security.
	 */
	public boolean registerUser(User user) {
		String query = "INSERT INTO users (username, password, name) VALUES (?, ?, ?)";
		// Hashing the password with a salt
		String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, hashedPassword); // Store the hashed password
			pstmt.setString(3, user.getName());

			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error during registration: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Validates a user's credentials against the database. Fetches the user by
	 *  and compares the provided password with the stored hash.
	 */
	public User validateUser(String username, String password) {
		String query = "SELECT id, username, password, name FROM users WHERE username = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String storedHash = rs.getString("password");
				// Use BCrypt to check if the password matches the hash
				if (BCrypt.checkpw(password, storedHash)) {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getString("username"));
					user.setName(rs.getString("name"));
					return user;
				}
			}
		} catch (SQLException e) {
			System.err.println("Error during validation: " + e.getMessage());
		}
		return null; // Return null if user not found or password incorrect
	}
}
