package org.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.boxes.JumbfBoxService;
import org.mipams.jumbf.core.util.BadRequestException;
import org.mipams.jumbf.core.util.CoreUtils;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

@Service
public class CoreGeneratorService implements GeneratorInterface {

    @Autowired
    JumbfBoxService superBoxService;

    @Autowired
    Properties properties;

    @Override
    public String generateJumbfFileFromBox(List<JumbfBox> jumbfBoxList, String fileName) throws MipamsException {

        String path = CoreUtils.getFullPath(properties.getFileDirectory(), fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {

            for (JumbfBox jumbfBox : jumbfBoxList) {
                superBoxService.writeToJumbfFile(jumbfBox, fileOutputStream);
            }

            return path;
        } catch (IOException e) {
            throw new BadRequestException("Could not open file: " + path, e);
        }
    }
}