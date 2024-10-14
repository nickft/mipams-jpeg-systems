package org.mipams.jumbf.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.JumbfBoxService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.CorruptedJumbfFileException;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.UnsupportedContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreParserService implements ParserInterface {

    private static final Logger logger = Logger.getLogger(CoreParserService.class.getName());

    @Autowired
    JumbfBoxService superBoxService;

    @Override
    public List<JumbfBox> parseMetadataFromFile(String assetUrl) throws MipamsException {

        String parentDirectory = CoreUtils.getParentDirectory(assetUrl);
        String tmpDirectory = CoreUtils.createSubdirectory(parentDirectory, CoreUtils.randomStringGenerator());

        ParseMetadata parseMetadata = new ParseMetadata();
        parseMetadata.setParentDirectory(tmpDirectory);

        try (InputStream input = new BufferedInputStream(new FileInputStream(assetUrl), 8)) {

            List<JumbfBox> bmffBoxList = new ArrayList<>();

            while (input.available() > 0) {
                Optional<JumbfBox> jumbfBox = parseJumbfBoxFromInputStream(parseMetadata, input);
                if(jumbfBox.isPresent()){
                    bmffBoxList.add(jumbfBox.get());
                }
            }

            return bmffBoxList;
        } catch (IOException e) {
            throw new CorruptedJumbfFileException("Could not open file: " + assetUrl, e);
        }
    }

    public Optional<JumbfBox> parseJumbfBoxFromInputStream(ParseMetadata parseMetadata, InputStream input) throws MipamsException {
        try{
            JumbfBox jumbfBox = superBoxService.parseSuperBox(input, parseMetadata);
            logger.log(Level.FINE, "New box discovered: " + jumbfBox.toString());
            return Optional.of(jumbfBox);
        } catch (UnsupportedContentTypeException e) {
            /* Ignore JUMBF Box and continue */
            return Optional.empty();
        }
    }

}