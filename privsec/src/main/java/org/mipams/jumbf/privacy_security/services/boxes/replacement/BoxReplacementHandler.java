package org.mipams.jumbf.privacy_security.services.boxes.replacement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.boxes.JumbfBoxService;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoxReplacementHandler implements DataBoxHandler {

    @Autowired
    JumbfBoxService jumbfBoxService;

    @Override
    public void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, FileOutputStream fileOutputStream)
            throws MipamsException {

        for (BmffBox contentBox : replacementDataBoxList) {
            JumbfBox jumbfBox = (JumbfBox) contentBox;
            jumbfBoxService.writeToJumbfFile(jumbfBox, fileOutputStream);
        }

    }

    @Override
    public List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, long availableBytesForBox)
            throws MipamsException {
        List<BmffBox> contentBoxList = new ArrayList<>();

        while (availableBytesForBox > 0) {

            JumbfBox jumbfBox = jumbfBoxService.parseFromJumbfFile(inputStream, availableBytesForBox);
            availableBytesForBox -= jumbfBox.getBoxSize();

            contentBoxList.add(jumbfBox);
        }

        return contentBoxList;
    }

}
