package org.mipams.privsec.services.boxes;

import java.io.OutputStream;
import java.io.InputStream;

import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.services.boxes.BmffBoxService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

import org.mipams.privsec.entities.ReplacementDescriptionBox;
import org.mipams.privsec.entities.replacement.ParamHandlerInterface;
import org.mipams.privsec.entities.replacement.ReplacementType;
import org.mipams.privsec.services.boxes.replacement.ParamHandlerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplacementDescriptionBoxService extends BmffBoxService<ReplacementDescriptionBox> {

    @Autowired
    ParamHandlerFactory paramHandlerFactory;

    ServiceMetadata serviceMetadata = new ServiceMetadata(ReplacementDescriptionBox.TYPE_ID,
            ReplacementDescriptionBox.TYPE);

    @Override
    protected ReplacementDescriptionBox initializeBox() {
        return new ReplacementDescriptionBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected final void writeBmffPayloadToJumbfFile(ReplacementDescriptionBox box, OutputStream outputStream)
            throws MipamsException {

        CoreUtils.writeIntAsSingleByteToOutputStream(box.getReplacementTypeId(), outputStream);
        CoreUtils.writeIntAsSingleByteToOutputStream(box.getToggle(), outputStream);

        box.getParamHandler().writeParamToBytes(outputStream);

    }

    @Override
    protected final void populatePayloadFromJumbfFile(ReplacementDescriptionBox box, ParseMetadata parseMetadata,
            InputStream input) throws MipamsException {

        long remainingBytes = parseMetadata.getAvailableBytesForBox();

        int value = CoreUtils.readSingleByteAsIntFromInputStream(input);
        box.setReplacementTypeId(value);
        remainingBytes -= box.getReplacementTypeIdSize();

        value = CoreUtils.readSingleByteAsIntFromInputStream(input);
        box.setToggle(value);
        remainingBytes -= box.getToggleSize();

        ReplacementType replacementType = ReplacementType.getTypeFromId(box.getReplacementTypeId());

        ParamHandlerInterface paramHandler = paramHandlerFactory.getParamHandler(replacementType);
        paramHandler.populateParamFromBytes(input, remainingBytes);

        box.setParamHandler(paramHandler);
    }

}
