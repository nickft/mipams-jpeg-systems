package org.mipams.jumbf.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.services.boxes.JumbfBoxService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.JpegXLException;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpegXLGenerator {
    @Autowired
    JpegXLParser jpegXLParser;

    @Autowired
    JumbfBoxService jumbfBoxService;

    public void generateJumbfMetadataToFile(List<JumbfBox> jumbfBoxList, String assetUrl, String outputUrl) 
    throws JpegXLException {

        try {
            jpegXLParser.parseMetadataFromFile(assetUrl);

            try (InputStream is = new BufferedInputStream(new FileInputStream(assetUrl), 8);
            OutputStream os = new FileOutputStream(outputUrl);) {
                while(is.available() > 0) {
                    writeJxlBoxToOutputStream(is, os);
                }

                for (JumbfBox jumbfBox: jumbfBoxList){
                    jumbfBoxService.writeToJumbfFile(jumbfBox, os);
                }
            }


        } catch (Exception e) {
            throw new JpegXLException(e);
        }

    }

    private void writeJxlBoxToOutputStream(InputStream is, OutputStream os) throws MipamsException, IOException {

        int lBox = CoreUtils.readIntFromInputStream(is);
        CoreUtils.writeIntToOutputStream(lBox, os);

        int tBox = CoreUtils.readIntFromInputStream(is);
        CoreUtils.writeIntToOutputStream(tBox, os);

        long boxLength = lBox;

        if (boxLength == 1 ) {
            boxLength = CoreUtils.readLongFromInputStream(is);
            boxLength -= CoreUtils.LONG_BYTE_SIZE;
            CoreUtils.writeLongToOutputStream(boxLength, os);
        }

        if (lBox != 0) {
            boxLength -= 2 * CoreUtils.INT_BYTE_SIZE;
        }


        CoreUtils.writeBytesFromInputStreamToOutputstream(is, boxLength, os);
    }
}
