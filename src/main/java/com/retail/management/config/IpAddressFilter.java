package com.retail.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class IpAddressFilter extends OncePerRequestFilter {

    private final List<String> allowedIps;

    public IpAddressFilter(String allowedIps) {
        this.allowedIps = Arrays.asList(allowedIps.split(","));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Retrieve the client IP from the X-Forwarded-For header, as used by Render
        String clientIp = request.getHeader("X-Forwarded-For");
        System.out.println("clientIp -> " + clientIp);
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
            System.out.println("request.getRemoteAddr -> " + request.getRemoteAddr());
        }

        if (clientIp != null && allowedIps.contains(clientIp)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied: Your IP address is not authorized.");
        }
    }
}
