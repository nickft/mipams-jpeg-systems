package org.mipams.jumbf.demo.services.core;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.BinaryDataBoxService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class BinaryDataBoxParser extends SingleFormatParser<BinaryDataBox> {

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return binaryDataBoxService.getServiceMetadata();
    }

    @Override
    protected BinaryDataBox initializeBox() {
        return new BinaryDataBox();
    }
}
