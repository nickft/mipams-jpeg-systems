package org.mipams.jumbf.privacy_security.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.BmffBoxService;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.services.replacement.ParamHandlerFactory;

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

        try {
            fileOutputStream.write(CoreUtils.convertIntToSingleElementByteArray(box.getReplacementTypeId()));
            fileOutputStream.write(CoreUtils.convertIntToSingleElementByteArray(box.getToggle()));

            box.getParamHandler().writeParamToBytes(fileOutputStream);
        } catch (IOException e) {
            throw new MipamsException("Could not write to file.", e);
        }

    }

    @Override
    protected final void populatePayloadFromJumbfFile(ReplacementDescriptionBox box, long availableBytesForBox,
            InputStream input) throws MipamsException {

        int value = CoreUtils.readSingleByteAsIntFromInputStream(input);
        box.setReplacementTypeId(value);

        value = CoreUtils.readSingleByteAsIntFromInputStream(input);
        box.setToggle(value);

        ReplacementType replacementType = ReplacementType.getTypeFromId(box.getReplacementTypeId());
        ParamHandlerInterface paramHandler = paramHandlerFactory.getParamHandler(replacementType);
        paramHandler.populateParamFromBytes(input);

        box.setParamHandler(paramHandler);
    }

}
