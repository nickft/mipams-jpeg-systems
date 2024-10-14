package org.mipams.jumbf.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.PaddingBox;
import org.mipams.jumbf.entities.PrivateBox;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;

public class CoreUtils {
    public static final int INT_BYTE_SIZE = 4;

    public static final int LONG_BYTE_SIZE = 8;

    public static final int UUID_BYTE_SIZE = 16;

    public static final int WORD_BYTE_SIZE = 2;

    public static final int MAX_APP_SEGMENT_SIZE = 0xFFFF;

    private static final int BUFFER_SIZE = 1024;

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static final String TEMP_DIRECTORY = ".mipams-tmp";

    public static final String JUMBF_FILENAME_SUFFIX = ".jumbf";

    public static void writeFileContentToOutput(String path, OutputStream outputStream) throws MipamsException {
        int numberOfBytesRead;
        try (FileInputStream inputStream = new FileInputStream(path)) {
            byte[] bytes = new byte[BUFFER_SIZE];

            while ((numberOfBytesRead = inputStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
                outputStream.write(bytes, 0, numberOfBytesRead);
            }

        } catch (IOException e) {
            throw new MipamsException("Could not read content", e);
        }
    }

    public static void writeFloatToOutputStream(float val, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertFloatToByteArray(val), outputStream);
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
        writeByteArrayToOutputStream(convertStringByteArrayUTF8(text), outputStream);
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

    public static float convertByteArrayToFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static byte[] convertIntToByteArray(int num) {
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    public static byte[] convertFloatToByteArray(float num) {
        return ByteBuffer.allocate(4).putFloat(num).array();
    }

    public static byte[] convertHexToByteArray(String hexAsString) {

        if (hexAsString.length() % 2 != 0) {
            hexAsString = "0" + hexAsString;
        }

        byte[] result = new byte[hexAsString.length() / 2];

        for (int i = 0; i < hexAsString.length(); i += 2) {

            String byteAsHex = hexAsString.charAt(i) + "" + hexAsString.charAt(i + 1);
            int value = Integer.parseInt(byteAsHex, 16);
            result[i / 2] = (byte) value;
        }

        return result;
    }

    public static String convertByteArrayToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
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

    public static byte[] convertStringByteArrayUTF8(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
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

        return new String(str.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    public static int readSingleByteAsIntFromInputStream(InputStream input) throws MipamsException {
        byte[] intBuffer = readBytesFromInputStream(input, 1);
        return Byte.toUnsignedInt(intBuffer[0]);
    }

    public static int readIntFromInputStream(InputStream input) throws MipamsException {
        byte[] intBuffer = readBytesFromInputStream(input, INT_BYTE_SIZE);
        return convertByteArrayToInt(intBuffer);
    }

    public static float readFloatFromInputStream(InputStream input) throws MipamsException {
        byte[] floatBuffer = readBytesFromInputStream(input, INT_BYTE_SIZE);
        return convertByteArrayToFloat(floatBuffer);
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

        if (numberOfBytes != 0 && numberOfBytes != outputStream.toByteArray().length) {
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

        final int currentBufferSize = (BUFFER_SIZE > nominalTotalSizeInBytes && nominalTotalSizeInBytes > 0)
                ? (int) nominalTotalSizeInBytes
                : BUFFER_SIZE;
        byte[] bytes = new byte[currentBufferSize];

        int maximumBytesToRead = currentBufferSize,
                numberOfBytesRead;
        long remainingBytes = nominalTotalSizeInBytes;

        try {
            while ((nominalTotalSizeInBytes == 0 || remainingBytes > 0)
                    && ((numberOfBytesRead = input.read(bytes, 0, maximumBytesToRead)) != -1)) {
                outputStream.write(bytes, 0, numberOfBytesRead);

                if (nominalTotalSizeInBytes == 0) {
                    continue;
                }

                remainingBytes -= numberOfBytesRead;
                maximumBytesToRead = (remainingBytes / currentBufferSize > 0) ? currentBufferSize
                        : (int) remainingBytes;
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
            OutputStream outputStream) throws MipamsException {

        final int currentBufferSize = (BUFFER_SIZE > numberOfBytes) ? (int) numberOfBytes : BUFFER_SIZE;
        byte[] bytes = new byte[currentBufferSize];
        Arrays.fill(bytes, (byte) paddingValue);

        long remainingBytes = numberOfBytes;
        int numberOfBytesToWrite = currentBufferSize;
        try {
            while (remainingBytes > 0) {
                outputStream.write(bytes, 0, numberOfBytesToWrite);

                remainingBytes -= numberOfBytesToWrite;
                numberOfBytesToWrite = (remainingBytes / currentBufferSize > 0) ? currentBufferSize
                        : (int) remainingBytes;
            }
        } catch (IOException e) {
            throw new MipamsException("Could not write to file", e);
        }

    }

    public static long parsePaddingFromInputStream(InputStream input, int paddingValue, long availableBytesForBox)
            throws MipamsException {
        try {

            final int currentBufferSize = (BUFFER_SIZE > availableBytesForBox && availableBytesForBox > 0)
                    ? (int) availableBytesForBox
                    : BUFFER_SIZE;
            byte[] bytes = new byte[currentBufferSize];

            long remainingBytes = availableBytesForBox;

            int maximumBytesToRead = currentBufferSize,
                    numberOfBytesRead;

            while ((availableBytesForBox == 0 || remainingBytes > 0)
                    && ((numberOfBytesRead = input.read(bytes, 0, maximumBytesToRead)) != -1)) {
                for (byte b : bytes) {
                    if (b != paddingValue) {
                        throw new MipamsException("Padding is corrupted. It should contain only values of 0x00");
                    }
                }

                if (availableBytesForBox == 0) {
                    continue;
                }

                remainingBytes -= numberOfBytesRead;
                maximumBytesToRead = (remainingBytes / currentBufferSize > 0) ? currentBufferSize
                        : (int) remainingBytes;
            }

            return availableBytesForBox - remainingBytes;
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

    public static String createTempSubdirectory() throws MipamsException {
        return createSubdirectory(getTempDir(), randomStringGenerator());
    }

    public static String getTempDir() throws MipamsException {
        String randomFile = createTempFile(randomStringGenerator(), null);
        String tempDir = getParentDirectory(randomFile);
        deleteFile(randomFile);
        return tempDir;
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

        if (fileUrl == null) {
            return true;
        }

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

    public static int getNextBoxType(InputStream input) throws MipamsException {

        try {
            if (!input.markSupported()) {
                throw new MipamsException("Input Stream does not support marking.");
            }

            input.mark(16);

            readIntFromInputStream(input);
            int tBox = readIntFromInputStream(input);

            input.reset();
            return tBox;
        } catch (IOException e) {
            throw new MipamsException(e);
        }
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

    public static String getParentDirectory(String assetUrl) throws MipamsException {

        File dir = new File(assetUrl);

        if (!dir.exists()) {
            throw new MipamsException(String.format("Path %s does not exist", assetUrl));
        }
        return dir.getParent();
    }

    public static String createTempFile(String tempFileName, String suffix) throws MipamsException {
        try {
            File f = File.createTempFile(tempFileName, suffix);
            return f.getAbsolutePath();
        } catch (IOException e) {
            throw new MipamsException(e);
        }
    }

    public static void skipBytesFromInputStream(InputStream input, long availableBytesForBox) throws IOException {
        final int currentBufferSize = (BUFFER_SIZE > availableBytesForBox && availableBytesForBox > 0)
                ? (int) availableBytesForBox
                : BUFFER_SIZE;
        byte[] bytes = new byte[currentBufferSize];

        long remainingBytes = availableBytesForBox;

        int maximumBytesToRead = currentBufferSize,
                numberOfBytesRead;

        while ((availableBytesForBox == 0 || remainingBytes > 0)
                && ((numberOfBytesRead = input.read(bytes, 0, maximumBytesToRead)) != -1)) {
            if (availableBytesForBox == 0) {
                continue;
            }

            remainingBytes -= numberOfBytesRead;
            maximumBytesToRead = (remainingBytes / currentBufferSize > 0) ? currentBufferSize
                    : (int) remainingBytes;
        }
    }

    public static int parseBoxAndGetType(InputStream is) throws MipamsException {
        try {
            long boxLength = readIntFromInputStream(is);

            int boxType = readIntFromInputStream(is);

            if (boxLength == 1) {
                boxLength = readLongFromInputStream(is);
            }

            if (boxLength > 0) {
                readBytesFromInputStream(is, boxLength);
            }
            
            return boxType;
        } catch (Exception e) {
            throw new MipamsException("Failed to parse box", e);
        }
    }
}