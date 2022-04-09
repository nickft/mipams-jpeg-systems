package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BinaryDataBoxService extends SingleFormatBoxService<BinaryDataBox> {

    @Value("${org.mipams.core.image_folder}")
    private String IMAGE_FOLDER;

    @Override
    protected void writeXtBoxPayloadToJumbfFile(BinaryDataBox binaryDataBox, FileOutputStream fileOutputStream)
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

        if (doesFileSizeExceedApplicationLimits(binaryDataBox.getFileUrl())) {
            throw new BadRequestException("File is too large for the application. Check the available limits.");
        }

        CoreUtils.writeFileContentToOutput(binaryDataBox.getFileUrl(), fileOutputStream);
    }

    @Override
    protected BinaryDataBox initializeBox() throws MipamsException {
        return new BinaryDataBox();
    }

    @Override
    public int serviceIsResponsibleForBoxTypeId() {
        return BoxTypeEnum.BinaryDataBox.getTypeId();
    }

    @Override
    public String serviceIsResponsibleForBoxType() {
        return BoxTypeEnum.BinaryDataBox.getType();
    }
}
