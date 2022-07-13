package org.mipams.jumbf.jlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(scanBasePackages = { "org.mipams.jumbf.core", "org.mipams.jumbf.jlink" })
public class JlinkApplication {
	private static final Logger logger = LoggerFactory.getLogger(JlinkApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JlinkApplication.class, args);
		logger.info("JLINK module is up and running");
	}
}
