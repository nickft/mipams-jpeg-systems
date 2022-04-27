package org.mipams.jumbf.core.services.content_types;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.services.boxes.JsonBoxService;
import org.mipams.jumbf.core.util.MipamsException;
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
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, long availableBytesForBox)
            throws MipamsException {
        JsonBox jsonBox = jsonBoxService.parseFromJumbfFile(input, availableBytesForBox);
        return List.of(jsonBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, FileOutputStream fileOutputStream)
            throws MipamsException {
        JsonBox jsonBox = (JsonBox) inputBox.get(0);
        jsonBoxService.writeToJumbfFile(jsonBox, fileOutputStream);
    }

}
