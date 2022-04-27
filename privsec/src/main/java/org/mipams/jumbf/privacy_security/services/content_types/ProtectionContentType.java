package org.mipams.jumbf.privacy_security.services.content_types;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.services.boxes.BinaryDataBoxService;
import org.mipams.jumbf.core.services.content_types.ContentTypeService;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.privacy_security.entities.ProtectionDescriptionBox;
import org.mipams.jumbf.privacy_security.services.boxes.ProtectionDescriptionBoxService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProtectionContentType implements ContentTypeService {

    @Autowired
    ProtectionDescriptionBoxService protectionDescriptionBoxService;

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    public String getContentTypeUuid() {
        return "74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, long availableBytesForBox)
            throws MipamsException {

        ProtectionDescriptionBox protectionDescriptionBox = protectionDescriptionBoxService.parseFromJumbfFile(input,
                availableBytesForBox);

        BinaryDataBox binaryDataBox = binaryDataBoxService.parseFromJumbfFile(input, availableBytesForBox);

        return List.of(protectionDescriptionBox, binaryDataBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, FileOutputStream fileOutputStream)
            throws MipamsException {

        ProtectionDescriptionBox protectionDescriptionBox = (ProtectionDescriptionBox) inputBox.get(0);
        protectionDescriptionBoxService.writeToJumbfFile(protectionDescriptionBox, fileOutputStream);

        BinaryDataBox binaryDataBox = (BinaryDataBox) inputBox.get(1);
        binaryDataBoxService.writeToJumbfFile(binaryDataBox, fileOutputStream);
    }

}
