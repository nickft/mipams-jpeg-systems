package org.mipams.jumbf.demo.services.core;

import org.mipams.jumbf.core.entities.CborBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.CborBoxService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class CborBoxParser extends SingleFormatParser<CborBox> implements ContentBoxParser {

    @Autowired
    CborBoxService CborBoxService;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return CborBoxService.getServiceMetadata();
    }

    @Override
    protected CborBox initializeBox() {
        return new CborBox();
    }
}
