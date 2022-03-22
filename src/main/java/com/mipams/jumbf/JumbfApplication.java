package com.mipams.jumbf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class JumbfApplication {
	private static final Logger logger = LoggerFactory.getLogger(JumbfApplication.class);

	public static void main(String[] args) throws Exception{
		SpringApplication.run(JumbfApplication.class, args);
		logger.info("Application is up and running");        
    }
}
