package org.mipams.jumbf.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.JumbfBoxService;
import org.mipams.jumbf.util.CorruptedJumbfFileException;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.Properties;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreParserService implements ParserInterface {

    private static final Logger logger = Logger.getLogger(CoreParserService.class.getName());

    @Autowired
    JumbfBoxService superBoxService;

    @Autowired
    Properties properties;

    @Override
    public List<JumbfBox> parseMetadataFromFile(String assetUrl) throws MipamsException {

        ParseMetadata parseMetadata = new ParseMetadata();
        parseMetadata.setParentDirectory(properties.getFileDirectory());

        try (InputStream input = new BufferedInputStream(new FileInputStream(assetUrl), 8)) {

            List<JumbfBox> bmffBoxList = new ArrayList<>();

            while (input.available() > 0) {

                JumbfBox jumbfBox = superBoxService.parseSuperBox(input, parseMetadata);
                logger.log(Level.FINE,"New box discovered: " + jumbfBox.toString());

                bmffBoxList.add(jumbfBox);
            }

            return bmffBoxList;
        } catch (IOException e) {
            throw new CorruptedJumbfFileException("Could not open file: " + assetUrl, e);
        }

    }
}