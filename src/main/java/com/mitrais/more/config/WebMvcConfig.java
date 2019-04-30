package com.mitrais.more.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class used to allow cross origin resource sharing.
 * So other platform can access this application
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	/**
	 * The account max age
	 * You want to set
	 */
	private final long MAX_AGE_SECS = 3600;

	/**
	 * Sets CORS properties
	 * @param registry the registry being added to
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		        .allowedOrigins("*")
		        .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
		        .maxAge(MAX_AGE_SECS);
	}
	
}
