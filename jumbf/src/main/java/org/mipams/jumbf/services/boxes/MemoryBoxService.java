package org.mipams.jumbf.services.boxes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.MemoryBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

public abstract class MemoryBoxService<T extends MemoryBox> extends BmffBoxService<T> {

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
