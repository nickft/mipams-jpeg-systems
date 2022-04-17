package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.ServiceMetadata;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BinaryDataBoxService extends SingleFormatBoxService<BinaryDataBox> {

    @Autowired
    Properties properties;

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
        try {

            String fileUrlWithEscapeChar = CoreUtils.addEscapeCharacterToText(binaryDataBox.getFileUrl());
            fileOutputStream.write(CoreUtils.convertStringToByteArray(fileUrlWithEscapeChar));
        } catch (IOException e) {
            throw new MipamsException(e);
        }
    }

    private void writeFileToJumbfBox(BinaryDataBox binaryDataBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        properties.checkIfFileSizeExceedApplicationLimits(binaryDataBox.getFileUrl());

        CoreUtils.writeFileContentToOutput(binaryDataBox.getFileUrl(), fileOutputStream);
    }

    @Override
    protected BinaryDataBox initializeBox() throws MipamsException {
        return new BinaryDataBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return BoxTypeEnum.BinaryDataBox.getServiceMetadata();
    }
}
