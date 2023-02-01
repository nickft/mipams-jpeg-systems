package org.mipams.jumbf.services.boxes;

import org.mipams.jumbf.entities.ServiceMetadata;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.entities.CborBox;

import org.springframework.stereotype.Service;

@Service
public class CborBoxService extends MemoryBoxService<CborBox> {

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
}