package com.AccountingManagementSystem.controller;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * AuthFilter - Handles authentication & authorization
 */
@WebFilter({"/home", "/admin"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String path = req.getServletPath();

        // If no session → redirect to login
        if (session == null) {
            res.sendRedirect("login");
            return;
        }

        // Get session attributes
        String user = (String) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        // 🔐 HOME → only logged-in users
        if (path.equals("/home")) {
            if (user != null) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect("login");
            }
        }

        // 🔐 ADMIN → only admin allowed
        else if (path.equals("/admin")) {
            if ("admin".equals(role)) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect("login");
            }
        }
    }

    @Override
    public void destroy() {}
}