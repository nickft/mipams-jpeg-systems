package org.mipams.jumbf.demo.services.privacy_security.replacement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.util.MipamsException;

import org.mipams.jumbf.demo.services.core.BinaryDataBoxParser;
import org.mipams.jumbf.demo.services.core.JumbfBoxParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoxReplacementDataBoxParser implements DataBoxParser {

    @Autowired
    JumbfBoxParser jumbfBoxParser;

    @Autowired
    BinaryDataBoxParser binaryDataBoxParser;

    @Override
    public List<BmffBox> discoverDataBoxFromRequest(ObjectNode inputNode) throws MipamsException {
        JsonNode contentNode = inputNode.get("content");

        List<BmffBox> contentList = new ArrayList<>();

        if (!contentNode.isArray()) {

            ObjectNode contentBoxNode = (ObjectNode) contentNode;
            BmffBox jumbfBox = jumbfBoxParser.discoverBoxFromRequest(contentBoxNode);
            contentList.add(jumbfBox);
        } else {
            Iterator<JsonNode> contentIterator = contentNode.elements();

            while (contentIterator.hasNext()) {
                ObjectNode jumbfBoxNode = (ObjectNode) contentIterator.next();

                BmffBox jumbfBox = jumbfBoxParser.discoverBoxFromRequest(jumbfBoxNode);
                contentList.add(jumbfBox);
            }
        }

        return contentList;
    }

}
