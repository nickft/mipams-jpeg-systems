package org.mipams.jumbf.services.boxes;

import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.entities.XmlBox;

import org.springframework.stereotype.Service;

@Service
public class XmlBoxService extends MemoryBoxService<XmlBox> {

    ServiceMetadata serviceMetadata = new ServiceMetadata(XmlBox.TYPE_ID,
            XmlBox.TYPE);

    @Override
    protected XmlBox initializeBox() {
        return new XmlBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }
}