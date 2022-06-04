package org.mipams.jumbf.privacy_security.services.boxes.replacement;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.ParseMetadata;
import org.mipams.jumbf.core.services.boxes.BinaryDataBoxService;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppReplacementHandler implements DataBoxHandler {

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    public void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, OutputStream outputStream)
            throws MipamsException {

        BinaryDataBox binaryDataBox = (BinaryDataBox) replacementDataBoxList.get(0);
        binaryDataBoxService.writeToJumbfFile(binaryDataBox, outputStream);
    }

    @Override
    public List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, ParseMetadata parseMetadata)
            throws MipamsException {

        BinaryDataBox binaryDataBox = binaryDataBoxService.parseFromJumbfFile(inputStream, parseMetadata);
        return List.of(binaryDataBox);
    }

}
