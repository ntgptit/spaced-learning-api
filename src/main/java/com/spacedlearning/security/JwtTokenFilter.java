package com.spacedlearning.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter to validate JWT tokens for each request
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		try {
			final String authHeader = request.getHeader("Authorization");
			final String jwt;
			final String userEmail;

			// Skip filter if Authorization header is missing or malformed
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				log.debug("No JWT token found in request headers");
				filterChain.doFilter(request, response);
				return;
			}

			// Extract JWT token from header
			jwt = authHeader.substring(7);

			// Extract user email from token
			userEmail = jwtTokenProvider.getUsernameFromToken(jwt);

			// Log debug information
			log.debug("Processing JWT token for user: {}", userEmail);

			// Validate token and set authentication if valid
			if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				if (jwtTokenProvider.validateToken(jwt)) {
					final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

					// Create authenticated token
					final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					// Add request details to authentication token
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					// Set authentication in security context
					SecurityContextHolder.getContext().setAuthentication(authToken);

					log.debug("Authentication set in SecurityContext for user: {}", userEmail);
				} else {
					log.debug("JWT token validation failed for user: {}", userEmail);
				}
			}

			filterChain.doFilter(request, response);
		} catch (final Exception e) {
			log.error("Failed to process JWT authentication: {}", e.getMessage(), e);
			filterChain.doFilter(request, response);
		}
	}
}