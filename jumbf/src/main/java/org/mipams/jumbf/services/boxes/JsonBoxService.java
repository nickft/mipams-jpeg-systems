package org.mipams.jumbf.services.boxes;

import org.springframework.stereotype.Service;

import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.ServiceMetadata;

@Service
public class JsonBoxService extends MemoryBoxService<JsonBox> {

    ServiceMetadata serviceMetadata = new ServiceMetadata(JsonBox.TYPE_ID,
            JsonBox.TYPE);

    @Override
    protected JsonBox initializeBox() {
        return new JsonBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }
}