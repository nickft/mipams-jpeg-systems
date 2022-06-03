package org.mipams.jumbf.core.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.boxes.JumbfBoxService;
import org.mipams.jumbf.core.util.BadRequestException;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

@Service
public class CoreGeneratorService implements GeneratorInterface {

    @Autowired
    JumbfBoxService superBoxService;

    @Override
    public String generateJumbfMetadataToFile(List<JumbfBox> jumbfBoxList, String assetUrl) throws MipamsException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(assetUrl)) {

            for (JumbfBox jumbfBox : jumbfBoxList) {
                superBoxService.writeToJumbfFile(jumbfBox, fileOutputStream);
            }

            return assetUrl;
        } catch (IOException e) {
            throw new BadRequestException("Could not open file: " + assetUrl, e);
        }
    }
}