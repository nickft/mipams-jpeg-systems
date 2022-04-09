package org.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;

import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.BoxTypeEnum;

@Service
public class JsonBoxService extends SingleFormatBoxService<JsonBox> implements ContentBoxService<JsonBox> {

    @Override
    protected JsonBox initializeBox() throws MipamsException {
        return new JsonBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.JsonBox.getServiceMetadata();
    }

    @Override
    protected String getExtension() {
        return "json";
    }
}