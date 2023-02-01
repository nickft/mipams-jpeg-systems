package org.mipams.jumbf.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.CborBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.CborBoxService;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CborContentType implements ContentTypeService {

    @Autowired
    CborBoxService cborBoxService;

    @Override
    public String getContentTypeUuid() {
        return "63626F72-0011-0010-8000-00AA00389B71";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
            throws MipamsException {
        CborBox cborBox = cborBoxService.parseFromJumbfFile(input, parseMetadata);
        return List.of(cborBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {
        CborBox jsonBox = (CborBox) inputBox.get(0);
        cborBoxService.writeToJumbfFile(jsonBox, outputStream);
    }

}
