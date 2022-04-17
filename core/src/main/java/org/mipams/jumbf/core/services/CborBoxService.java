package org.mipams.jumbf.core.services;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.entities.CborBox;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.stereotype.Service;

@Service
public class CborBoxService extends SingleFormatBoxService<CborBox> implements ContentBoxService<CborBox> {

    @Override
    protected CborBox initializeBox() throws MipamsException {
        return new CborBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.CborBox.getServiceMetadata();
    }

    @Override
    protected String getExtension() {
        return "cbor";
    }
}