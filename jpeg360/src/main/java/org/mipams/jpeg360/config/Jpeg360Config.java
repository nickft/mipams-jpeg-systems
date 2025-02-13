package org.mipams.jpeg360.config;

import org.mipams.jpeg360.services.Jpeg360ContentType;
import org.mipams.jpeg360.services.Jpeg360XmlGenerator;
import org.mipams.jpeg360.services.Jpeg360XmlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Jpeg360Config {

    @Bean
    public Jpeg360ContentType jpeg360ContentType() {
        return new Jpeg360ContentType();
    }

    @Bean
    public Jpeg360XmlGenerator schemaGenerator() {
        return new Jpeg360XmlGenerator();
    }

    @Bean
    public Jpeg360XmlValidator schemaValidator() {
        return new Jpeg360XmlValidator();
    }
}
