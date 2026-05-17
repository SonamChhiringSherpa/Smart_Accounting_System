package com.AccountingManagementSystem.service;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {

	// This method creates a secure BCrypt hash for a password.
	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}

	// This method checks both new BCrypt passwords and old simple hash passwords.
	public static boolean checkPassword(String password, String storedPassword) {
		if (storedPassword == null) {
			return false;
		}

		if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
			return BCrypt.checkpw(password, storedPassword);
		}

		return storedPassword.equals(Integer.toHexString(password.hashCode()));
	}

	// This method checks if a password follows strong password rules.
	public static boolean isStrongPassword(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}

		boolean hasUppercase = false;
		boolean hasLowercase = false;
		boolean hasNumber = false;
		boolean hasSpecial = false;

		for (int i = 0; i < password.length(); i++) {
			char ch = password.charAt(i);

			if (Character.isUpperCase(ch)) {
				hasUppercase = true;
			} else if (Character.isLowerCase(ch)) {
				hasLowercase = true;
			} else if (Character.isDigit(ch)) {
				hasNumber = true;
			} else {
				hasSpecial = true;
			}
		}

		return hasUppercase && hasLowercase && hasNumber && hasSpecial;
	}
}
