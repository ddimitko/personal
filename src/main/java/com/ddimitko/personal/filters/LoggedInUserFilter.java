package com.ddimitko.personal.filters;

import com.ddimitko.personal.tools.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoggedInUserFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public LoggedInUserFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Check if the request is for login or signup
        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/sign-up")) {

            // Check if the user is already authenticated
            final String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                String userTag = jwtTokenUtil.getUserTagFromToken(token);

                if (userTag != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                    // If user is authenticated, return a 403 Forbidden response
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("You are already logged in.");
                    return;
                }
            }
        }

        // If the user is not authenticated, continue with the filter chain
        filterChain.doFilter(request, response);
    }
}

