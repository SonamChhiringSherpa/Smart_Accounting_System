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

// This filter stops users from opening protected pages without login.
@WebFilter({"/home", "/profile", "/update-profile", "/admin", "/update-user", "/delete-user", "/restore-user", "/add-transaction", "/transactions", "/delete-transaction", "/edit-transaction", "/reports", "/analytics"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    // This method runs before protected servlets.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String path = req.getServletPath();

        // If there is no session, send the user to login.
        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String user = (String) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        // Admin page is only for admin role.
        if (path.equals("/admin") || path.equals("/update-user") || path.equals("/delete-user") || path.equals("/restore-user")) {
            if ("admin".equals(role)) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(req.getContextPath() + "/login");
            }
        }
        else {
            // All other protected pages are for normal logged in users.
            if (user != null) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(req.getContextPath() + "/login");
            }
        }
    }

    @Override
    public void destroy() {}
}
