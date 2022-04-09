package org.mipams.jumbf.core.services;

import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
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
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.ContiguousCodestreamBox.getServiceMetadata();
    }

    @Override
    protected String getExtension() {
        return "jpeg";
    }
}