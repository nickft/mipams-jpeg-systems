package org.mipams.jumbf.privacy_security.services.boxes;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.boxes.BmffBoxService;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.services.boxes.replacement.ParamHandlerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementDescriptionBoxService extends BmffBoxService<ReplacementDescriptionBox> {

    @Autowired
    ParamHandlerFactory paramHandlerFactory;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        ReplacementDescriptionBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected ReplacementDescriptionBox initializeBox() {
        return new ReplacementDescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected final void writeBmffPayloadToJumbfFile(ReplacementDescriptionBox box, FileOutputStream fileOutputStream)
            throws MipamsException {

        CoreUtils.writeIntAsSingleByteToOutputStream(box.getReplacementTypeId(), fileOutputStream);
        CoreUtils.writeIntAsSingleByteToOutputStream(box.getToggle(), fileOutputStream);

        box.getParamHandler().writeParamToBytes(fileOutputStream);

    }

    @Override
    protected final void populatePayloadFromJumbfFile(ReplacementDescriptionBox box, long availableBytesForBox,
            InputStream input) throws MipamsException {

        long remainingBytes = availableBytesForBox;

        int value = CoreUtils.readSingleByteAsIntFromInputStream(input);
        box.setReplacementTypeId(value);
        remainingBytes -= box.getReplacementTypeIdSize();

        value = CoreUtils.readSingleByteAsIntFromInputStream(input);
        box.setToggle(value);
        remainingBytes -= box.getToggleSize();

        ReplacementType replacementType = ReplacementType.getTypeFromId(box.getReplacementTypeId());

        ParamHandlerInterface paramHandler = paramHandlerFactory.getParamHandler(replacementType, remainingBytes);
        paramHandler.populateParamFromBytes(input);

        box.setParamHandler(paramHandler);
    }

}
