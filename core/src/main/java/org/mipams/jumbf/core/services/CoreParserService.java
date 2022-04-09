package org.mipams.jumbf.core.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.CorruptedJumbfFileException;
import org.mipams.jumbf.core.util.MipamsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreParserService implements ParserInterface {

    private static final Logger logger = LoggerFactory.getLogger(CoreParserService.class);

    @Autowired
    JumbfBoxService superBoxService;

    @Override
    public List<JumbfBox> parseMetadataFromJumbfFile(String path) throws MipamsException {

        try (InputStream input = new FileInputStream(path)) {

            List<JumbfBox> xtBoxList = new ArrayList<>();

            while (input.available() > 0) {

                JumbfBox jumbfBox = superBoxService.parseFromJumbfFile(input);

                logger.debug("New box discovered: " + jumbfBox.toString());

                xtBoxList.add(jumbfBox);
            }

            return xtBoxList;
        } catch (FileNotFoundException e) {
            throw new CorruptedJumbfFileException("File {" + path + "} does not exist", e);
        } catch (IOException e) {
            throw new CorruptedJumbfFileException("Could not open file: " + path, e);
        }

    }
}