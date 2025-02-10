package org.mipams.jumbf.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.UnsupportedContentTypeException;
import org.mipams.jumbf.ContentTypeDiscoveryManager;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.services.boxes.JumbfBoxService;
import org.mipams.jumbf.util.BadRequestException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoreGeneratorService {

    @Autowired
    JumbfBoxService superBoxService;

    @Autowired
    ContentTypeDiscoveryManager contentTypeDiscoveryManager;

    public void generateJumbfMetadataToFile(List<JumbfBox> jumbfBoxList, String assetUrl) throws MipamsException {

        List<JumbfBox> supportedJumbfBoxList = new ArrayList<>();

        for (JumbfBox jumbfBox : jumbfBoxList) {
            try {
                contentTypeDiscoveryManager
                        .getContentBoxServiceBasedOnContentUuid(jumbfBox.getDescriptionBox().getUuid());
                supportedJumbfBoxList.add(jumbfBox);
            } catch (UnsupportedContentTypeException e) {
                /* Ignore unsupported JUMBF Box */
            }

        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(assetUrl)) {
            generateJumbfBoxesToOutputStream(jumbfBoxList, fileOutputStream);
        } catch (IOException e) {
            throw new BadRequestException("Could not open file: " + assetUrl, e);
        }
    }

    public void generateJumbfBoxesToOutputStream(List<JumbfBox> jumbfBoxList, OutputStream os) throws MipamsException {
        for(JumbfBox jumbfBox: jumbfBoxList) {
            try {
                contentTypeDiscoveryManager
                        .getContentBoxServiceBasedOnContentUuid(jumbfBox.getDescriptionBox().getUuid());

                superBoxService.writeToJumbfFile(jumbfBox, os);
            } catch (UnsupportedContentTypeException e) {
                /* Ignore unsupported JUMBF Box */
            }
        }
    }
}