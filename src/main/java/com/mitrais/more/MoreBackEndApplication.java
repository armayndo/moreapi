package com.mitrais.more;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mitrais.more.config.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	FileStorageProperties.class
})
@EnableScheduling
public class MoreBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoreBackEndApplication.class, args);
	}

}

