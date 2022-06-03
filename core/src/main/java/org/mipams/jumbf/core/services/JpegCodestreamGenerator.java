package org.mipams.jumbf.core.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.mipams.jumbf.core.entities.BoxSegment;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpegCodestreamGenerator implements GeneratorInterface {

    private static int MAX_APP_SIZE = 0xFFFF;

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    Properties properties;

    @Override
    public String generateJumbfMetadataToFile(List<JumbfBox> jumbfBoxList, String assetUrl) throws MipamsException {

        Map<String, List<BoxSegment>> boxSegmentMap = jpegCodestreamParser.parseBoxSegmentMapFromFile(assetUrl);

        boolean alreadyEmbeddedJumbfBoxes = boxSegmentMap.size() > 0;

        addBoxSegmentsFromJumbfBoxList(boxSegmentMap, jumbfBoxList);

        String resultAssetUrl = CoreUtils.getFullPath(properties.getFileDirectory(),
                assetUrl.substring(assetUrl.lastIndexOf("/") + 1) + "-new");

        String appMarkerAsHex;
        try (InputStream is = new FileInputStream(assetUrl);
                OutputStream os = new FileOutputStream(resultAssetUrl);) {

            appMarkerAsHex = CoreUtils.readTwoByteWordAsHex(is);

            if (!CoreUtils.isStartOfImageAppMarker(appMarkerAsHex)) {
                throw new MipamsException("Start of image (SOI) marker is missing.");
            }

            CoreUtils.writeByteArrayToOutputStream(DatatypeConverter.parseHexBinary(appMarkerAsHex), os);

            if (!alreadyEmbeddedJumbfBoxes) {
                appendNewBoxSegmentsToAsset(os, boxSegmentMap);
            }

            while (is.available() > 0) {

                appMarkerAsHex = CoreUtils.readTwoByteWordAsHex(is);
                CoreUtils.writeByteArrayToOutputStream(DatatypeConverter.parseHexBinary(appMarkerAsHex), os);

                if (CoreUtils.isEndOfImageAppMarker(appMarkerAsHex)) {
                    break;
                } else if (CoreUtils.isStartOfScanMarker(appMarkerAsHex)) {
                    copyStartOfScanMarker(is, os);
                } else if (CoreUtils.isApp11Marker(appMarkerAsHex)) {
                    generateJumbfSegmentInApp11Marker(is, os, boxSegmentMap, jumbfBoxList.size());
                } else {
                    copyNextMarkerToOutputStream(is, os);
                }
            }

            return resultAssetUrl;
        } catch (IOException e) {
            throw new MipamsException(e);
        }
    }

    private void copyStartOfScanMarker(InputStream is, OutputStream os) throws MipamsException, IOException {

        int currentByte, previousByte = 0;

        while (is.available() > 0) {

            currentByte = CoreUtils.readSingleByteAsIntFromInputStream(is);
            CoreUtils.writeIntAsSingleByteToOutputStream(currentByte, os);

            if (previousByte == 0xFF && currentByte != 0xFF && currentByte > 0x00
                    && !(currentByte >= 0xD0 && currentByte <= 0xD7)) {
                break;
            }

            previousByte = currentByte;
        }
    }

    private void generateJumbfSegmentInApp11Marker(InputStream is, OutputStream os,
            Map<String, List<BoxSegment>> boxSegmentMap, long numberOfNewJumbfBoxes) throws MipamsException {

        byte[] appMarkerAsByteArray = CoreUtils.readBytesFromInputStream(is, 2);
        CoreUtils.writeByteArrayToOutputStream(appMarkerAsByteArray, os);

        int markerSegmentSize = CoreUtils.readTwoByteWordAsInt(appMarkerAsByteArray);

        byte[] commonIdentifierAsByteArray = CoreUtils.readBytesFromInputStream(is, 2);
        CoreUtils.writeByteArrayToOutputStream(commonIdentifierAsByteArray, os);

        String commonIdentifier = Integer.toHexString(CoreUtils.readTwoByteWordAsInt(commonIdentifierAsByteArray));

        if (!commonIdentifier.equalsIgnoreCase("4A50")) {
            writeNextBytesToOutputStream(is, os, markerSegmentSize - 2 - 2);
            return;
        }

        byte[] boxInstanceNumberAsByteArray = CoreUtils.readBytesFromInputStream(is, 2);
        CoreUtils.writeByteArrayToOutputStream(boxInstanceNumberAsByteArray, os);

        int boxInstanceNumber = CoreUtils.readTwoByteWordAsInt(boxInstanceNumberAsByteArray);

        int packetSequenceNumber = CoreUtils.readIntFromInputStream(is);
        CoreUtils.writeIntToOutputStream(packetSequenceNumber, os);

        int boxLength = CoreUtils.readIntFromInputStream(is);
        CoreUtils.writeIntToOutputStream(boxLength, os);

        int boxType = CoreUtils.readIntFromInputStream(is);
        CoreUtils.writeIntToOutputStream(boxType, os);

        writeNextBytesToOutputStream(is, os, markerSegmentSize - (2 + 2 + 2 + 4 + 4 + 4));

        deleteBoxSegment(boxSegmentMap, boxType, boxInstanceNumber, packetSequenceNumber);

        if (boxSegmentMap.size() == numberOfNewJumbfBoxes) {
            appendNewBoxSegmentsToAsset(os, boxSegmentMap);
        }
    }

    private void deleteBoxSegment(Map<String, List<BoxSegment>> boxSegmentMap, int boxType, int boxInstanceNumber,
            int packetSequenceNumber) {

        String boxSegmentId = String.format("%d-%d", boxType, boxInstanceNumber);

        List<BoxSegment> boxSegmentList = boxSegmentMap.get(boxSegmentId);

        for (BoxSegment bs : new ArrayList<>(boxSegmentList)) {
            if (bs.getPacketSequenceNumber() == packetSequenceNumber) {
                boxSegmentList.remove(bs);
            }
        }

        if (boxSegmentList.isEmpty()) {
            boxSegmentMap.remove(boxSegmentId);
        }
    }

    private void appendNewBoxSegmentsToAsset(OutputStream os, Map<String, List<BoxSegment>> boxSegmentMap)
            throws MipamsException {

        boolean firstSegmentForJumbf;

        for (String key : boxSegmentMap.keySet()) {

            firstSegmentForJumbf = true;

            for (BoxSegment bs : boxSegmentMap.get(key)) {
                writeBoxSegmentToOutputStream(os, bs, firstSegmentForJumbf);
                firstSegmentForJumbf = false;
            }
        }
    }

    private void writeBoxSegmentToOutputStream(OutputStream os, BoxSegment boxSegment, boolean isFirstSegmentForJumbf)
            throws MipamsException {

        byte[] markerId = DatatypeConverter.parseHexBinary("FFEB");
        CoreUtils.writeByteArrayToOutputStream(markerId, os);

        byte[] length = CoreUtils.convertIntToTwoByteArray(boxSegment.getSize());
        CoreUtils.writeByteArrayToOutputStream(length, os);

        byte[] commonIdentifier = DatatypeConverter.parseHexBinary("4A50");
        CoreUtils.writeByteArrayToOutputStream(commonIdentifier, os);

        byte[] boxInstance = CoreUtils.convertIntToTwoByteArray(boxSegment.getBoxInstanceNumber());
        CoreUtils.writeByteArrayToOutputStream(boxInstance, os);

        CoreUtils.writeIntToOutputStream(boxSegment.getPacketSequenceNumber(), os);

        if (!isFirstSegmentForJumbf) {
            byte[] bmffHeader = CoreUtils.getBmffHeaderBuffer(boxSegment.getLBox(), boxSegment.getTBox(),
                    boxSegment.getXlBox());
            CoreUtils.writeByteArrayToOutputStream(bmffHeader, os);
        }

        CoreUtils.writeFileContentToOutput(boxSegment.getPayloadUrl(), os);

        CoreUtils.deleteFile(boxSegment.getPayloadUrl());
    }

    private void copyNextMarkerToOutputStream(InputStream is, OutputStream os) throws MipamsException {
        byte[] appMarkerAsByteArray = CoreUtils.readBytesFromInputStream(is, 2);

        int markerSegmentSize = CoreUtils.readTwoByteWordAsInt(appMarkerAsByteArray);
        CoreUtils.writeByteArrayToOutputStream(appMarkerAsByteArray, os);

        writeNextBytesToOutputStream(is, os, markerSegmentSize - 2);
    }

    private void writeNextBytesToOutputStream(InputStream is, OutputStream os, long numberOfBytes)
            throws MipamsException {

        CoreUtils.writeBytesFromInputStreamToOutputstream(is, numberOfBytes, os);
    }

    private void addBoxSegmentsFromJumbfBoxList(Map<String, List<BoxSegment>> boxSegmentMap,
            List<JumbfBox> jumbfBoxList) throws MipamsException {
        for (JumbfBox jumbfBox : jumbfBoxList) {

            int boxInstanceNumber = discoverBoxInstanceNumber(boxSegmentMap, jumbfBox);
            String boxSegmentId = String.format("%d-%d", jumbfBox.getTBox(), boxInstanceNumber);

            List<BoxSegment> boxSegmentList = getBoxSegmentsForJumbfBox(boxInstanceNumber, jumbfBox);

            boxSegmentMap.put(boxSegmentId, boxSegmentList);
        }
    }

    private int discoverBoxInstanceNumber(Map<String, List<BoxSegment>> boxSegmentMap, JumbfBox jumbfBox)
            throws MipamsException {

        int boxInstanceNumber = 1;

        int boxType = jumbfBox.getTBox();

        String provisionalBoxSegmentId;

        while (boxInstanceNumber <= MAX_APP_SIZE) {

            provisionalBoxSegmentId = String.format("%d-%d", boxType, boxInstanceNumber);

            if (boxSegmentMap.containsKey(provisionalBoxSegmentId)) {
                boxInstanceNumber++;
                continue;
            }

            return boxInstanceNumber;
        }

        throw new MipamsException("Could not issue a new box segment id");
    }

    private List<BoxSegment> getBoxSegmentsForJumbfBox(int boxInstanceNumber, JumbfBox jumbfBox)
            throws MipamsException {

        List<BoxSegment> boxSegmentList = new ArrayList<>();

        String boxSegmentId = String.format("%d-%d", jumbfBox.getTBox(), boxInstanceNumber);
        String jumbfFileUrl = CoreUtils.getFullPath(properties.getFileDirectory(), boxSegmentId);

        coreGeneratorService.generateJumbfMetadataToFile(List.of(jumbfBox), jumbfFileUrl);

        int packetSequence = 1;

        int maximumSegmentPayloadSize = MAX_APP_SIZE - (2 + 2 + 2 + 4);

        int remainingBytesInSegment = maximumSegmentPayloadSize;

        try (InputStream is = new FileInputStream(jumbfFileUrl)) {

            String boxSegmentPayloadUrl = CoreUtils.getFullPath(properties.getFileDirectory(),
                    boxSegmentId + "-" + packetSequence);

            while (is.available() > 0) {

                if (remainingBytesInSegment == 0) {
                    packetSequence++;
                    boxSegmentPayloadUrl = CoreUtils.getFullPath(properties.getFileDirectory(),
                            boxSegmentId + "-" + packetSequence);
                    remainingBytesInSegment = maximumSegmentPayloadSize;
                }

                int segmentPayloadBytes = (is.available() >= remainingBytesInSegment) ? remainingBytesInSegment
                        : is.available();

                int segmentBytes = segmentPayloadBytes + (2 + 2 + 2 + 4);

                if (packetSequence > 1) {
                    segmentBytes += 4 + 4 + (jumbfBox.getXlBox() != null ? 8 : 0);
                }

                CoreUtils.writeBytesFromInputStreamToFile(is, segmentPayloadBytes, boxSegmentPayloadUrl);

                BoxSegment boxSegment = new BoxSegment(segmentBytes, boxInstanceNumber, packetSequence,
                        jumbfBox.getLBox(), jumbfBox.getTBox(), jumbfBox.getXlBox(), boxSegmentPayloadUrl);

                boxSegmentList.add(boxSegment);

                remainingBytesInSegment -= segmentPayloadBytes;
            }

        } catch (IOException e) {
            throw new MipamsException(e);
        } finally {
            CoreUtils.deleteFile(jumbfFileUrl);
        }

        return boxSegmentList;
    }
}
