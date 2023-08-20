package org.mipams.jpeg360.config;

import org.mipams.jpeg360.services.Jpeg360ContentType;
import org.mipams.jpeg360.services.Jpeg360XmlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Jpeg360Config {

    @Bean
    public Jpeg360ContentType jlinkContentType() {
        return new Jpeg360ContentType();
    }

    // @Bean
    // public JlinkXmlGenerator schemaGenerator() {
    // return new JlinkXmlGenerator();
    // }

    @Bean
    public Jpeg360XmlValidator schemaValidator() {
        return new Jpeg360XmlValidator();
    }
}
