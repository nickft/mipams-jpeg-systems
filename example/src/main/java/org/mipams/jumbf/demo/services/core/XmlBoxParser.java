package org.mipams.jumbf.demo.services.core;

import org.mipams.jumbf.core.entities.XmlBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.XmlBoxService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class XmlBoxParser extends SingleFormatParser<XmlBox> implements ContentBoxParser {

    @Autowired
    XmlBoxService xmlBoxService;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return xmlBoxService.getServiceMetadata();
    }

    @Override
    protected XmlBox initializeBox() {
        return new XmlBox();
    }
}
