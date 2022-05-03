package org.mipams.jumbf.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;

import java.util.UUID;

import org.springframework.http.MediaType;

public class CoreUtils {
    public static final int INT_BYTE_SIZE = 4;

    public static final int LONG_BYTE_SIZE = 8;

    public static final int UUID_BYTE_SIZE = 16;

    public static void writeFileContentToOutput(String path, OutputStream outputStream) throws IOException {

        try (FileInputStream inputStream = new FileInputStream(path)) {
            int n;
            while ((n = inputStream.read()) != -1) {
                outputStream.write(n);
            }
        }
    }

    public static void writeIntToOutputStream(int val, OutputStream outputStream) throws MipamsException {
        writeByteArrayToOutputStream(convertIntToByteArray(val), outputStream);
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
        return (int) intBuffer[0];
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

    public static byte[] readBytesFromInputStream(InputStream input, int numberOfBytes) throws MipamsException {
        try {

            byte[] buffer = new byte[numberOfBytes % 256];

            if (input.read(buffer) == -1) {
                throw new MipamsException();
            }

            return buffer;
        } catch (IOException e) {
            throw new MipamsException(e);
        }
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

            int actualBytes = 0, n;

            while ((actualBytes < nominalTotalSizeInBytes) && ((n = input.read()) != -1)) {
                fileOutputStream.write(n);
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
}