package org.mipams.jumbf.demo.services.core;

import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.JsonBoxService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class JsonBoxParser extends SingleFormatParser<JsonBox> implements ContentBoxParser {

    @Autowired
    JsonBoxService jsonBoxService;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return jsonBoxService.getServiceMetadata();
    }

    @Override
    protected JsonBox initializeBox() {
        return new JsonBox();
    }
}
