package com.mitrais.more.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
/*
 * This class is used to create custom authentication entry point
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

	/*
	 * This method commence the authentication scheme
	 * add logger error with message to show error in console
	 * send error with message and SC_UNAUTHORIZED for the error status code
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException e) throws IOException, ServletException {
		 logger.error("Responding with unauthorized error. Message - {}", e.getMessage());
	     response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
	}
}
