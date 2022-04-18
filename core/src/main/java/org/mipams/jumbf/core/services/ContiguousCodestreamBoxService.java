package org.mipams.jumbf.core.services;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;

import org.springframework.stereotype.Service;

@Service
public class ContiguousCodestreamBoxService extends SingleFormatBoxService<ContiguousCodestreamBox>
        implements ContentBoxService<ContiguousCodestreamBox> {

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        ContiguousCodestreamBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType(), box.getContentTypeUUID());
    }

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