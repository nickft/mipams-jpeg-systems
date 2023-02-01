package org.mipams.jumbf.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger; 
import java.util.logging.Level;

import org.mipams.jumbf.entities.BoxSegment;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.util.JpegCodestreamException;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpegCodestreamParser implements ParserInterface {

    private static final Logger logger = Logger.getLogger(JpegCodestreamParser.class.getName());

    @Autowired
    Properties properties;

    @Autowired
    CoreParserService coreParserService;

    @Override
    public List<JumbfBox> parseMetadataFromFile(String assetUrl) throws JpegCodestreamException {
        try {
            Map<String, List<BoxSegment>> boxSegmentMap = parseBoxSegmentMapFromFile(assetUrl);
            return new ArrayList<>(mergeBoxSegmentsToJumbfBoxes(boxSegmentMap).values());
        } catch (MipamsException e) {
            throw new JpegCodestreamException(e);
        }
    }

    public Map<String, List<BoxSegment>> parseBoxSegmentMapFromFile(String assetUrl) throws JpegCodestreamException {

        Map<String, List<BoxSegment>> boxSegmentMap = new HashMap<>();

        String appMarkerAsHex = null;
        try (InputStream is = new FileInputStream(assetUrl)) {

            handleStartOfImage(is, null);

            while (is.available() > 0) {

                if (appMarkerAsHex == null) {
                    appMarkerAsHex = CoreUtils.readTwoByteWordAsHex(is);
                }

                logger.log(Level.FINE,appMarkerAsHex);

                if (CoreUtils.isEndOfImageAppMarker(appMarkerAsHex)) {
                    break;
                } else if (CoreUtils.isStartOfScanMarker(appMarkerAsHex)) {
                    appMarkerAsHex = handleStartOfScanMarker(is, null);
                } else if (CoreUtils.isApp11Marker(appMarkerAsHex)) {
                    parseJumbfSegmentInApp11Marker(is, boxSegmentMap);
                } else {
                    handleNextMarkerToOutputStream(is, null);
                }

                appMarkerAsHex = null;
            }

            return boxSegmentMap;
        } catch (IOException | MipamsException e) {
            throw new JpegCodestreamException(e);
        }
    }

    public void handleStartOfImage(InputStream is, OutputStream os) throws MipamsException {
        int appMarker = CoreUtils.readTwoByteWordAsInt(is);

        String appMarkerAsHex = Integer.toHexString(appMarker);

        if (!CoreUtils.isStartOfImageAppMarker(appMarkerAsHex)) {
            throw new JpegCodestreamException("Start of image (SOI) marker is missing.");
        }

        if (os != null) {
            CoreUtils.writeIntAsTwoByteToOutputStream(appMarker, os);
        }
    }

    public String handleStartOfScanMarker(InputStream is, OutputStream os) throws IOException, MipamsException {

        int currentByte, previousByte = 0;

        while (is.available() > 0) {

            currentByte = CoreUtils.readSingleByteAsIntFromInputStream(is);

            if (os != null) {
                CoreUtils.writeIntAsSingleByteToOutputStream(currentByte, os);
            }

            if (checkTerminationOfScanMarker(previousByte, currentByte)) {
                return "ff" + Integer.toHexString(currentByte);
            }

            previousByte = currentByte;

        }

        return "ffd9";
    }

    private boolean checkTerminationOfScanMarker(int previousByte, int currentByte) {

        boolean previousByteTerminationCondition = previousByte == 0xFF;

        boolean currentByteTerminationCondition = (currentByte != 0xFF) && (currentByte > 0x00)
                && !(currentByte >= 0xD0 && currentByte <= 0xD7);

        return previousByteTerminationCondition && currentByteTerminationCondition;
    }

    private void parseJumbfSegmentInApp11Marker(InputStream is, Map<String, List<BoxSegment>> boxSegmentMap)
            throws MipamsException, IOException {

        int nominalMarkerSegmentSize = CoreUtils.readTwoByteWordAsInt(is);
        int markerSegmentRemainingSize = nominalMarkerSegmentSize - CoreUtils.WORD_BYTE_SIZE;

        byte[] commonIdentifierAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        markerSegmentRemainingSize -= CoreUtils.WORD_BYTE_SIZE;

        String commonIdentifier = Integer.toHexString(CoreUtils.readTwoByteWordAsInt(commonIdentifierAsByteArray));

        if (!commonIdentifier.equalsIgnoreCase("4A50")) {
            is.skip(markerSegmentRemainingSize);
            return;
        }

        int boxInstanceNumber = CoreUtils.readTwoByteWordAsInt(is);
        markerSegmentRemainingSize -= CoreUtils.WORD_BYTE_SIZE;

        int packetSequenceNumber = CoreUtils.readIntFromInputStream(is);
        markerSegmentRemainingSize -= CoreUtils.INT_BYTE_SIZE;

        int boxLength = CoreUtils.readIntFromInputStream(is);
        markerSegmentRemainingSize -= CoreUtils.INT_BYTE_SIZE;

        int boxType = CoreUtils.readIntFromInputStream(is);
        markerSegmentRemainingSize -= CoreUtils.INT_BYTE_SIZE;

        Long boxLengthExtension = null;

        if (boxLength == 1) {
            boxLengthExtension = CoreUtils.readLongFromInputStream(is);
            markerSegmentRemainingSize -= CoreUtils.LONG_BYTE_SIZE;
        }

        String boxSegmentId = CoreUtils.getBoxSegmentId(boxType, boxInstanceNumber);

        String randomFileName = CoreUtils.randomStringGenerator();
        String payloadFileUrl = CoreUtils.getFullPath(properties.getFileDirectory(), randomFileName);

        CoreUtils.writeBytesFromInputStreamToFile(is, markerSegmentRemainingSize, payloadFileUrl);

        BoxSegment boxSegment = new BoxSegment(nominalMarkerSegmentSize, boxInstanceNumber, packetSequenceNumber,
                boxLength, boxType, boxLengthExtension, payloadFileUrl);

        addBoxSegmentToMap(boxSegmentMap, boxSegmentId, boxSegment);
    }

    private void addBoxSegmentToMap(Map<String, List<BoxSegment>> boxSegmentMap, String boxSegmentId,
            BoxSegment boxSegment) {

        List<BoxSegment> boxSegmentList = boxSegmentMap.get(boxSegmentId);

        if (boxSegmentList == null) {
            boxSegmentList = new ArrayList<>();
        }

        boxSegmentList.add(boxSegment);

        boxSegmentMap.put(boxSegmentId, boxSegmentList);
    }

    public Map<String, JumbfBox> mergeBoxSegmentsToJumbfBoxes(Map<String, List<BoxSegment>> boxSegmentMap)
            throws JpegCodestreamException {

        Map<String, JumbfBox> result = new HashMap<>();

        Map<String, List<BoxSegment>> tempBoxSegmentMap = new HashMap<>(boxSegmentMap);

        for (String boxSegmentId : tempBoxSegmentMap.keySet()) {

            String jumbfFileUrl = CoreUtils.getFullPath(properties.getFileDirectory(), boxSegmentId + ".jumbf");

            List<BoxSegment> boxSegmentList = boxSegmentMap.get(boxSegmentId);

            Collections.sort(boxSegmentList);

            try (OutputStream os = new FileOutputStream(jumbfFileUrl)) {

                BoxSegment bs = boxSegmentList.get(0);

                byte[] bmffHeader = CoreUtils.getBmffHeaderBuffer(bs.getLBox(), bs.getTBox(), bs.getXlBox());

                CoreUtils.writeByteArrayToOutputStream(bmffHeader, os);

                for (BoxSegment boxSegment : new ArrayList<>(boxSegmentList)) {
                    CoreUtils.writeFileContentToOutput(boxSegment.getPayloadUrl(), os);
                    deleteBoxSegment(boxSegmentMap, boxSegment.getTBox(), boxSegment.getBoxInstanceNumber(),
                            boxSegment.getPacketSequenceNumber());
                }

                List<JumbfBox> boxList = coreParserService.parseMetadataFromFile(jumbfFileUrl);
                result.put(boxSegmentId, boxList.get(0));
            } catch (MipamsException | IOException e) {
                throw new JpegCodestreamException(e);
            }
        }

        return result;
    }

    public void deleteBoxSegment(Map<String, List<BoxSegment>> boxSegmentMap, int boxType, int boxInstanceNumber,
            int packetSequenceNumber) {

        String boxSegmentId = String.format("%d-%d", boxType, boxInstanceNumber);

        List<BoxSegment> boxSegmentList = boxSegmentMap.get(boxSegmentId);

        for (BoxSegment bs : new ArrayList<>(boxSegmentList)) {
            if (bs.getPacketSequenceNumber() == packetSequenceNumber) {
                boxSegmentList.remove(bs);
                CoreUtils.deleteFile(bs.getPayloadUrl());
            }
        }

        if (boxSegmentList.isEmpty()) {
            boxSegmentMap.remove(boxSegmentId);
        }
    }

    public void handleNextMarkerToOutputStream(InputStream is, OutputStream os) throws MipamsException, IOException {

        byte[] appMarkerAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        int markerSegmentSize = CoreUtils.readTwoByteWordAsInt(appMarkerAsByteArray);

        if (os == null) {
            is.skip(markerSegmentSize - CoreUtils.WORD_BYTE_SIZE);
        } else {
            CoreUtils.writeByteArrayToOutputStream(appMarkerAsByteArray, os);
            CoreUtils.writeBytesFromInputStreamToOutputstream(is, markerSegmentSize - CoreUtils.WORD_BYTE_SIZE, os);
        }
    }
}