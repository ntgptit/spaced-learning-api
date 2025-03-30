package com.spacedlearning.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.exception.ApiError;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component to handle unauthorized access attempts in a RESTful manner
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		log.error("Unauthorized error: {}", authException.getMessage());

		// Set the response status and content type
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		// Create the error response using your existing ApiError class
		final ApiError errorResponse = ApiError.builder().timestamp(LocalDateTime.now())
				.status(HttpServletResponse.SC_UNAUTHORIZED).error("Unauthorized")
				.message("Unauthorized: You need to be authenticated to access this resource")
				.path(request.getRequestURI()).build();

		// Write the error response as JSON
		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}
}