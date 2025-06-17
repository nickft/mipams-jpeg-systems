package org.mipams.jumbf.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.services.boxes.JumbfBoxService;
import org.mipams.jumbf.util.JpegXLException;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.CorruptedJumbfFileException;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.UnsupportedContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpegXLParser implements ParserInterface {

    private static final Logger logger = Logger.getLogger(JpegXLParser.class.getName());

    enum JXL_BOXES {
        JXL_SIGNATURE_BOX_TYPE(0x4A584C20),
        JXL_FILE_TYPE_BOX_TYPE(0x66747970),
        JXL_LEVEL_BOX_TYPE(0x6A786C6C),
        JXL_EXIF_BOX_TYPE(0x6A786C6C),
        JXL_BROTLI_BOX_TYPE(0x62726F62),
        JXL_JXL_BOX_TYPE(0x6A786C63),
        JXL_PARTIAL_JXL_BOX_TYPE(0x6A786C70),
        JXL_BISTREAM_RECONSTRUCTION_BOX_TYPE(0x6A627264),
        JXL_FRAME_INDEX_BOX_TYPE(0x6A786C69),
        JXL_JUMBF_BOX_TYPE(JumbfBox.TYPE_ID);

        private int tBox;

        JXL_BOXES(int tBox) {
            this.tBox = tBox;
        }

        public int getTBox() {
            return tBox;
        }

        public static JXL_BOXES getTBoxFromInt(int tBoxId) throws JpegXLException {
            for (JXL_BOXES box : values()) {
                if (box.getTBox() == tBoxId) {
                    return box;
                }
            }

            throw new JpegXLException(String.format("Unsupported tBox: %d", tBoxId));
        }
    }

    @Autowired
    JumbfBoxService superBoxService;

    @Override
    public List<JumbfBox> parseMetadataFromFile(String assetUrl) throws MipamsException {
        String parentDirectory = CoreUtils.getParentDirectory(assetUrl);
        String tmpDirectory = CoreUtils.createSubdirectory(parentDirectory, CoreUtils.randomStringGenerator());

        ParseMetadata parseMetadata = new ParseMetadata();
        parseMetadata.setParentDirectory(tmpDirectory);

        Set<Integer> tBoxesEncountered = new HashSet<>();

        try (InputStream input = new BufferedInputStream(new FileInputStream(assetUrl), 16)) {
            parseJxlBox(input, JXL_BOXES.JXL_SIGNATURE_BOX_TYPE);

            parseJxlBox(input, JXL_BOXES.JXL_FILE_TYPE_BOX_TYPE);

            List<JumbfBox> bmffBoxList = new ArrayList<>();

            while (input.available() > 0) {
                int nextTBoxId = getNextBoxType(input);
                JXL_BOXES tBox = JXL_BOXES.getTBoxFromInt(nextTBoxId);

                switch (tBox) {
                    case JXL_JUMBF_BOX_TYPE:
                        logger.info("JUMBF box detected in jxl file.");
                        JumbfBox jumbfBox = parseJumbfBox(input, parseMetadata);
                        bmffBoxList.add(jumbfBox);
                        break;
                    case JXL_PARTIAL_JXL_BOX_TYPE:
                        if (tBoxesEncountered.contains(JXL_BOXES.JXL_JXL_BOX_TYPE.getTBox())) {
                            throw new JpegXLException(String.format(
                                    "JXL file cannot have both complete and partial JPEG XL Codestream boxes."));
                        }
                        parseJxlBox(input, tBox);
                        tBoxesEncountered.add(nextTBoxId);
                        break;
                    default:
                        if (tBoxesEncountered.contains(nextTBoxId)) {
                            throw new JpegXLException(
                                    String.format("JXL box type %d has been encountered multiple times", nextTBoxId));
                        }
                        parseJxlBox(input, tBox);
                        tBoxesEncountered.add(nextTBoxId);
                        break;
                }
            }
            return bmffBoxList;
        } catch (IOException e) {
            throw new CorruptedJumbfFileException("Could not open file: " + assetUrl, e);
        }
    }

    private JumbfBox parseJumbfBox(InputStream input, ParseMetadata parseMetadata)
            throws JpegXLException, UnsupportedContentTypeException {
        try {
            JumbfBox jumbfBox = superBoxService.parseSuperBox(input, parseMetadata);
            return jumbfBox;
        } catch (MipamsException e) {
            throw new JpegXLException(e);
        }
    }

    private void parseJxlBox(InputStream input, JXL_BOXES expectedBoxType) throws MipamsException {
        int lBox = CoreUtils.readIntFromInputStream(input);

        int tBox = CoreUtils.readIntFromInputStream(input);

        if (tBox != expectedBoxType.getTBox()) {
            throw new JpegXLException(String.format("Expected box %d, found box %d", expectedBoxType.getTBox(), tBox));
        }

        long boxRemainingBytes = lBox;

        if (lBox == 1) {
            boxRemainingBytes = CoreUtils.readLongFromInputStream(input);
            boxRemainingBytes -= CoreUtils.LONG_BYTE_SIZE;
        } else if (lBox != 0) {
            boxRemainingBytes -= 2 * CoreUtils.INT_BYTE_SIZE;
        }
        try {
            CoreUtils.skipBytesFromInputStream(input, boxRemainingBytes);
        } catch (IOException e) {
            throw new JpegXLException(String.format("Failed to skip the box with type %d", tBox));
        }
    }

    public int getNextBoxType(InputStream input) throws JpegXLException {
        input.mark(16);

        try {
            CoreUtils.readIntFromInputStream(input);
            int tBox = CoreUtils.readIntFromInputStream(input);

            if (!input.markSupported()) {
                throw new JpegXLException("Input Stream does not support marking.");
            }

            input.reset();
            return tBox;
        } catch (Exception e) {
            throw new JpegXLException(e);
        }
    }

    public boolean assetHasLevelBox(String assetUrl) throws MipamsException {
        try (InputStream input = new BufferedInputStream(new FileInputStream(assetUrl), 16)) {
            parseJxlBox(input, JXL_BOXES.JXL_SIGNATURE_BOX_TYPE);

            parseJxlBox(input, JXL_BOXES.JXL_FILE_TYPE_BOX_TYPE);

            int nextTBoxId = getNextBoxType(input);
            JXL_BOXES tBox = JXL_BOXES.getTBoxFromInt(nextTBoxId);

            return JXL_BOXES.JXL_LEVEL_BOX_TYPE.equals(tBox);
        } catch (IOException e) {
            throw new CorruptedJumbfFileException("Could not open file: " + assetUrl, e);
        }
    }
}