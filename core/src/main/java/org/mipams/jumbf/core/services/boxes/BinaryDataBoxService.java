package org.mipams.jumbf.core.services.boxes;

import java.io.OutputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BinaryDataBoxService extends SingleFormatBoxService<BinaryDataBox> {

    @Autowired
    Properties properties;

    ServiceMetadata serviceMetadata;

    @PostConstruct
    void init() {
        BinaryDataBox box = initializeBox();
        serviceMetadata = new ServiceMetadata(box.getTypeId(), box.getType());
    }

    @Override
    protected BinaryDataBox initializeBox() {
        return new BinaryDataBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(BinaryDataBox binaryDataBox, OutputStream outputStream)
            throws MipamsException {

        if (binaryDataBox.isReferencedExternally()) {
            writeUrlToJumbfBox(binaryDataBox, outputStream);
        } else {
            writeFileToJumbfBox(binaryDataBox, outputStream);
        }
    }

    private void writeUrlToJumbfBox(BinaryDataBox binaryDataBox, OutputStream outputStream)
            throws MipamsException {
        String fileUrlWithEscapeChar = CoreUtils.addEscapeCharacterToText(binaryDataBox.getFileUrl());
        CoreUtils.writeTextToOutputStream(fileUrlWithEscapeChar, outputStream);
    }

    private void writeFileToJumbfBox(BinaryDataBox binaryDataBox, OutputStream outputStream)
            throws MipamsException {

        properties.checkIfFileSizeExceedApplicationLimits(binaryDataBox.getFileUrl());
        CoreUtils.writeFileContentToOutput(binaryDataBox.getFileUrl(), outputStream);
    }
}
