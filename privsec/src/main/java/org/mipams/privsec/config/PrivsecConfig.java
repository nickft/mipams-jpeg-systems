package org.mipams.privsec.config;

import org.mipams.privsec.services.boxes.ProtectionDescriptionBoxService;
import org.mipams.privsec.services.boxes.ReplacementDescriptionBoxService;
import org.mipams.privsec.services.boxes.replacement.AppReplacementHandler;
import org.mipams.privsec.services.boxes.replacement.BoxReplacementHandler;
import org.mipams.privsec.services.boxes.replacement.DataBoxHandlerFactory;
import org.mipams.privsec.services.boxes.replacement.FileReplacementHandler;
import org.mipams.privsec.services.boxes.replacement.ParamHandlerFactory;
import org.mipams.privsec.services.boxes.replacement.RoiReplacementHandler;
import org.mipams.privsec.services.content_types.ProtectionContentType;
import org.mipams.privsec.services.content_types.ReplacementContentType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrivsecConfig {

    @Bean
    public ProtectionContentType protectionContentType() {
        return new ProtectionContentType();
    }

    @Bean
    public ProtectionDescriptionBoxService protectionDescriptionBoxService() {
        return new ProtectionDescriptionBoxService();
    }

    @Bean
    public ReplacementContentType replacementContentType() {
        return new ReplacementContentType();
    }

    @Bean
    public ReplacementDescriptionBoxService replacementDescriptionBoxService() {
        return new ReplacementDescriptionBoxService();
    }

    @Bean
    public ParamHandlerFactory paramHandlerFactory() {
        return new ParamHandlerFactory();
    }

    @Bean
    public DataBoxHandlerFactory dataBoxHandlerFactory() {
        return new DataBoxHandlerFactory();
    }

    @Bean
    public BoxReplacementHandler boxReplacementHandler() {
        return new BoxReplacementHandler();
    }

    @Bean
    public AppReplacementHandler appReplacementHandler() {
        return new AppReplacementHandler();
    }

    @Bean
    public RoiReplacementHandler roiReplacementHandler() {
        return new RoiReplacementHandler();
    }

    @Bean
    public FileReplacementHandler fileReplacementHandler() {
        return new FileReplacementHandler();
    }
}
