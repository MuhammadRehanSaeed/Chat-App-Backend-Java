package com.rehancode.chatapp.JWT;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rehancode.chatapp.UserDetailsService.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class JwtFilter extends OncePerRequestFilter {
       private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);


    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
           String path = request.getRequestURI();
    
     // SKIP WebSocket handshake paths
        if (path.startsWith("/ws/")) {
              logger.debug("JWT filter skipped for WS path: {}", path);
            filterChain.doFilter(request, response);
            System.out.println("JWT filter skipped for WS path: " + path);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                  logger.info("JWT validated successfully | Username: {} | Path: {}", username, path);
            }
            else {
                    logger.warn("JWT validation failed | Username: {} | Path: {}", username, path);
                }
        }

    
        filterChain.doFilter(request, response);
    }
}
