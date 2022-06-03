package org.mipams.jumbf.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JumbfApplication {
	private static final Logger logger = LoggerFactory.getLogger(JumbfApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(JumbfApplication.class, args);
		logger.info("JUMBF core module is up and running");
	}
}
