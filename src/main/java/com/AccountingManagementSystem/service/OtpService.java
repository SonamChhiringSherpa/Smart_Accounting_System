package com.AccountingManagementSystem.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class OtpService {
	private static final SecureRandom RANDOM = new SecureRandom();

	// This method creates a secure 6 digit OTP.
	public static String generateOtp() {
		int number = RANDOM.nextInt(1_000_000);
		return String.format("%06d", number);
	}

	// This method hashes the OTP before storing it in the session.
	public static String hashOtp(String email, String otp) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String value = email.toLowerCase() + ":" + otp;
			byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			return "";
		}
	}

	// This method compares a typed OTP with the stored hash.
	public static boolean matches(String email, String typedOtp, String storedHash) {
		if (typedOtp == null || storedHash == null) {
			return false;
		}
		return hashOtp(email, typedOtp.trim()).equals(storedHash);
	}
}
