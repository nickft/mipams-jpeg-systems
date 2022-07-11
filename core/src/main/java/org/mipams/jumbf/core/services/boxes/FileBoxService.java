package org.mipams.jumbf.core.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.FileBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FileBoxService<T extends FileBox> extends BmffBoxService<T> {

    @Autowired
    Properties properties;

    @Override
    protected void writeBmffPayloadToJumbfFile(T fileBox, OutputStream outputStream)
            throws MipamsException {
        CoreUtils.writeFileContentToOutput(fileBox.getFileUrl(), outputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(T fileBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {

        String fileName = getFileName();

        String fullPath = CoreUtils.getFullPath(properties.getFileDirectory(), fileName);

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
