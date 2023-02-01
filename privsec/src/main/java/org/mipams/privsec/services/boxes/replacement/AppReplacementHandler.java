package org.mipams.privsec.services.boxes.replacement;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.BinaryDataBoxService;
import org.mipams.jumbf.util.MipamsException;

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
