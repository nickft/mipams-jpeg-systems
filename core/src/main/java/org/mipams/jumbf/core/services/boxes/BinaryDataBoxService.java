package org.mipams.jumbf.core.services.boxes;

import java.io.FileOutputStream;

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
    protected void writeBmffPayloadToJumbfFile(BinaryDataBox binaryDataBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        if (binaryDataBox.isReferencedExternally()) {
            writeUrlToJumbfBox(binaryDataBox, fileOutputStream);
        } else {
            writeFileToJumbfBox(binaryDataBox, fileOutputStream);
        }
    }

    private void writeUrlToJumbfBox(BinaryDataBox binaryDataBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        String fileUrlWithEscapeChar = CoreUtils.addEscapeCharacterToText(binaryDataBox.getFileUrl());
        CoreUtils.writeTextToOutputStream(fileUrlWithEscapeChar, fileOutputStream);
    }

    private void writeFileToJumbfBox(BinaryDataBox binaryDataBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        properties.checkIfFileSizeExceedApplicationLimits(binaryDataBox.getFileUrl());

        CoreUtils.writeFileContentToOutput(binaryDataBox.getFileUrl(), fileOutputStream);
    }
}
