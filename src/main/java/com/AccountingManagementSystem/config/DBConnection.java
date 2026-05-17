package com.AccountingManagementSystem.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	private static final String URL = "jdbc:mysql://localhost:3306/accounting_system";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	// This method creates and returns one database connection.
	public static Connection getConnection() {
		Connection conn = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (Exception e) {
			System.err.println("Database connection failed: " + e.getMessage());
		}

		return conn;
	}
}
