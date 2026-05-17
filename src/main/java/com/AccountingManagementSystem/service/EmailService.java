package com.AccountingManagementSystem.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class EmailService {
	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final int SMTP_PORT = 465;
	private static final Properties MAIL_PROPERTIES = loadMailProperties();

	
	// This method sends an OTP email using Gmail SMTP.
	public boolean sendOtpEmail(String toEmail, String otp) {
		String subject = "Smart Accounting Password Reset OTP";
		String message = "Your Smart Accounting password reset OTP is " + otp
				+ ". This OTP will expire in 5 minutes. Do not share it with anyone.";

		return sendEmail(toEmail, subject, message);
	}

	public boolean sendPasswordResetEmail(String toEmail, String resetLink) {
		String subject = "Reset your Smart Accounting password";
		String message = "We received a request to reset your Smart Accounting password.\n\n"
				+ "Open this secure link to set a new password:\n" + resetLink + "\n\n"
				+ "This link expires in 1 hour and can be used only once. If you did not request this, you can ignore this email.";

		return sendEmail(toEmail, subject, message);
	}

	private boolean sendEmail(String toEmail, String subject, String message) {
		String gmailAddress = firstConfig("GMAIL_USERNAME", "GMAIL_ADDRESS", "gmail.username", "mail.username");
		String gmailPassword = firstConfig("GMAIL_APP_PASSWORD", "GMAIL_PASSWORD", "gmail.app.password", "mail.password");
		
		if (gmailAddress == null || gmailPassword == null) {
			System.err.println("Gmail SMTP credentials are missing.");
			return false;
		}

		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket(SMTP_HOST, SMTP_PORT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

			checkResponse(reader, 220);
			sendCommand(writer, reader, "EHLO localhost", 250);
			sendCommand(writer, reader, "AUTH LOGIN", 334);
			sendCommand(writer, reader, Base64.getEncoder().encodeToString(gmailAddress.getBytes(StandardCharsets.UTF_8)), 334);
			sendCommand(writer, reader, Base64.getEncoder().encodeToString(gmailPassword.getBytes(StandardCharsets.UTF_8)), 235);
			sendCommand(writer, reader, "MAIL FROM:<" + gmailAddress + ">", 250);
			sendCommand(writer, reader, "RCPT TO:<" + toEmail + ">", 250);
			sendCommand(writer, reader, "DATA", 354);

			writer.write("From: Smart Accounting <" + gmailAddress + ">\r\n");
			writer.write("To: " + toEmail + "\r\n");
			writer.write("Subject: " + subject + "\r\n");
			writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
			writer.write("\r\n");
			writer.write(message + "\r\n");
			writer.write(".\r\n");
			writer.flush();
			checkResponse(reader, 250);
			sendCommand(writer, reader, "QUIT", 221);
			socket.close();
			return true;
		} catch (Exception e) {
			System.err.println("Unable to send OTP email: " + e.getMessage());
			return false;
		}
	}

	private String firstConfig(String... keys) {
		for (String key : keys) {
			String value = getConfig(key);

			if (value != null && !value.trim().isEmpty()) {
				return stripWrappingQuotes(value.trim());
			}
		}

		return null;
	}

	private String stripWrappingQuotes(String value) {
		if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
			return value.substring(1, value.length() - 1);
		}

		return value;
	}

	// This method reads Gmail credentials from system properties, environment variables, or mail.properties.
	private String getConfig(String key) {
		String value = System.getProperty(key);

		if (value == null || value.trim().isEmpty()) {
			value = System.getenv(key);
		}

		if ((value == null || value.trim().isEmpty()) && MAIL_PROPERTIES != null) {
			value = MAIL_PROPERTIES.getProperty(key);
		}

		return value;
	}

	private static Properties loadMailProperties() {
		Properties properties = new Properties();

		try (InputStream input = EmailService.class.getClassLoader().getResourceAsStream("mail.properties")) {
			if (input != null) {
				properties.load(input);
			}
		} catch (Exception e) {
			System.err.println("Unable to load mail.properties: " + e.getMessage());
		}

		loadFileProperties(properties, "mail.properties");
		loadFileProperties(properties, ".env");

		return properties;
	}

	private static void loadFileProperties(Properties properties, String path) {
		try (InputStream input = new FileInputStream(path)) {
			properties.load(input);
		} catch (Exception e) {
			// Optional local config file. It is fine if it does not exist.
		}
	}

	// This helper sends one SMTP command and reads the response.
	private void sendCommand(BufferedWriter writer, BufferedReader reader, String command, int expectedCode) throws Exception {
		writer.write(command + "\r\n");
		writer.flush();
		checkResponse(reader, expectedCode);
	}

	// This helper checks one SMTP response block.
	private void checkResponse(BufferedReader reader, int expectedCode) throws Exception {
		String line;
		String lastLine = "";

		do {
			line = reader.readLine();
			if (line != null) {
				lastLine = line;
			}
		} while (line != null && line.length() > 3 && line.charAt(3) == '-');

		if (lastLine.length() < 3 || !lastLine.startsWith(String.valueOf(expectedCode))) {
			throw new Exception("SMTP error: " + lastLine);
		}
	}
}
