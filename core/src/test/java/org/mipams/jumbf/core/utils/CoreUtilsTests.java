package org.mipams.jumbf.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

@SpringBootTest
public class CoreUtilsTests {

    @BeforeAll
    static void initUseCase() throws IOException {

        File file = new File(getTestFilePath());
        if (file.exists()) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(getTestFilePath())) {
            fos.write("Hello world".getBytes());
        }
    }

    @AfterAll
    static void cleanUp() throws IOException {

        File file = new File(getTestFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    static String getTestFilePath() {
        return CoreUtils.getFullPath("/tmp", "test");
    }

    @Test
    void testClassInit() {
        CoreUtils utils = new CoreUtils();
        assertNotNull(utils);
    }

    @Test
    void testConvertByteArrayToInt() {
        byte[] result = DatatypeConverter.parseHexBinary("0000000f");
        int resultAsInt = CoreUtils.convertByteArrayToInt(result);
        assertEquals(15, resultAsInt);
    }

    @Test
    void testConvertIntToByteArray() {
        int testInput = 10;
        byte[] outputArray = CoreUtils.convertIntToByteArray(testInput);
        String resultAsString = DatatypeConverter.printHexBinary(outputArray);
        assertEquals("0000000A", resultAsString);
    }

    @Test
    void testConvertIntToSingleElementByteArray() {
        int testInput = 10;
        byte[] outputArray = CoreUtils.convertIntToSingleElementByteArray(testInput);
        String resultAsString = DatatypeConverter.printHexBinary(outputArray);
        assertEquals("0A", resultAsString);
    }

    @Test
    void testConvertByteArrayToLong() {
        byte[] inputArray = DatatypeConverter.parseHexBinary("000000000000000f");
        long outputLong = CoreUtils.convertByteArrayToLong(inputArray);
        assertEquals(15, outputLong);
    }

    @Test
    void testConvertLongToByteArray() {
        long testInput = 10;
        byte[] outputArray = CoreUtils.convertLongToByteArray(testInput);
        String resultAsString = DatatypeConverter.printHexBinary(outputArray);
        assertEquals("000000000000000A", resultAsString);
    }

    @Test
    void testConvertStringToByteArray() {
        String text = "Hello world";
        byte[] outputArray = CoreUtils.convertStringToByteArray(text);
        assertTrue(Arrays.equals(text.getBytes(), outputArray));
    }

    @Test
    void testConvertByteArrayToUUID() throws MipamsException {

        UUID input = UUID.randomUUID();

        byte[] uuidAsByteArray = convertUuidToByteArray(input);

        UUID result = CoreUtils.convertByteArrayToUUID(uuidAsByteArray);

        assertEquals(input, result);
    }

    @Test
    void testConvertUUIDToByteArray() {

        UUID input = UUID.randomUUID();

        byte[] resultByteArray = CoreUtils.convertUUIDToByteArray(input);

        byte[] expectedByteArray = convertUuidToByteArray(input);

        assertTrue(Arrays.equals(expectedByteArray, resultByteArray));
    }

    @Test
    void testParseStringFromFile() throws MipamsException {

        String existentFileName = getTestFilePath();

        String test = CoreUtils.parseStringFromFile(existentFileName);

        assertEquals("Hello world", test);
    }

    @Test
    void testParseStringFromNonExistentFile() {

        String nonExistentFileName = CoreUtils.randomStringGenerator();

        assertThrows(MipamsException.class, () -> {
            CoreUtils.parseStringFromFile(nonExistentFileName);
        });
    }

    @Test
    void testReadStringFromInputStream() throws IOException {
        try (ByteArrayInputStream is = new ByteArrayInputStream("Hello\0".getBytes());) {
            String result = CoreUtils.readStringFromInputStream(is);
            assertEquals("Hello", result);
        }
    }

    @Test
    void testReadStringWithoutNullTerminationFromInputStream() throws IOException {
        try (ByteArrayInputStream is = new ByteArrayInputStream("Hello".getBytes());) {
            String result = CoreUtils.readStringFromInputStream(is);
            assertEquals("Hello", result);
        }
    }

    @Test
    void testReadStringFromFailingInputStream() throws IOException {
        try (FailingInputStream is = new FailingInputStream();) {
            assertThrows(IOException.class, () -> {
                CoreUtils.readStringFromInputStream(is);
            });
        }
    }

    @Test
    void testReadSingleByteAsIntFromInputStream() throws MipamsException, IOException {

        int val = 285;

        byte[] testArray = CoreUtils.convertIntToSingleElementByteArray(val);

        try (ByteArrayInputStream is = new ByteArrayInputStream(testArray);) {
            int result = CoreUtils.readSingleByteAsIntFromInputStream(is);
            assertEquals(val % 256, result);
        }
    }

    @Test
    void testReadIntFromInputStream() throws MipamsException, IOException {

        int val = 45;

        byte[] testArray = CoreUtils.convertIntToByteArray(val);

        try (ByteArrayInputStream is = new ByteArrayInputStream(testArray);) {
            int result = CoreUtils.readIntFromInputStream(is);
            assertEquals(val, result);
        }
    }

    @Test
    void testReadLongFromInputStream() throws IOException, MipamsException {
        long val = 45;

        byte[] testArray = CoreUtils.convertLongToByteArray(val);

        try (ByteArrayInputStream is = new ByteArrayInputStream(testArray);) {
            long result = CoreUtils.readLongFromInputStream(is);
            assertEquals(val, result);
        }
    }

    @Test
    void testReadBytesFromEmptyInputStream() throws IOException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);) {
            assertThrows(MipamsException.class, () -> {
                CoreUtils.readBytesFromInputStream(is, 1);
            });
        }
    }

    @Test
    void testReadBytesFromFailingInputStream() throws IOException {
        try (FailingInputStream is = new FailingInputStream();) {
            assertThrows(MipamsException.class, () -> {
                CoreUtils.readBytesFromInputStream(is, 1);
            });
        }
    }

    @Test
    void testReadUuidFromInputStream() throws IOException, MipamsException {

        UUID input = UUID.randomUUID();

        byte[] inputUuid = convertUuidToByteArray(input);

        try (ByteArrayInputStream is = new ByteArrayInputStream(inputUuid);) {

            UUID result = CoreUtils.readUuidFromInputStream(is);
            assertEquals(input, result);
        }
    }

    public class FailingInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new IOException("Test generated exception");
        }
    }

    private byte[] convertUuidToByteArray(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();

        byte[] msbAsByteArray = CoreUtils.convertLongToByteArray(msb);
        byte[] lsbAsByteArray = CoreUtils.convertLongToByteArray(lsb);

        byte[] outputByteArray = new byte[16];
        System.arraycopy(msbAsByteArray, 0, outputByteArray, 0, 8);
        System.arraycopy(lsbAsByteArray, 0, outputByteArray, 8, 8);

        return outputByteArray;
    }

    @Test
    void testIsBitAtGivenPositionSet() throws MipamsException {

        int test = 2;

        assertTrue(!CoreUtils.isBitAtGivenPositionSet(test, 0));
        assertTrue(CoreUtils.isBitAtGivenPositionSet(test, 1));

        test = 3;
        assertTrue(CoreUtils.isBitAtGivenPositionSet(test, 0));
        assertTrue(CoreUtils.isBitAtGivenPositionSet(test, 1));
    }

    @Test
    void testSetBitValueAtGivenPosition() throws MipamsException {

        int inputInt = 2;

        int outputInt = CoreUtils.setBitValueAtGivenPosition(inputInt, 1, 0);
        outputInt = CoreUtils.setBitValueAtGivenPosition(outputInt, 3, 1);

        assertEquals(8, outputInt);
    }

    @Test
    void testGetFileSizeFromPath() throws MipamsException {
        long resultSize = CoreUtils.getFileSizeFromPath(getTestFilePath());
        assertEquals(11, resultSize);
    }

    @Test
    void testRandomStringGenerator() throws MipamsException {
        String test = CoreUtils.randomStringGenerator();
        assertTrue(test != null);
    }

    @Test
    void testGetFullPath() throws MipamsException {

        String result = CoreUtils.getFullPath("test1", "test2");
        assertEquals("test1/test2", result);

        result = CoreUtils.getFullPath("test1/", "test2");
        assertEquals("test1/test2", result);
    }

    @Test
    void testAddEscapeCharacterToText() throws MipamsException {

        String result = CoreUtils.addEscapeCharacterToText("test1");
        assertEquals("test1\0", result);
    }

    @Test
    void testWriteFileContentToOutput() throws MipamsException, FileNotFoundException, IOException {

        String inputFileName = getTestFilePath();
        String outputFileName = CoreUtils.getFullPath("/tmp", CoreUtils.randomStringGenerator());

        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);
        try (FileOutputStream os = new FileOutputStream(outputFileName);) {

            CoreUtils.writeFileContentToOutput(inputFileName, os);

            assertEquals(inputFile.length(), outputFile.length());
        } finally {
            outputFile.delete();
        }
    }

    @Test
    void testWriteFileContentToNonExistentOutput() throws MipamsException, FileNotFoundException, IOException {

        String nonExistentInput = CoreUtils.getFullPath("/tmp", CoreUtils.randomStringGenerator());
        String outputFileName = CoreUtils.getFullPath("/tmp", CoreUtils.randomStringGenerator());

        try (FileOutputStream os = new FileOutputStream(outputFileName);) {

            Exception e = assertThrows(MipamsException.class, () -> {
                CoreUtils.writeFileContentToOutput(nonExistentInput, os);
            });

            String actualMessage = e.getMessage();

            assertEquals("Could not locate file", actualMessage);
        } finally {
            File test = new File(outputFileName);
            test.delete();
        }
    }

    @Test
    void testWriteFileContentToFailingOutput() throws MipamsException, FileNotFoundException, IOException {

        String inputFileName = getTestFilePath();

        try (FailingOutputStream os = new FailingOutputStream();) {
            assertThrows(MipamsException.class, () -> {
                CoreUtils.writeFileContentToOutput(inputFileName, os);
            });
        }
    }

    public class FailingOutputStream extends OutputStream {
        @Override
        public void write(int test) throws IOException {
            throw new IOException("Test generated exception");
        }
    }

    @Test
    void testWriteBytesFromInputStreamToFile() throws MipamsException, FileNotFoundException, IOException {
        UUID input = UUID.randomUUID();

        byte[] inputUuid = convertUuidToByteArray(input);

        String absoluteFilePath = CoreUtils.getFullPath("/tmp", CoreUtils.randomStringGenerator());

        File temporaryFile = new File(absoluteFilePath);

        try (ByteArrayInputStream is = new ByteArrayInputStream(inputUuid);) {

            CoreUtils.writeBytesFromInputStreamToFile(is, 16, absoluteFilePath);
            assertEquals(16, temporaryFile.length());
        } finally {
            temporaryFile.delete();
        }
    }

    @Test
    void testWriteLessBytesThanProvidedFromInputStreamToFile()
            throws MipamsException, FileNotFoundException, IOException {
        UUID input = UUID.randomUUID();

        byte[] inputUuid = convertUuidToByteArray(input);

        String absoluteFilePath = CoreUtils.getFullPath("/tmp", CoreUtils.randomStringGenerator());

        File temporaryFile = new File(absoluteFilePath);

        try (ByteArrayInputStream is = new ByteArrayInputStream(inputUuid);) {

            CoreUtils.writeBytesFromInputStreamToFile(is, 17, absoluteFilePath);
            assertEquals(16, temporaryFile.length());
        } finally {
            temporaryFile.delete();
        }
    }

    @Test
    void testWriteBytesFromFailingInputStreamToFile() throws MipamsException, FileNotFoundException, IOException {
        String absoluteFilePath = CoreUtils.getFullPath("/tmp", CoreUtils.randomStringGenerator());

        try (FailingInputStream is = new FailingInputStream();) {
            Exception exception = assertThrows(MipamsException.class, () -> {
                CoreUtils.writeBytesFromInputStreamToFile(is, 16, absoluteFilePath);
            });

            String actualMessage = exception.getMessage();

            assertEquals("Could not read content", actualMessage);
        } finally {
            File temporaryFile = new File(absoluteFilePath);
            temporaryFile.delete();
        }
    }

    @Test
    void testGetMediaTypeFromString() {
        String test = "application/json";
        MediaType type = CoreUtils.getMediaTypeFromString(test);
        assertNotNull(type);
    }

    @Test
    void testGetMediaTypeFromStringWithNoSubtype() {
        String test = "application/";

        assertThrows(IllegalArgumentException.class, () -> {
            CoreUtils.getMediaTypeFromString(test);
        });
    }

    @Test
    void testGetMediaTypeFromInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> {
            CoreUtils.getMediaTypeFromString("test");
        });
    }

    @Test
    void testGetMediaTypeFromNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            CoreUtils.getMediaTypeFromString(null);
        });
    }
}
