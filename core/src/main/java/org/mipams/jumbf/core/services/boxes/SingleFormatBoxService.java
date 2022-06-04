package org.mipams.jumbf.core.services.boxes;

import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.SingleFormatBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;

public abstract class SingleFormatBoxService<T extends SingleFormatBox> extends BmffBoxService<T> {

    @Override
    protected void writeBmffPayloadToJumbfFile(T singleFormatBox, OutputStream outputStream)
            throws MipamsException {
        CoreUtils.writeFileContentToOutput(singleFormatBox.getFileUrl(), outputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(T singleFormatBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {

        String fileName = getFileName();

        String fullPath = CoreUtils.getFullPath(parseMetadata.getParentDirectory(), fileName);

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
