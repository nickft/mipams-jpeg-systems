package org.mipams.jumbf.core.services.boxes;

import java.io.FileOutputStream;
import java.io.InputStream;

import org.mipams.jumbf.core.entities.SingleFormatBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SingleFormatBoxService<T extends SingleFormatBox> extends BmffBoxService<T> {

    @Autowired
    Properties properties;

    @Override
    protected void writeBmffPayloadToJumbfFile(T singleFormatBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        properties.checkIfFileSizeExceedApplicationLimits(singleFormatBox.getFileUrl());

        CoreUtils.writeFileContentToOutput(singleFormatBox.getFileUrl(), fileOutputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(T singleFormatBox, long availableBytesForBox, InputStream input)
            throws MipamsException {

        String fileName = getFileName();

        String fullPath = CoreUtils.getFullPath(properties.getFileDirectory(), fileName);

        singleFormatBox.setFileUrl(fullPath);

        long nominalTotalSizeInBytes = singleFormatBox.getPayloadSizeFromBmffHeaders();

        CoreUtils.writeBytesFromInputStreamToFile(input, nominalTotalSizeInBytes, singleFormatBox.getFileUrl());
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
