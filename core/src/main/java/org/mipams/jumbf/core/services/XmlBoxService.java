package org.mipams.jumbf.core.services;

import org.mipams.jumbf.core.entities.XmlBox;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.stereotype.Service;

@Service
public class XmlBoxService extends SingleFormatBoxService<XmlBox> implements ContentBoxService<XmlBox> {

    @Override
    protected XmlBox initializeBox() throws MipamsException {
        return new XmlBox();
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.XmlBox.getTypeId();
    }

    @Override
    public String serviceIsResponsibleForBoxType() {
        return BoxTypeEnum.XmlBox.getType();
    }

    @Override
    protected String getExtension() {
        return "xml";
    }
}