package org.mipams.jumbf.services.boxes;

import org.mipams.jumbf.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.entities.ServiceMetadata;

import org.springframework.stereotype.Service;

@Service
public class ContiguousCodestreamBoxService extends FileBoxService<ContiguousCodestreamBox> {

    ServiceMetadata serviceMetadata = new ServiceMetadata(ContiguousCodestreamBox.TYPE_ID,
            ContiguousCodestreamBox.TYPE);

    @Override
    protected ContiguousCodestreamBox initializeBox() {
        return new ContiguousCodestreamBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected String getExtension() {
        return "jpeg";
    }
}