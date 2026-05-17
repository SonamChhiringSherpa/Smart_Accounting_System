package com.AccountingManagementSystem.service;

import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CsrfService {
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final String SESSION_KEY = "csrfToken";

	// This method returns an existing CSRF token or creates a new one.
	public static String getToken(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String token = (String) session.getAttribute(SESSION_KEY);

		if (token == null) {
			byte[] bytes = new byte[32];
			RANDOM.nextBytes(bytes);
			token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
			session.setAttribute(SESSION_KEY, token);
		}

		return token;
	}

	// This method checks if the submitted CSRF token matches the session token.
	public static boolean isValid(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return false;
		}

		String sessionToken = (String) session.getAttribute(SESSION_KEY);
		String formToken = request.getParameter("csrfToken");

		return sessionToken != null && sessionToken.equals(formToken);
	}
}
