package org.mipams.jumbf.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.ParseMetadata;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.Jp2CodestreamException;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;

public class Jp2CodestreamParser implements ParserInterface {
    private static final Logger logger = Logger.getLogger(Jp2CodestreamParser.class.getName());

    private static final int JP2_SIGNATURE_BOX_TYPE = 0x6A502020;
    private static final int JP2_FILE_TYPE_BOX_TYPE = 0x66747970;
    private static final int JP2_HEADER_BOX_TYPE = 0x6A703268;
    private static final int JP2_CONTIGUOUS_CODESTREAM_BOX_TYPE = 0x6A703263;

    @Autowired
    CoreParserService coreParserService;

    @Override
    public List<JumbfBox> parseMetadataFromFile(String assetUrl) throws Jp2CodestreamException {
        try {
            return parseJumbfBoxesFromFile(assetUrl);
        } catch (MipamsException e) {
            throw new Jp2CodestreamException(e);
        }
    }

    private List<JumbfBox> parseJumbfBoxesFromFile(String assetUrl) throws Jp2CodestreamException {

        List<JumbfBox> jumbfBoxes = new ArrayList<>();
        try (InputStream is = new BufferedInputStream(new FileInputStream(assetUrl), 8)) {

            parseSignatureBox(is);

            parseFileTypeBox(is);

            boolean headerBoxEncountered = false;

            while (is.available() > 0) {
                if (CoreUtils.getNextBoxType(is) == JP2_HEADER_BOX_TYPE) {
                    parseHeaderBox(is);
                    headerBoxEncountered = true;
                } else if (CoreUtils.getNextBoxType(is) == JP2_CONTIGUOUS_CODESTREAM_BOX_TYPE) {
                    if (!headerBoxEncountered) {
                        throw new Jp2CodestreamException(
                                "JP2 Header box shall be encountered before any Contiguous Codestream box.");
                    }
                    parseContiguousCodestreamBox(is);
                } else if (CoreUtils.getNextBoxType(is) == JumbfBox.TYPE_ID) {
                    String parentDirectory = CoreUtils.getParentDirectory(assetUrl);
                    String tmpDirectory = CoreUtils.createSubdirectory(parentDirectory,
                            CoreUtils.randomStringGenerator());

                    ParseMetadata parseMetadata = new ParseMetadata();
                    parseMetadata.setParentDirectory(tmpDirectory);
                    Optional<JumbfBox> jumbfBox = coreParserService.parseJumbfBoxFromInputStream(parseMetadata, is);
                    if (jumbfBox.isPresent()) {
                        jumbfBoxes.add(jumbfBox.get());
                    }
                } else {
                    parseGenericBox(is);
                }
            }

            return jumbfBoxes;
        } catch (IOException | MipamsException e) {
            throw new Jp2CodestreamException(e);
        }
    }

    private void parseSignatureBox(InputStream is) throws Jp2CodestreamException {

        try {
            int boxLength = CoreUtils.readIntFromInputStream(is);
            if (boxLength != 12) {
                throw new Jp2CodestreamException("Invalid box size");
            }

            int boxType = CoreUtils.readIntFromInputStream(is);
            if (boxType != JP2_SIGNATURE_BOX_TYPE) {
                throw new Jp2CodestreamException("Invalid box type");
            }

            int boxContent = CoreUtils.readIntFromInputStream(is);
            if (boxContent != 0x0D0A870A) {
                throw new Jp2CodestreamException("Invalid box content");
            }
        } catch (MipamsException e) {
            throw new Jp2CodestreamException("Failed to parse JPEG 2000 Signature Box", e);
        }
    }

    private void parseFileTypeBox(InputStream is) throws Jp2CodestreamException {
        try {
            long parsedBytes = 0;

            long boxLength = CoreUtils.readIntFromInputStream(is);
            parsedBytes += CoreUtils.INT_BYTE_SIZE;

            int boxType = CoreUtils.readIntFromInputStream(is);
            parsedBytes += CoreUtils.INT_BYTE_SIZE;
            if (boxType != JP2_FILE_TYPE_BOX_TYPE) {
                throw new Jp2CodestreamException("Invalid box type");
            }

            if (boxLength == 1L) {
                boxLength = CoreUtils.readLongFromInputStream(is);
                parsedBytes += CoreUtils.LONG_BYTE_SIZE;
            }

            int boxBrand = CoreUtils.readIntFromInputStream(is);
            parsedBytes += CoreUtils.INT_BYTE_SIZE;
            if (isSupportedBrand(boxBrand)) {
                logger.info(String.format("JP2: Parsing a %s file", Integer.toHexString(boxBrand)));
            } else {
                throw new Jp2CodestreamException(
                        String.format("Unsupported brand name %s", Integer.toHexString(boxBrand)));
            }

            if (boxLength > parsedBytes) {
                CoreUtils.readBytesFromInputStream(is, boxLength - parsedBytes);
            }

        } catch (MipamsException e) {
            throw new Jp2CodestreamException("Failed to parse JPEG 2000 File Type Box", e);
        }
    }

    private boolean isSupportedBrand(int boxBrand) {
        return boxBrand == 0x6A703220;
    }

    private void parseHeaderBox(InputStream is) throws Jp2CodestreamException {
        parseGenericBox(is);
    }

    private void parseContiguousCodestreamBox(InputStream is) throws Jp2CodestreamException {
        parseGenericBox(is);
    }

    private void parseGenericBox(InputStream is) throws Jp2CodestreamException {
        try {
            int boxType = CoreUtils.parseBoxAndGetType(is);
            logger.info(String.format("Box type: %s", Integer.toHexString(boxType)));
        } catch (MipamsException e) {
            throw new Jp2CodestreamException(e);
        }
    }

}
