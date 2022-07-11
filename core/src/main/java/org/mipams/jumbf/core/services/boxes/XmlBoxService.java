package org.mipams.jumbf.core.services.boxes;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.XmlBox;

import org.springframework.stereotype.Service;

@Service
public class XmlBoxService extends MemoryBoxService<XmlBox> {

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        XmlBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected XmlBox initializeBox() {
        return new XmlBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }
}