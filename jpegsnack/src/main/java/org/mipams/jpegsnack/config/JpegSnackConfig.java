package org.mipams.jpegsnack.config;

import org.mipams.jpegsnack.services.boxes.InstructionSetBoxService;
import org.mipams.jpegsnack.services.boxes.JpegSnackDescriptionBoxService;
import org.mipams.jpegsnack.services.boxes.ObjectMetadataBoxService;
import org.mipams.jpegsnack.services.content_types.JpegSnackContentType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpegSnackConfig {

    @Bean
    public JpegSnackContentType jpegSnackContentType() {
        return new JpegSnackContentType();
    }

    @Bean
    public JpegSnackDescriptionBoxService jpegSnackDescriptionBoxService() {
        return new JpegSnackDescriptionBoxService();
    }

    @Bean
    public InstructionSetBoxService instructionSetBoxService() {
        return new InstructionSetBoxService();
    }

    @Bean
    public ObjectMetadataBoxService objectMetadataBoxService() {
        return new ObjectMetadataBoxService();
    }
}
