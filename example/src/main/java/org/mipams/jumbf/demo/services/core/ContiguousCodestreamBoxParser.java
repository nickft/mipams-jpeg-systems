package org.mipams.jumbf.demo.services.core;

import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.ContiguousCodestreamBoxService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class ContiguousCodestreamBoxParser extends SingleFormatParser<ContiguousCodestreamBox>
        implements ContentBoxParser {

    @Autowired
    ContiguousCodestreamBoxService ContiguousCodestreamBoxService;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return ContiguousCodestreamBoxService.getServiceMetadata();
    }

    @Override
    protected ContiguousCodestreamBox initializeBox() {
        return new ContiguousCodestreamBox();
    }
}
