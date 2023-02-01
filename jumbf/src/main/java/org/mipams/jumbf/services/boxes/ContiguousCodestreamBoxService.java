package org.mipams.jumbf.services.boxes;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.entities.ServiceMetadata;

import org.springframework.stereotype.Service;

@Service
public class ContiguousCodestreamBoxService extends FileBoxService<ContiguousCodestreamBox> {

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        ContiguousCodestreamBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
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