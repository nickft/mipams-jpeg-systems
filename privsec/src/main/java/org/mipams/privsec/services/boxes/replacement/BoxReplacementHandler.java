package org.mipams.privsec.services.boxes.replacement;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.JumbfBoxService;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoxReplacementHandler implements DataBoxHandler {

    @Autowired
    JumbfBoxService jumbfBoxService;

    @Override
    public void writeDataBoxToJumbfFile(List<BmffBox> replacementDataBoxList, OutputStream outputStream)
            throws MipamsException {

        for (BmffBox contentBox : replacementDataBoxList) {
            JumbfBox jumbfBox = (JumbfBox) contentBox;
            jumbfBoxService.writeToJumbfFile(jumbfBox, outputStream);
        }
    }

    @Override
    public List<BmffBox> parseDataBoxFromJumbfFile(InputStream inputStream, ParseMetadata parseMetadata)
            throws MipamsException {
        List<BmffBox> contentBoxList = new ArrayList<>();

        long availableBytesForBox = parseMetadata.getAvailableBytesForBox();

        ParseMetadata jumbfParseMetadata;

        while (availableBytesForBox > 0) {

            if (CoreUtils.isPaddingBoxNext(inputStream)) {
                break;
            }

            jumbfParseMetadata = new ParseMetadata();
            jumbfParseMetadata.setAvailableBytesForBox(availableBytesForBox);
            jumbfParseMetadata.setParentDirectory(parseMetadata.getParentDirectory());

            JumbfBox jumbfBox = jumbfBoxService.parseFromJumbfFile(inputStream, jumbfParseMetadata);
            availableBytesForBox -= jumbfBox.getBoxSize();

            contentBoxList.add(jumbfBox);
        }

        return contentBoxList;
    }

}
