package org.mipams.jumbf.core.services.boxes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.MemoryBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MemoryBoxService<T extends MemoryBox> extends BmffBoxService<T> {

    @Autowired
    Properties properties;

    @Override
    protected void writeBmffPayloadToJumbfFile(T memoryBox, OutputStream outputStream)
            throws MipamsException {
        CoreUtils.writeByteArrayToOutputStream(memoryBox.getContent(), outputStream);
    }

    @Override
    protected void populatePayloadFromJumbfFile(T memoryBox, ParseMetadata parseMetadata, InputStream input)
            throws MipamsException {

        long nominalTotalSizeInBytes = memoryBox.getPayloadSizeFromBmffHeaders();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CoreUtils.writeBytesFromInputStreamToOutputstream(input, nominalTotalSizeInBytes, outputStream);

        memoryBox.setContent(outputStream.toByteArray());
    }
}
