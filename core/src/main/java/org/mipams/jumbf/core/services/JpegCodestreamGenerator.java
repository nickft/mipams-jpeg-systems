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
import org.mipams.jumbf.core.util.JpegCodestreamException;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpegCodestreamGenerator {

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    Properties properties;

    public void generateJumbfMetadataToFile(List<JumbfBox> jumbfBoxList, String assetUrl, String outputUrl)
            throws JpegCodestreamException {

        Map<String, List<BoxSegment>> boxSegmentMap = jpegCodestreamParser.parseBoxSegmentMapFromFile(assetUrl);

        boolean alreadyEmbeddedJumbfBoxes = boxSegmentMap.size() > 0;

        addBoxSegmentsFromJumbfBoxList(boxSegmentMap, jumbfBoxList);

        String appMarkerAsHex;
        try (InputStream is = new FileInputStream(assetUrl);
                OutputStream os = new FileOutputStream(outputUrl);) {

            jpegCodestreamParser.handleStartOfImage(is, os);

            if (!alreadyEmbeddedJumbfBoxes) {
                appendNewBoxSegmentsToAsset(os, boxSegmentMap);
            }

            appMarkerAsHex = null;

            while (is.available() > 0) {

                if (appMarkerAsHex == null) {
                    appMarkerAsHex = CoreUtils.readTwoByteWordAsHex(is);
                    CoreUtils.writeByteArrayToOutputStream(DatatypeConverter.parseHexBinary(appMarkerAsHex), os);
                }

                if (CoreUtils.isEndOfImageAppMarker(appMarkerAsHex)) {
                    break;
                } else if (CoreUtils.isStartOfScanMarker(appMarkerAsHex)) {
                    appMarkerAsHex = jpegCodestreamParser.handleStartOfScanMarker(is, os);
                    continue;
                } else if (CoreUtils.isApp11Marker(appMarkerAsHex)) {
                    generateJumbfSegmentInApp11Marker(is, os, boxSegmentMap, jumbfBoxList.size());
                } else {
                    jpegCodestreamParser.handleNextMarkerToOutputStream(is, os);
                }

                appMarkerAsHex = null;
            }
        } catch (IOException | MipamsException e) {
            throw new JpegCodestreamException(e);
        }
    }

    private void generateJumbfSegmentInApp11Marker(InputStream is, OutputStream os,
            Map<String, List<BoxSegment>> boxSegmentMap, long numberOfNewJumbfBoxes) throws MipamsException {

        byte[] markerSegmentSizeAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        CoreUtils.writeByteArrayToOutputStream(markerSegmentSizeAsByteArray, os);
        int markerSegmentSize = CoreUtils.readTwoByteWordAsInt(markerSegmentSizeAsByteArray) - CoreUtils.WORD_BYTE_SIZE;

        byte[] commonIdentifierAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        markerSegmentSize -= CoreUtils.WORD_BYTE_SIZE;
        CoreUtils.writeByteArrayToOutputStream(commonIdentifierAsByteArray, os);
        String commonIdentifier = Integer.toHexString(CoreUtils.readTwoByteWordAsInt(commonIdentifierAsByteArray));

        if (!commonIdentifier.equalsIgnoreCase("4A50")) {
            CoreUtils.writeBytesFromInputStreamToOutputstream(is, markerSegmentSize, os);
            return;
        }

        byte[] boxInstanceNumberAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        markerSegmentSize -= CoreUtils.WORD_BYTE_SIZE;
        CoreUtils.writeByteArrayToOutputStream(boxInstanceNumberAsByteArray, os);
        int boxInstanceNumber = CoreUtils.readTwoByteWordAsInt(boxInstanceNumberAsByteArray);

        int packetSequenceNumber = CoreUtils.readIntFromInputStream(is);
        markerSegmentSize -= CoreUtils.INT_BYTE_SIZE;
        CoreUtils.writeIntToOutputStream(packetSequenceNumber, os);

        int boxLength = CoreUtils.readIntFromInputStream(is);
        markerSegmentSize -= CoreUtils.INT_BYTE_SIZE;
        CoreUtils.writeIntToOutputStream(boxLength, os);

        int boxType = CoreUtils.readIntFromInputStream(is);
        markerSegmentSize -= CoreUtils.INT_BYTE_SIZE;
        CoreUtils.writeIntToOutputStream(boxType, os);

        if (boxLength == 1) {
            long boxExtendedLength = CoreUtils.readLongFromInputStream(is);
            markerSegmentSize -= CoreUtils.LONG_BYTE_SIZE;
            CoreUtils.writeLongToOutputStream(boxExtendedLength, os);
        }

        CoreUtils.writeBytesFromInputStreamToOutputstream(is, markerSegmentSize, os);

        jpegCodestreamParser.deleteBoxSegment(boxSegmentMap, boxType, boxInstanceNumber, packetSequenceNumber);

        if (boxSegmentMap.size() == numberOfNewJumbfBoxes) {
            appendNewBoxSegmentsToAsset(os, boxSegmentMap);
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

    private void addBoxSegmentsFromJumbfBoxList(Map<String, List<BoxSegment>> boxSegmentMap,
            List<JumbfBox> jumbfBoxList) throws JpegCodestreamException {
        for (JumbfBox jumbfBox : jumbfBoxList) {

            int boxInstanceNumber = discoverBoxInstanceNumber(boxSegmentMap, jumbfBox);
            String boxSegmentId = String.format("%d-%d", jumbfBox.getTBox(), boxInstanceNumber);

            List<BoxSegment> boxSegmentList = getBoxSegmentsForJumbfBox(boxInstanceNumber, jumbfBox);

            boxSegmentMap.put(boxSegmentId, boxSegmentList);
        }
    }

    private int discoverBoxInstanceNumber(Map<String, List<BoxSegment>> boxSegmentMap, JumbfBox jumbfBox)
            throws JpegCodestreamException {

        int boxInstanceNumber = 1;

        int boxType = jumbfBox.getTBox();

        String provisionalBoxSegmentId;

        while (boxInstanceNumber <= CoreUtils.MAX_APP_SEGMENT_SIZE) {

            provisionalBoxSegmentId = String.format("%d-%d", boxType, boxInstanceNumber);

            if (boxSegmentMap.containsKey(provisionalBoxSegmentId)) {
                boxInstanceNumber++;
                continue;
            }

            return boxInstanceNumber;
        }

        throw new JpegCodestreamException("Could not issue a new box segment id");
    }

    private List<BoxSegment> getBoxSegmentsForJumbfBox(int boxInstanceNumber, JumbfBox jumbfBox)
            throws JpegCodestreamException {

        List<BoxSegment> boxSegmentList = new ArrayList<>();

        int bmffHeaderSize = CoreUtils.getBmffHeaderBuffer(jumbfBox.getLBox(), jumbfBox.getTBox(),
                jumbfBox.getXlBox()).length;

        String boxSegmentId = String.format("%d-%d", jumbfBox.getTBox(), boxInstanceNumber);
        String jumbfFileUrl = CoreUtils.getFullPath(properties.getFileDirectory(), boxSegmentId);

        try {
            coreGeneratorService.generateJumbfMetadataToFile(List.of(jumbfBox), jumbfFileUrl);
        } catch (MipamsException e) {
            throw new JpegCodestreamException(e);
        }

        int packetSequence = 1;

        int maximumSegmentPayloadSize = CoreUtils.getMaximumSizeForBmffBoxInAPPSegment();
        int remainingBytesInSegment = maximumSegmentPayloadSize;

        try (InputStream is = new FileInputStream(jumbfFileUrl)) {

            String boxSegmentPayloadUrl = CoreUtils.getFullPath(properties.getFileDirectory(),
                    boxSegmentId + "-" + packetSequence);

            while (is.available() > 0) {

                if (remainingBytesInSegment == 0) {
                    packetSequence++;
                    boxSegmentPayloadUrl = CoreUtils.getFullPath(properties.getFileDirectory(),
                            boxSegmentId + "-" + packetSequence);
                    remainingBytesInSegment = (packetSequence == 1) ? maximumSegmentPayloadSize
                            : maximumSegmentPayloadSize - bmffHeaderSize;
                }

                int segmentPayloadBytes = (is.available() >= remainingBytesInSegment) ? remainingBytesInSegment
                        : is.available();

                int segmentBytes = segmentPayloadBytes + CoreUtils.getAppSegmentHeaderSize();

                if (packetSequence > 1) {
                    segmentBytes += bmffHeaderSize;
                }

                CoreUtils.writeBytesFromInputStreamToFile(is, segmentPayloadBytes, boxSegmentPayloadUrl);

                BoxSegment boxSegment = new BoxSegment(segmentBytes, boxInstanceNumber, packetSequence,
                        jumbfBox.getLBox(), jumbfBox.getTBox(), jumbfBox.getXlBox(), boxSegmentPayloadUrl);

                boxSegmentList.add(boxSegment);

                remainingBytesInSegment -= segmentPayloadBytes;
            }

        } catch (MipamsException | IOException e) {
            throw new JpegCodestreamException(e);
        } finally {
            CoreUtils.deleteFile(jumbfFileUrl);
        }

        return boxSegmentList;
    }

    public void stripJumbfMetadataWithUuidEqualTo(String assetUrl, String outputUrl, String targetUuid)
            throws JpegCodestreamException {

        Map<String, List<BoxSegment>> boxSegmentMap = jpegCodestreamParser.parseBoxSegmentMapFromFile(assetUrl);

        String appMarkerAsHex;
        try (InputStream is = new FileInputStream(assetUrl);
                OutputStream os = new FileOutputStream(outputUrl);) {

            List<String> targetboxSegmentIdList = getBoxSegmentIdFromUuid(boxSegmentMap, targetUuid);

            jpegCodestreamParser.handleStartOfImage(is, os);

            while (is.available() > 0) {

                appMarkerAsHex = CoreUtils.readTwoByteWordAsHex(is);

                if (CoreUtils.isApp11Marker(appMarkerAsHex)) {
                    filterApp11MarkerBasedOnBoxSegmentIdList(is, os, appMarkerAsHex, targetboxSegmentIdList);
                } else {
                    CoreUtils.writeByteArrayToOutputStream(DatatypeConverter.parseHexBinary(appMarkerAsHex), os);

                    if (CoreUtils.isEndOfImageAppMarker(appMarkerAsHex)) {
                        break;
                    } else if (CoreUtils.isStartOfScanMarker(appMarkerAsHex)) {
                        jpegCodestreamParser.handleStartOfScanMarker(is, os);
                    } else {
                        jpegCodestreamParser.handleNextMarkerToOutputStream(is, os);
                    }
                }
            }

        } catch (MipamsException | IOException e) {
            throw new JpegCodestreamException(e);
        }
    }

    private List<String> getBoxSegmentIdFromUuid(Map<String, List<BoxSegment>> boxSegmentMap, String targetUuid)
            throws JpegCodestreamException {

        List<String> result = new ArrayList<>();

        if (targetUuid == null) {
            return result;
        }

        Map<String, JumbfBox> map = jpegCodestreamParser.mergeBoxSegmentsToJumbfBoxes(boxSegmentMap);

        JumbfBox jumbfBox;
        for (String boxInstanceNumber : map.keySet()) {

            jumbfBox = map.get(boxInstanceNumber);

            if (targetUuid.equalsIgnoreCase(jumbfBox.getDescriptionBox().getUuid())) {
                result.add(boxInstanceNumber);
            }
        }

        return result;
    }

    private void filterApp11MarkerBasedOnBoxSegmentIdList(InputStream is, OutputStream os,
            String appMarkerAsHex, List<String> targetBoxInstanceNumberList) throws MipamsException, IOException {

        byte[] markerSegmentSizeAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        int markerSegmentSize = CoreUtils.readTwoByteWordAsInt(markerSegmentSizeAsByteArray) - CoreUtils.WORD_BYTE_SIZE;

        byte[] commonIdentifierAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        markerSegmentSize -= CoreUtils.WORD_BYTE_SIZE;
        String commonIdentifier = Integer.toHexString(CoreUtils.readTwoByteWordAsInt(commonIdentifierAsByteArray));

        if (!commonIdentifier.equalsIgnoreCase("4A50")) {
            CoreUtils.writeByteArrayToOutputStream(markerSegmentSizeAsByteArray, os);
            CoreUtils.writeByteArrayToOutputStream(commonIdentifierAsByteArray, os);
            CoreUtils.writeBytesFromInputStreamToOutputstream(is, markerSegmentSize, os);
            return;
        }

        byte[] boxInstanceNumberAsByteArray = CoreUtils.readBytesFromInputStream(is, CoreUtils.WORD_BYTE_SIZE);
        markerSegmentSize -= CoreUtils.WORD_BYTE_SIZE;
        int boxInstanceNumber = CoreUtils.readTwoByteWordAsInt(boxInstanceNumberAsByteArray);

        int packetSequenceNumber = CoreUtils.readIntFromInputStream(is);
        markerSegmentSize -= CoreUtils.INT_BYTE_SIZE;

        int boxLength = CoreUtils.readIntFromInputStream(is);
        markerSegmentSize -= CoreUtils.INT_BYTE_SIZE;

        int boxType = CoreUtils.readIntFromInputStream(is);
        markerSegmentSize -= CoreUtils.INT_BYTE_SIZE;

        String boxSegmentId = CoreUtils.getBoxSegmentId(boxType, boxInstanceNumber);

        if (targetBoxInstanceNumberList.contains(boxSegmentId)) {
            is.skip(markerSegmentSize);
            return;
        }

        CoreUtils.writeByteArrayToOutputStream(DatatypeConverter.parseHexBinary(appMarkerAsHex), os);
        CoreUtils.writeByteArrayToOutputStream(markerSegmentSizeAsByteArray, os);
        CoreUtils.writeByteArrayToOutputStream(commonIdentifierAsByteArray, os);
        CoreUtils.writeByteArrayToOutputStream(boxInstanceNumberAsByteArray, os);
        CoreUtils.writeIntToOutputStream(packetSequenceNumber, os);
        CoreUtils.writeIntToOutputStream(boxLength, os);
        CoreUtils.writeIntToOutputStream(boxType, os);

        if (boxLength == 1) {
            long boxExtendedLength = CoreUtils.readLongFromInputStream(is);
            markerSegmentSize -= CoreUtils.LONG_BYTE_SIZE;
            CoreUtils.writeLongToOutputStream(boxExtendedLength, os);
        }

        CoreUtils.writeBytesFromInputStreamToOutputstream(is, markerSegmentSize, os);
    }
}
