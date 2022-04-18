package org.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;

@Service
public class JsonBoxService extends SingleFormatBoxService<JsonBox> implements ContentBoxService<JsonBox> {

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        JsonBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType(), box.getContentTypeUUID());
    }

    @Override
    protected JsonBox initializeBox() {
        return new JsonBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected String getExtension() {
        return "json";
    }
}