package org.mipams.jumbf.core.services.content_types;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.services.boxes.UuidBoxService;
import org.mipams.jumbf.core.util.MipamsException;

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
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, long availableBytesForBox)
            throws MipamsException {
        UuidBox uuidBox = uuidBoxService.parseFromJumbfFile(input, availableBytesForBox);
        return List.of(uuidBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        UuidBox jsonBox = (UuidBox) inputBox.get(0);
        uuidBoxService.writeToJumbfFile(jsonBox, fileOutputStream);
    }

}
