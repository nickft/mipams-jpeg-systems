package org.mipams.jumbf.core.services.content_types;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.entities.XmlBox;
import org.mipams.jumbf.core.services.boxes.XmlBoxService;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XmlContentType implements ContentTypeService {

    @Autowired
    XmlBoxService xmlBoxService;

    @Override
    public String getContentTypeUuid() {
        return "786D6C20-0011-0010-8000-00AA00389B71";
    }

    @Override
    public List<BmffBox> parseContentBoxesFromJumbfFile(InputStream input, ParseMetadata parseMetadata)
            throws MipamsException {
        XmlBox xmlBox = xmlBoxService.parseFromJumbfFile(input, parseMetadata);
        return List.of(xmlBox);
    }

    @Override
    public void writeContentBoxesToJumbfFile(List<BmffBox> inputBox, OutputStream outputStream)
            throws MipamsException {
        XmlBox jsonBox = (XmlBox) inputBox.get(0);
        xmlBoxService.writeToJumbfFile(jsonBox, outputStream);
    }

}
