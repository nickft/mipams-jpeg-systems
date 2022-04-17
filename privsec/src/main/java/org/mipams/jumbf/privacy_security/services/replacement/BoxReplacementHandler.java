package org.mipams.jumbf.privacy_security.services.replacement;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.services.JumbfBoxService;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoxReplacementHandler implements DataBoxHandler {

    @Autowired
    JumbfBoxService jumbfBoxService;

    @Override
    public List<BmffBox> discoverDataBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        JsonNode contentNode = inputNode.get("content");

        List<BmffBox> contentList = new ArrayList<>();

        if (!contentNode.isArray()) {

            ObjectNode contentBoxNode = (ObjectNode) contentNode;
            BmffBox jumbfBox = jumbfBoxService.discoverBoxFromRequest(contentBoxNode);
            contentList.add(jumbfBox);
        } else {
            Iterator<JsonNode> contentIterator = contentNode.elements();

            while (contentIterator.hasNext()) {
                ObjectNode jumbfBoxNode = (ObjectNode) contentIterator.next();

                BmffBox jumbfBox = jumbfBoxService.discoverBoxFromRequest(jumbfBoxNode);
                contentList.add(jumbfBox);
            }
        }

        return contentList;
    }

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
