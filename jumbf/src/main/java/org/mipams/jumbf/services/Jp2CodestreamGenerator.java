package org.mipams.jumbf.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.Jp2CodestreamException;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Jp2CodestreamGenerator {

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @Autowired
    CoreGeneratorService coreGeneratorService;

    public void generateJumbfMetadataToFile(List<JumbfBox> jumbfBoxList, String assetUrl, String outputUrl)
            throws Jp2CodestreamException {

        try (OutputStream os = new FileOutputStream(outputUrl);) {
            CoreUtils.writeFileContentToOutput(assetUrl, os);
            
            coreGeneratorService.generateJumbfBoxesToOutputStream(jumbfBoxList, os);
        }  catch (IOException | MipamsException e) {
            throw new Jp2CodestreamException(e);
        }
    }
}
