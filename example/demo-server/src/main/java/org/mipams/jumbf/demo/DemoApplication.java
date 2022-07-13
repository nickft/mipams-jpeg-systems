package org.mipams.jumbf.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(scanBasePackages = { "org.mipams.jumbf.core", "org.mipams.jumbf.privacy_security",
		"org.mipams.jumbf.jlink", "org.mipams.jumbf.demo" })
public class DemoApplication {
	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoApplication.class, args);
		logger.info("Demo application is up and running");
	}
}
