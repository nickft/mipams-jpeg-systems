package org.mipams.jumbf.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.JsonBoxService;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonContentType implements ContentTypeService {

    @Autowired
    JsonBoxService jsonBoxService;

    @Override
    public String getContentTypeUuid() {
        return "6A736F6E-0011-0010-8000-00AA00389B71";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
            throws MipamsException {
        JsonBox jsonBox = jsonBoxService.parseFromJumbfFile(input, parseMetadata);
        return List.of(jsonBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {
        JsonBox jsonBox = (JsonBox) inputBox.get(0);
        jsonBoxService.writeToJumbfFile(jsonBox, outputStream);
    }

}
