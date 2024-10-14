package org.mipams.jumbf.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.JpegCodestreamException;
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
            throws JpegCodestreamException {

        try (OutputStream os = new FileOutputStream(outputUrl);) {
            CoreUtils.writeFileContentToOutput(outputUrl, os);
        }  catch (IOException | MipamsException e) {
            throw new JpegCodestreamException(e);
        }

        try (OutputStream os = new FileOutputStream(outputUrl, true);) {
            coreGeneratorService.generateJumbfBoxesToOutputStream(jumbfBoxList, os);
        }  catch (IOException | MipamsException e) {
            throw new JpegCodestreamException(e);
        }
    }
}
