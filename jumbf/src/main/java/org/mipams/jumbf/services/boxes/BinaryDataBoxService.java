package org.mipams.jumbf.services.boxes;

import java.io.OutputStream;

import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.ServiceMetadata;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.stereotype.Service;

@Service
public class BinaryDataBoxService extends FileBoxService<BinaryDataBox> {

    ServiceMetadata serviceMetadata = new ServiceMetadata(BinaryDataBox.TYPE_ID, BinaryDataBox.TYPE);

    @Override
    protected BinaryDataBox initializeBox() {
        return new BinaryDataBox();
    }

    @Override
    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    @Override
    protected void writeBmffPayloadToJumbfFile(BinaryDataBox binaryDataBox, OutputStream outputStream)
            throws MipamsException {

        if (binaryDataBox.isReferencedExternally()) {
            writeUrlToJumbfBox(binaryDataBox, outputStream);
        } else {
            writeFileToJumbfBox(binaryDataBox, outputStream);
        }
    }

    private void writeUrlToJumbfBox(BinaryDataBox binaryDataBox, OutputStream outputStream)
            throws MipamsException {
        String fileUrlWithEscapeChar = CoreUtils.addEscapeCharacterToText(binaryDataBox.getFileUrl());
        CoreUtils.writeTextToOutputStream(fileUrlWithEscapeChar, outputStream);
    }

    private void writeFileToJumbfBox(BinaryDataBox binaryDataBox, OutputStream outputStream)
            throws MipamsException {
        CoreUtils.writeFileContentToOutput(binaryDataBox.getFileUrl(), outputStream);
    }
}
