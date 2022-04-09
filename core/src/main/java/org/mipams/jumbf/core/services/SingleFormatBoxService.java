package org.mipams.jumbf.core.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.SingleFormatBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Value;

public abstract class SingleFormatBoxService<T extends SingleFormatBox> extends XtBoxService<T> {

    @Value("${org.mipams.core.image_folder}")
    private String IMAGE_FOLDER;

    @Value("${org.mipams.core.max_file_size_in_bytes}")
    private long MAX_FILE_SIZE;

    @Override
    protected void populateBox(T singleFormatBox, ObjectNode input) throws MipamsException {
        String path = input.get("fileUrl").asText();

        if (path == null) {
            throw new BadRequestException("Path is not specified");
        }

        singleFormatBox.setFileUrl(path);
    }

    protected boolean doesFileSizeExceedApplicationLimits(String filePath) throws MipamsException {
        double size = CoreUtils.getFileSizeFromPath(filePath);
        return size > MAX_FILE_SIZE || size > Long.MAX_VALUE;
    }

    @Override
    protected void writeXtBoxPayloadToJumbfFile(T singleFormatBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        if (doesFileSizeExceedApplicationLimits(singleFormatBox.getFileUrl())) {
            throw new BadRequestException("File is too large for the application. Check the available limits.");
        }
        CoreUtils.writeFileContentToOutput(singleFormatBox.getFileUrl(), fileOutputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(T singleFormatBox, InputStream input) throws MipamsException {

        String fileName = getFileName();

        String fullPath = CoreUtils.getFullPath(IMAGE_FOLDER, fileName);

        singleFormatBox.setFileUrl(fullPath);

        try (FileOutputStream fileOutputStream = new FileOutputStream(fullPath)) {

            long nominalTotalSizeInBytes = singleFormatBox.getPayloadSizeFromXTBoxHeaders();

            int actualBytes = 0, n;

            while ((actualBytes < nominalTotalSizeInBytes) && ((n = input.read()) != -1)) {
                fileOutputStream.write(n);
                actualBytes++;
            }

        } catch (IOException e) {
            throw new MipamsException("Coulnd not read content", e);
        }
    }

    protected String getFileName() {

        String fileName = CoreUtils.randomStringGenerator();

        String extension = getExtension();

        return extension.isEmpty() ? fileName : String.format("%s.%s", fileName, extension);
    }

    protected String getExtension() {
        return "";
    }

}
