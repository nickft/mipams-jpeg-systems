package org.mipams.jumbf.core.services.boxes;

import org.mipams.jumbf.core.entities.ServiceMetadata;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.CborBox;

import org.springframework.stereotype.Service;

@Service
public class CborBoxService extends SingleFormatBoxService<CborBox> {

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        CborBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected CborBox initializeBox() {
        return new CborBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected String getExtension() {
        return "cbor";
    }
}