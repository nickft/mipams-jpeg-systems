package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.BoxTypeEnum;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Value;

public class BinaryDataBoxService extends XTBoxService<BinaryDataBox> {

    @Value("${org.mipams.core.image_folder}")
    private String IMAGE_FOLDER;

    @Value("${org.mipams.core.max_file_size_in_bytes}")
    private long MAX_FILE_SIZE;

    @Override
    protected void populateBox(BinaryDataBox binaryDataBox, ObjectNode input) throws MipamsException {
        String path = input.get("path").asText();

        if (path == null) {
            throw new BadRequestException("Path is not specified");
        }

        binaryDataBox.setFileUrl(path);
    }

    @Override
    protected void writeXTBoxPayloadToJumbfFile(BinaryDataBox binaryDataBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        if (doesFileSizeExceedApplicationLimits(binaryDataBox.getFileUrl())) {
            throw new BadRequestException("File is too large for the application. Check the available limits.");
        }

        if (binaryDataBox.isReferencedExternally()) {
            writeUrlToJumbfBox(binaryDataBox, fileOutputStream);
        } else {
            CoreUtils.writeFileContentToOutput(binaryDataBox.getFileUrl(), fileOutputStream);
        }
    }

    protected boolean doesFileSizeExceedApplicationLimits(String filePath) throws MipamsException {
        double size = CoreUtils.getFileSizeFromPath(filePath);
        return size > MAX_FILE_SIZE || size > Long.MAX_VALUE;
    }

    private void writeUrlToJumbfBox(BinaryDataBox binaryDataBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        try {
            fileOutputStream.write(CoreUtils.convertStringToByteArray(binaryDataBox.getFileUrl()));
        } catch (IOException e) {
            throw new MipamsException(e);
        }
    }

    @Override
    protected BinaryDataBox initializeBox() throws MipamsException {
        return new BinaryDataBox();
    }

    @Override
    protected void populatePayloadFromJumbfFile(BinaryDataBox binaryDataBox, InputStream input) throws MipamsException {

        String fileName = CoreUtils.randomStringGenerator();

        String fullPath = CoreUtils.getFullPath(IMAGE_FOLDER, fileName);

        binaryDataBox.setFileUrl(fullPath);

        try (FileOutputStream fileOutputStream = new FileOutputStream(fullPath)) {

            long nominalTotalSizeInBytes = binaryDataBox.getPayloadSizeFromXTBoxHeaders();

            int actualBytes = 0, n;

            while ((actualBytes < nominalTotalSizeInBytes) && ((n = input.read()) != -1)) {
                fileOutputStream.write(n);
                actualBytes++;
            }

        } catch (IOException e) {
            throw new MipamsException("Coulnd not read Json content", e);
        }

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
