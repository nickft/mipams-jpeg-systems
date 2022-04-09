package org.mipams.jumbf.privacy_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(scanBasePackages = {"org.mipams.jumbf.core", "org.mipams.jumbf.privacy_security"})
public class PrivacySecurityApplication {
	private static final Logger logger = LoggerFactory.getLogger(PrivacySecurityApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(PrivacySecurityApplication.class, args);
		logger.info("PrivSec is up and running");
	}
}
