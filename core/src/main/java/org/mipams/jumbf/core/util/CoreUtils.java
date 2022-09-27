package org.mipams.jumbf.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.PaddingBox;
import org.mipams.jumbf.core.entities.PrivateBox;
import org.springframework.http.MediaType;

public class CoreUtils {
    public static final int INT_BYTE_SIZE = 4;

    public static final int LONG_BYTE_SIZE = 8;

    public static final int UUID_BYTE_SIZE = 16;

    public static final int WORD_BYTE_SIZE = 2;

    public static final int MAX_APP_SEGMENT_SIZE = 0xFFFF;

    // public static final String

    public static void writeFileContentToOutput(String path, OutputStream outputStream) throws MipamsException {

        try (FileInputStream inputStream = new FileInputStream(path)) {
            int n;
            while ((n = inputStream.read()) != -1) {
                outputStream.write(n);
            }
        } catch (IOException e) {
            throw new MipamsException(e);
        }
    }

    public static void writeIntToOutputStream(int val, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertIntToByteArray(val), outputStream);
    }

    public static void writeIntAsTwoByteToOutputStream(int val, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertIntToTwoByteArray(val), outputStream);
    }

    public static void writeIntAsSingleByteToOutputStream(int val, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertIntToSingleElementByteArray(val), outputStream);
    }

    public static void writeLongToOutputStream(long val, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertLongToByteArray(val), outputStream);
    }

    public static void writeTextToOutputStream(String text, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertStringToByteArray(text), outputStream);
    }

    public static void writeUuidToOutputStream(String uuid, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertUUIDToByteArray(uuid), outputStream);
    }

    public static void writeByteArrayToOutputStream(byte[] input, OutputStream outputStream) throws MipamsException {
        try {
            outputStream.write(input);
        } catch (IOException e) {
            throw new MipamsException("Could not write to file", e);
        }
    }

    public static int convertByteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] convertIntToByteArray(int num) {
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    public static byte[] convertIntToTwoByteArray(int num) {

        byte[] result = new byte[2];

        result[1] = (byte) num;
        result[0] = (byte) (num >> 8);

        return result;
    }

    public static byte[] convertIntToSingleElementByteArray(int num) {
        return ByteBuffer.allocate(1).put((byte) num).array();
    }

    public static long convertByteArrayToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static byte[] convertLongToByteArray(long num) {
        return ByteBuffer.allocate(8).putLong(num).array();
    }

    public static byte[] convertStringToByteArray(String text) {
        return text.getBytes();
    }

    public static String parseStringFromFile(String fileUrl) throws MipamsException {
        try {
            try (InputStream inputStream = new FileInputStream(fileUrl)) {
                return readStringFromInputStream(inputStream);
            }
        } catch (IOException e) {
            throw new MipamsException("Could not close input stream", e);
        }

    }

    public static String readStringFromInputStream(InputStream input) throws MipamsException {
        char charVal;
        StringBuilder str = new StringBuilder();

        int n;

        try {
            while ((n = input.read()) != -1) {

                charVal = (char) n;

                if (charVal == '\0') {
                    break;
                }

                str.append(charVal);
            }
        } catch (IOException e) {
            throw new MipamsException("Could not read from file", e);
        }

        return str.toString();
    }

    public static int readSingleByteAsIntFromInputStream(InputStream input) throws MipamsException {
        byte[] intBuffer = readBytesFromInputStream(input, 1);
        return Byte.toUnsignedInt(intBuffer[0]);
    }

    public static int readIntFromInputStream(InputStream input) throws MipamsException {
        byte[] intBuffer = readBytesFromInputStream(input, INT_BYTE_SIZE);
        return convertByteArrayToInt(intBuffer);
    }

    public static long readLongFromInputStream(InputStream input) throws MipamsException {
        byte[] longBuffer = readBytesFromInputStream(input, LONG_BYTE_SIZE);
        return convertByteArrayToLong(longBuffer);
    }

    public static String readUuidFromInputStream(InputStream input) throws MipamsException {
        byte[] uuidTemp = readBytesFromInputStream(input, UUID_BYTE_SIZE);
        return convertByteArrayToUUID(uuidTemp).toString().toUpperCase();
    }

    public static byte[] readBytesFromInputStream(InputStream input, long numberOfBytes) throws MipamsException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        writeBytesFromInputStreamToOutputstream(input, numberOfBytes, outputStream);

        if (numberOfBytes != outputStream.toByteArray().length) {
            throw new MipamsException("Failed to read requested bytes from input stream");
        }

        return outputStream.toByteArray();
    }

    public static String readTwoByteWordAsHex(InputStream input) throws MipamsException {
        return Integer.toHexString(readTwoByteWordAsInt(input));
    }

    public static int readTwoByteWordAsInt(InputStream input) throws MipamsException {
        byte[] byteArray = CoreUtils.readBytesFromInputStream(input, 2);
        return readTwoByteWordAsInt(byteArray);
    }

    public static int readTwoByteWordAsInt(byte[] byteArray) throws MipamsException {

        byte[] fullWordByteArray = new byte[4];

        System.arraycopy(byteArray, 0, fullWordByteArray, 2, 2);
        return convertByteArrayToInt(fullWordByteArray);
    }

    public static UUID convertByteArrayToUUID(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid;
    }

    public static byte[] convertUUIDToByteArray(String uuid) {

        UUID uuidVal = UUID.fromString(uuid);

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);

        bb.putLong(uuidVal.getMostSignificantBits());
        bb.putLong(uuidVal.getLeastSignificantBits());

        return bb.array();
    }

    public static boolean isBitAtGivenPositionSet(int n, int position) {
        int new_num = n >> position;

        return (new_num & 1) == 1;
    }

    public static int setBitValueAtGivenPosition(int n, int position, int val) {
        int mask = 1 << position;
        return (n & ~mask) | ((val << position) & mask);
    }

    public static long getFileSizeFromPath(String filePath) throws MipamsException {
        File f = new File(filePath);
        return f.length();
    }

    public static String randomStringGenerator() {
        return UUID.randomUUID().toString();
    }

    public static String getFullPath(String folder, String fileName) {
        StringBuilder fullPath = new StringBuilder(folder);

        if (!folder.endsWith("/"))
            fullPath.append("/");

        fullPath.append(fileName);

        return fullPath.toString();
    }

    public static String addEscapeCharacterToText(String text) {
        return text + "\0";
    }

    public static void writeBytesFromInputStreamToFile(InputStream input, long nominalTotalSizeInBytes,
            String fileUrl) throws MipamsException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileUrl)) {
            writeBytesFromInputStreamToOutputstream(input, nominalTotalSizeInBytes, fileOutputStream);
        } catch (IOException e) {
            throw new MipamsException("Could not read content", e);
        }
    }

    public static void writeBytesFromInputStreamToOutputstream(InputStream input, long nominalTotalSizeInBytes,
            OutputStream outputStream) throws MipamsException {

        try {
            int actualBytes = 0, n;

            while ((nominalTotalSizeInBytes == 0 || actualBytes < nominalTotalSizeInBytes)
                    && ((n = input.read()) != -1)) {
                outputStream.write(n);
                actualBytes++;
            }

        } catch (IOException e) {
            throw new MipamsException("Could not read content", e);
        }
    }

    public static MediaType getMediaTypeFromString(String input) throws IllegalArgumentException {
        MediaType mediaType = MediaType.valueOf(input);
        return mediaType;
    }

    public static void writePaddingToOutputStream(long numberOfBytes, int paddingValue,
            OutputStream outputStream)
            throws MipamsException {

        try {
            int i = 0;
            while (i < numberOfBytes) {
                outputStream.write(paddingValue);
                i++;
            }
        } catch (IOException e) {
            throw new MipamsException("Could not write to file", e);
        }

    }

    public static long parsePaddingFromInputStream(InputStream input, int paddingValue, long availableBytesForBox)
            throws MipamsException {
        try {

            int actualBytes = 0, n;

            while ((actualBytes < availableBytesForBox) && ((n = input.read()) != -1)) {
                if (n != paddingValue) {
                    throw new MipamsException("Padding is corrupted. It should be contain only values of 0x00");
                }
                actualBytes++;
            }

            return actualBytes;
        } catch (IOException e) {
            throw new MipamsException("Could not read content", e);
        }
    }

    public static int numberOfHexCharsToRepresentLong(long size) {
        return Long.toHexString(size).length();
    }

    public static byte[] getBmffHeaderBuffer(int boxLength, int boxType, Long boxLengthExtension) {

        ByteBuffer bb;

        if (boxLengthExtension != null) {
            bb = ByteBuffer.wrap(new byte[INT_BYTE_SIZE * 2 + LONG_BYTE_SIZE]);
            bb.put(convertIntToByteArray(boxLength));
            bb.put(convertIntToByteArray(boxType));
            bb.put(convertLongToByteArray(boxLengthExtension));
        } else {
            bb = ByteBuffer.wrap(new byte[INT_BYTE_SIZE * 2]);
            bb.put(convertIntToByteArray(boxLength));
            bb.put(convertIntToByteArray(boxType));
        }

        return bb.array();
    }

    public static String createSubdirectory(String parentDirectory, String subDirectory) throws MipamsException {

        String subDirectoryPath = CoreUtils.getFullPath(parentDirectory, subDirectory);

        File f = new File(subDirectoryPath);

        if (!f.exists()) {
            f.mkdirs();
        } else {
            if (!f.isDirectory()) {
                throw new MipamsException("Failed to create subdirectory: There is already a file with the same name");
            }
        }

        return subDirectoryPath;
    }

    public static boolean deleteDir(String dirUrl) {

        File dir = new File(dirUrl);
        if (!dir.exists()) {
            return true;
        }

        for (String name : dir.list()) {

            String absolutePath = getFullPath(dir.getAbsolutePath(), name);

            File t = new File(absolutePath);

            if (t.isDirectory()) {
                deleteDir(absolutePath);
            } else {
                deleteFile(absolutePath);
            }
        }

        return dir.delete();
    }

    public static boolean deleteFile(String fileUrl) {
        File f = new File(fileUrl);
        if (!f.exists()) {
            return true;
        }

        return f.delete();
    }

    public static boolean isStartOfImageAppMarker(String appMarkerAsHex) {
        return appMarkerAsHex.equalsIgnoreCase("FFD8");
    }

    public static boolean isEndOfImageAppMarker(String appMarkerAsHex) {
        return appMarkerAsHex.equalsIgnoreCase("FFD9");
    }

    public static boolean isApp11Marker(String appMarkerAsHex) {
        return appMarkerAsHex.equalsIgnoreCase("FFEB");
    }

    public static boolean isStartOfScanMarker(String appMarkerAsHex) {
        return appMarkerAsHex.equalsIgnoreCase("FFDA");
    }

    public static int getMaximumSizeForBmffBoxInAPPSegment() {
        return MAX_APP_SEGMENT_SIZE - getAppSegmentHeaderSize();
    }

    public static int getAppSegmentHeaderSize() {
        int segmentSizeByteLength = WORD_BYTE_SIZE;

        int commonIdentifierByteLength = WORD_BYTE_SIZE;

        int boxInstanceNumberByteLength = WORD_BYTE_SIZE;

        int packetSequenceNumberByteLength = INT_BYTE_SIZE;

        return segmentSizeByteLength + commonIdentifierByteLength + boxInstanceNumberByteLength
                + packetSequenceNumberByteLength;
    }

    public static String getBoxSegmentId(int boxType, int boxInstanceNumber) {
        return String.format("%d-%d", boxType, boxInstanceNumber);
    }

    public static JumbfBox locateJumbfBoxFromLabel(List<JumbfBox> assertionJumbfBoxList, String label)
            throws MipamsException {
        JumbfBox result = null;

        if (label == null) {
            return result;
        }

        for (JumbfBox jumbfBox : assertionJumbfBoxList) {
            if (label.equals(jumbfBox.getDescriptionBox().getLabel())) {
                result = jumbfBox;
                break;
            }
        }

        return result;
    }

    public static boolean isPaddingBoxNext(InputStream input) throws MipamsException {

        input.mark(16);

        readIntFromInputStream(input);
        int tBox = readIntFromInputStream(input);

        boolean result = false;

        PaddingBox paddingBox = new PaddingBox();

        try {
            result = (tBox == paddingBox.getTypeId());

            if (!input.markSupported()) {
                throw new MipamsException("Input Stream does not support marking.");
            }

            input.reset();
        } catch (IOException e) {
            throw new MipamsException(e);
        }

        return result;
    }

    public static boolean isPrivateBoxNext(InputStream input) throws MipamsException {

        input.mark(16);

        readIntFromInputStream(input);
        int tBox = readIntFromInputStream(input);

        boolean result = false;

        PrivateBox privateBox = new PrivateBox();

        try {
            result = (tBox == privateBox.getTypeId());

            if (!input.markSupported()) {
                throw new MipamsException("Input Stream does not support marking.");
            }

            input.reset();
        } catch (IOException e) {
            throw new MipamsException(e);
        }

        return result;
    }
}