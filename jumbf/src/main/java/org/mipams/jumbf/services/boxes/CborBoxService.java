package org.mipams.jumbf.services.boxes;

import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.entities.CborBox;

import org.springframework.stereotype.Service;

@Service
public class CborBoxService extends MemoryBoxService<CborBox> {

    ServiceMetadata serviceMetadata = new ServiceMetadata(CborBox.TYPE_ID, CborBox.TYPE);

    @Override
    protected CborBox initializeBox() {
        return new CborBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }
}