package org.mipams.privsec.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.BinaryDataBoxService;
import org.mipams.jumbf.services.content_types.ContentTypeService;
import org.mipams.jumbf.util.MipamsException;

import org.mipams.privsec.entities.ProtectionDescriptionBox;
import org.mipams.privsec.services.boxes.ProtectionDescriptionBoxService;

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
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
            throws MipamsException {

        ProtectionDescriptionBox protectionDescriptionBox = protectionDescriptionBoxService.parseFromJumbfFile(input,
                parseMetadata);

        BinaryDataBox binaryDataBox = binaryDataBoxService.parseFromJumbfFile(input, parseMetadata);

        return List.of(protectionDescriptionBox, binaryDataBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {

        ProtectionDescriptionBox protectionDescriptionBox = (ProtectionDescriptionBox) inputBox.get(0);
        protectionDescriptionBoxService.writeToJumbfFile(protectionDescriptionBox, outputStream);

        BinaryDataBox binaryDataBox = (BinaryDataBox) inputBox.get(1);
        binaryDataBoxService.writeToJumbfFile(binaryDataBox, outputStream);
    }

}
