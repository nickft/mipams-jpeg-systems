package org.mipams.jumbf.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.BinaryDataBoxService;
import org.mipams.jumbf.services.boxes.EmbeddedFileDescriptionBoxService;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddedFileContentType implements ContentTypeService {

    @Autowired
    EmbeddedFileDescriptionBoxService embeddedFileDescriptionBoxService;

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    public String getContentTypeUuid() {
        return "40CB0C32-BB8A-489D-A70B-2AD6F47F4369";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream inputStream, ParseMetadata parseMetadata)
            throws MipamsException {

        EmbeddedFileDescriptionBox dBox = embeddedFileDescriptionBoxService.parseFromJumbfFile(inputStream,
                parseMetadata);

        BinaryDataBox binaryDataBox = binaryDataBoxService.parseFromJumbfFile(inputStream, parseMetadata);
        binaryDataBox.setReferencedExternally(dBox.isContentReferencedExternally());

        return List.of(dBox, binaryDataBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {

        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = (EmbeddedFileDescriptionBox) inputBox.get(0);
        embeddedFileDescriptionBoxService.writeToJumbfFile(embeddedFileDescriptionBox, outputStream);

        BinaryDataBox binaryDataBox = (BinaryDataBox) inputBox.get(1);
        binaryDataBoxService.writeToJumbfFile(binaryDataBox, outputStream);
    }
}