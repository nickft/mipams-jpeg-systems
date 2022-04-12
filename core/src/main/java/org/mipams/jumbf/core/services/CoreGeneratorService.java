package org.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CoreGeneratorService implements GeneratorInterface {

    private static final Logger logger = LoggerFactory.getLogger(CoreGeneratorService.class);

    @Autowired
    JumbfBoxService superBoxService;

    @Autowired
    Properties properties;

    @Override
    public List<JumbfBox> generateBoxFromRequest(JsonNode inputNode) throws MipamsException {

        List<JumbfBox> jumbfBoxList = new ArrayList<>();

        logger.debug(inputNode.toString());

        if (!inputNode.isArray()) {

            ObjectNode inputObjectNode = (ObjectNode) inputNode;

            JumbfBox superbox = superBoxService.discoverBoxFromRequest(inputObjectNode);

            jumbfBoxList.add(superbox);
        } else {

            Iterator<JsonNode> elementIterator = inputNode.elements();

            while (elementIterator.hasNext()) {
                ObjectNode elementNode = (ObjectNode) elementIterator.next();

                JumbfBox jumbfBox = superBoxService.discoverBoxFromRequest(elementNode);
                jumbfBoxList.add(jumbfBox);
            }
        }

        return jumbfBoxList;
    }

    @Override
    public String generateJumbfFileFromBox(List<JumbfBox> jumbfBoxList, String fileName) throws MipamsException {

        String path = CoreUtils.getFullPath(properties.getFileDirectory(), fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {

            for (JumbfBox jumbfBox : jumbfBoxList) {
                superBoxService.writeToJumbfFile(jumbfBox, fileOutputStream);
            }

            return generateResultMessage(jumbfBoxList, path);
        } catch (IOException e) {
            throw new BadRequestException("Could not open file: " + path, e);
        }
    }

    private String generateResultMessage(List<JumbfBox> jumbfBoxList, String path) {

        StringBuilder result = new StringBuilder("Jumbf file is stored in location ");

        result.append(path).append("\n");

        result.append("The JUMBF content is the following: \n");

        for (JumbfBox jumbfBox : jumbfBoxList) {
            result.append(jumbfBox.toString());
        }

        return result.toString();
    }
}