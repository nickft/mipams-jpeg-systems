package org.mipams.jumbf.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.FileBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public abstract class FileBoxService<T extends FileBox> extends BmffBoxService<T> {

    @Override
    protected void writeBmffPayloadToJumbfFile(T fileBox, OutputStream outputStream)
            throws MipamsException {
        CoreUtils.writeFileContentToOutput(fileBox.getFileUrl(), outputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(T fileBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {

        String fileName = getFileName();

        String fullPath = CoreUtils.getFullPath(parseMetadata.getParentDirectory(), fileName);

        fileBox.setFileUrl(fullPath);

        long nominalTotalSizeInBytes = fileBox.getPayloadSizeFromBmffHeaders();

        CoreUtils.writeBytesFromInputStreamToFile(input, nominalTotalSizeInBytes, fileBox.getFileUrl());
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
