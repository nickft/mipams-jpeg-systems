package org.mipams.jumbf.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.mipams.jumbf.core")
public class JumbfApplication {
	private static final Logger logger = LoggerFactory.getLogger(JumbfApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JumbfApplication.class, args);
		logger.info("Application is up and running");
	}
}
