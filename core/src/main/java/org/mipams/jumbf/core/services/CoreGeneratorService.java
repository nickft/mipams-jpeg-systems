package org.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;

import java.io.FileNotFoundException;
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

    @Value("${org.mipams.core.image_folder}")
    private String IMAGE_FOLDER;

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
    public String generateJumbfFileFromBox(List<JumbfBox> xtBoxList) throws MipamsException {
        String path = CoreUtils.getFullPath(IMAGE_FOLDER, "test.jumbf");

        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {

            for (JumbfBox jumbfBox : xtBoxList) {

                superBoxService.writeToJumbfFile(jumbfBox, fileOutputStream);

                logger.debug("Write box: " + jumbfBox.toString() + " to file");
            }

            return "JUMBF file is stored in the following location: " + path;
        } catch (FileNotFoundException e) {
            throw new BadRequestException("File {" + path + "} does not exist", e);
        } catch (IOException e) {
            throw new BadRequestException("Could not open file: " + path, e);
        }
    }
}