package org.mipams.jumbf.core.services;

import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.stereotype.Service;

@Service
public class ContiguousCodestreamBoxService extends SingleFormatBoxService<ContiguousCodestreamBox>
        implements ContentBoxService<ContiguousCodestreamBox> {

    @Override
    protected ContiguousCodestreamBox initializeBox() throws MipamsException {
        return new ContiguousCodestreamBox();
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.ContiguousCodestreamBox.getTypeId();
    }

    @Override
    public String serviceIsResponsibleForBoxType() {
        return BoxTypeEnum.ContiguousCodestreamBox.getType();
    }

    @Override
    protected String getExtension() {
        return "jpeg";
    }
}