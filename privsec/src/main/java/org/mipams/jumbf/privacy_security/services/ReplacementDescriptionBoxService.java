package org.mipams.jumbf.privacy_security.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.services.BmffBoxService;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.privacy_security.entities.ReplacementDescriptionBox;
import org.mipams.jumbf.privacy_security.entities.replacement.ParamHandlerInterface;
import org.mipams.jumbf.privacy_security.entities.replacement.ReplacementType;
import org.mipams.jumbf.privacy_security.services.replacement.ParamHandlerFactory;
import org.mipams.jumbf.privacy_security.util.BoxTypeEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementDescriptionBoxService extends BmffBoxService<ReplacementDescriptionBox> {

    @Autowired
    ParamHandlerFactory paramHandlerFactory;

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.ReplacementDescriptionBox.getServiceMetadata();
    }

    @Override
    protected ReplacementDescriptionBox initializeBox() throws MipamsException {
        return new ReplacementDescriptionBox();
    }

    @Override
    protected final void populateBox(ReplacementDescriptionBox box, ObjectNode input) throws MipamsException {
        String typeAsString = input.get("replacementType").asText();

        ReplacementType replacementType = ReplacementType.getTypeFromString(typeAsString);

        box.setReplacementTypeId(replacementType.getId());

        boolean isAutoApply = input.get("auto-apply").asBoolean();
        box.setAutoApply(isAutoApply);

        ParamHandlerInterface paramHandler = paramHandlerFactory.getParamHandler(replacementType);
        paramHandler.populateParamFromRequest(input);

        box.setParamHandler(paramHandler);
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
    protected final void populatePayloadFromJumbfFile(ReplacementDescriptionBox box, InputStream input)
            throws MipamsException {

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
