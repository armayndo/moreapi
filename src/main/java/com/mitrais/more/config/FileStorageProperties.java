package com.mitrais.more.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * The Class FileStorageProperties.
 * Define location to store a file on server.
 */
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageProperties {
	
	/** The upload dir. */
	private String uploadDir;
}
