package org.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.BoxServiceManager;
import org.mipams.jumbf.core.entities.XTBox;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CoreGeneratorService implements GeneratorInterface {

    private static final Logger logger = LoggerFactory.getLogger(CoreGeneratorService.class);

    @Autowired
    BoxServiceManager boxServiceManager;

    @Value("${org.mipams.core.image_folder}")
    private String IMAGE_FOLDER;

    @Override
    public List<XTBox> generateBoxFromRequest(JsonNode inputNode) throws MipamsException {

        List<XTBox> xtBoxList = new ArrayList<>();

        logger.debug(inputNode.toString());

        if (!inputNode.isArray()) {

            ObjectNode inputObjectNode = (ObjectNode) inputNode;

            XTBox superbox = boxServiceManager.getSuperBoxService().discoverXTBoxFromRequest(inputObjectNode);

            xtBoxList.add(superbox);
        } else {

            Iterator<JsonNode> elementIterator = inputNode.elements();

            while (elementIterator.hasNext()) {
                ObjectNode elementNode = (ObjectNode) elementIterator.next();

                XTBox box = boxServiceManager.getSuperBoxService().discoverXTBoxFromRequest(elementNode);
                xtBoxList.add(box);
            }
        }

        return xtBoxList;
    }

    @Override
    public String generateJumbfFileFromBox(List<XTBox> xtBoxList) throws MipamsException {
        String path = CoreUtils.getFullPath(IMAGE_FOLDER, "test.jumbf");

        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {

            for (XTBox xtBox : xtBoxList) {
                XTBoxService superBoxService = boxServiceManager.getSuperBoxService();

                superBoxService.writeToJumbfFile(xtBox, fileOutputStream);

                logger.debug("Write box: " + xtBox.toString() + " to file");
            }

            return "JUMBF file is stored in the following location: " + path;
        } catch (FileNotFoundException e) {
            throw new BadRequestException("File {" + path + "} does not exist", e);
        } catch (IOException e) {
            throw new BadRequestException("Could not open file: " + path, e);
        }
    }
}