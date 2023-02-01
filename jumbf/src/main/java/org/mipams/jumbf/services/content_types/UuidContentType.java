package org.mipams.jumbf.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.entities.UuidBox;
import org.mipams.jumbf.services.boxes.UuidBoxService;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UuidContentType implements ContentTypeService {

    @Autowired
    UuidBoxService uuidBoxService;

    @Override
    public String getContentTypeUuid() {
        return "75756964-0011-0010-8000-00AA00389B71";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
            throws MipamsException {
        UuidBox uuidBox = uuidBoxService.parseFromJumbfFile(input, parseMetadata);
        return List.of(uuidBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {
        UuidBox jsonBox = (UuidBox) inputBox.get(0);
        uuidBoxService.writeToJumbfFile(jsonBox, outputStream);
    }

}
