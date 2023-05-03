package org.mipams.jlink.config;

import org.mipams.jlink.services.JlinkContentType;
import org.mipams.jlink.services.JlinkValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JlinkConfig {

    @Bean
    public JlinkContentType jlinkContentType() {
        return new JlinkContentType();
    }

    @Bean
    public JlinkValidator schemaValidator() {
        return new JlinkValidator();
    }
}
