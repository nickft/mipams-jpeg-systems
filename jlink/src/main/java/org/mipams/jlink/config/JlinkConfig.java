package org.mipams.jlink.config;

import org.mipams.jlink.services.JlinkContentType;
import org.mipams.jlink.services.JlinkXmlGenerator;
import org.mipams.jlink.services.JlinkXmlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JlinkConfig {

    @Bean
    public JlinkContentType jlinkContentType() {
        return new JlinkContentType();
    }

    @Bean
    public JlinkXmlGenerator schemaGenerator() {
        return new JlinkXmlGenerator();
    }

    @Bean
    public JlinkXmlValidator schemaValidator() {
        return new JlinkXmlValidator();
    }
}
