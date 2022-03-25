package mipams.jumbf.provenance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(scanBasePackages = "mipams.jumbf.provenance" )
public class JumbfApplication {
	private static final Logger logger = LoggerFactory.getLogger(JumbfApplication.class);

	public static void main(String[] args) throws Exception{
		SpringApplication.run(JumbfApplication.class, args);
		logger.info("Application is up and running");        
    }
}
