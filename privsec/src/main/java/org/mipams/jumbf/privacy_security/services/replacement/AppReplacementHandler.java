package org.mipams.jumbf.privacy_security.services.replacement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.services.BinaryDataBoxService;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppReplacementHandler implements DataBoxHandler {

    @Autowired
    BinaryDataBoxService binaryDataBoxService;

    @Override
    public void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, FileOutputStream fileOutputStream)
            throws MipamsException {

        BinaryDataBox binaryDataBox = (BinaryDataBox) replacementDataBoxList.get(0);
        binaryDataBoxService.writeToJumbfFile(binaryDataBox, fileOutputStream);
    }

    @Override
    public List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, long availableBytesForBox)
            throws MipamsException {

        BinaryDataBox binaryDataBox = binaryDataBoxService.parseFromJumbfFile(inputStream, availableBytesForBox);
        return List.of(binaryDataBox);
    }

}
