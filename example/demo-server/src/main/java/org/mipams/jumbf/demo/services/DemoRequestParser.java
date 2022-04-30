package org.mipams.jumbf.demo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.demo.services.core.JumbfBoxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoRequestParser {

    @Autowired
    JumbfBoxParser superBoxParser;

    private static final Logger logger = LoggerFactory.getLogger(DemoRequestParser.class);

    public List<JumbfBox> generateBoxFromRequest(JsonNode inputNode) throws MipamsException {

        List<JumbfBox> jumbfBoxList = new ArrayList<>();

        logger.debug(inputNode.toString());

        if (!inputNode.isArray()) {

            ObjectNode inputObjectNode = (ObjectNode) inputNode;

            JumbfBox superbox = superBoxParser.discoverBoxFromRequest(inputObjectNode);

            jumbfBoxList.add(superbox);
        } else {

            Iterator<JsonNode> elementIterator = inputNode.elements();

            while (elementIterator.hasNext()) {
                ObjectNode elementNode = (ObjectNode) elementIterator.next();

                JumbfBox jumbfBox = superBoxParser.discoverBoxFromRequest(elementNode);
                jumbfBoxList.add(jumbfBox);
            }
        }

        logger.debug(jumbfBoxList.toString());

        return jumbfBoxList;
    }
}
