package org.mipams.jumbf.services.boxes;

import java.io.OutputStream;

import javax.annotation.PostConstruct;

import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BinaryDataBoxService extends FileBoxService<BinaryDataBox> {

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
