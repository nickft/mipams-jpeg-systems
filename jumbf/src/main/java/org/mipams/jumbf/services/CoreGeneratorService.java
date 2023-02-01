package org.mipams.jumbf.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.services.boxes.JumbfBoxService;
import org.mipams.jumbf.util.BadRequestException;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

@Service
public class CoreGeneratorService {

    @Autowired
    JumbfBoxService superBoxService;

    public void generateJumbfMetadataToFile(List<JumbfBox> jumbfBoxList, String assetUrl) throws MipamsException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(assetUrl)) {

            for (JumbfBox jumbfBox : jumbfBoxList) {
                superBoxService.writeToJumbfFile(jumbfBox, fileOutputStream);
            }
        } catch (IOException e) {
            throw new BadRequestException("Could not open file: " + assetUrl, e);
        }
    }
}