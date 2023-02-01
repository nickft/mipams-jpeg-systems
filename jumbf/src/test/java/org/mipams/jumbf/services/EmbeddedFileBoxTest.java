// package org.mipams.jumbf.services;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;

// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.mipams.jumbf.entities.BinaryDataBox;
// import org.mipams.jumbf.entities.EmbeddedFileBox;
// import org.mipams.jumbf.entities.EmbeddedFileDescriptionBox;
// import org.mipams.jumbf.services.content_types.EmbeddedFileContentType;
// import org.mipams.jumbf.util.CoreUtils;
// import org.mipams.jumbf.util.MipamsException;
// import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest
// public class EmbeddedFileBoxTest {
// protected static String TEST_DIRECTORY = "/tmp/jumbf-tests/";
// protected static String TEST_FILE_NAME = "test.jpeg";
// protected static String TEST_FILE_PATH = TEST_DIRECTORY + TEST_FILE_NAME;

// private static String EXAMPLE_URL = "http://www.google.com/test.jpeg";

// @BeforeAll
// static void generateFile() throws IOException {
// File file = new File(TEST_DIRECTORY);
// if (file.exists()) {
// return;
// }

// file.mkdir();

// file = new File(TEST_DIRECTORY);

// try (FileOutputStream fos = new FileOutputStream(TEST_FILE_PATH)) {
// String toWrite = CoreUtils.addEscapeCharacterToText(EXAMPLE_URL);
// fos.write(toWrite.getBytes());
// }
// }

// @AfterAll
// static void fileCleanUp() throws IOException {

// File dir = new File(TEST_DIRECTORY);
// if (!dir.exists()) {
// return;
// }

// File[] directoryListing = dir.listFiles();

// for (File file : directoryListing) {
// file.delete();
// }

// dir.delete();
// }

// @Test
// void testGetFileUrlFromBoxWhenBoxIsEmbedded() throws MipamsException {
// EmbeddedFileDescriptionBox embeddedFileDescriptionBox = new
// EmbeddedFileDescriptionBox();
// embeddedFileDescriptionBox.setFileName(TEST_FILE_PATH);
// embeddedFileDescriptionBox.setMediaTypeFromString("image/jpeg");
// embeddedFileDescriptionBox.markFileAsInternallyReferenced();
// embeddedFileDescriptionBox.computeAndSetToggleBasedOnFields();
// embeddedFileDescriptionBox.updateBmffHeadersBasedOnBox();

// BinaryDataBox binaryDataBox = new BinaryDataBox();
// binaryDataBox.setFileUrl(TEST_FILE_PATH);
// binaryDataBox.updateBmffHeadersBasedOnBox();

// EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();
// embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBox);
// embeddedFileBox.setBinaryDataBox(binaryDataBox);

// EmbeddedFileContentType service = new EmbeddedFileContentType();

// String result = service.getFileUrlFromBox(embeddedFileBox);

// assertEquals(TEST_FILE_PATH, result);
// }

// @Test
// void testGetFileUrlFromBoxWhenBoxIsNotEmbedded() throws MipamsException {
// EmbeddedFileDescriptionBox embeddedFileDescriptionBox = new
// EmbeddedFileDescriptionBox();
// embeddedFileDescriptionBox.setMediaTypeFromString("image/jpeg");
// embeddedFileDescriptionBox.markFileAsExternallyReferenced();
// embeddedFileDescriptionBox.computeAndSetToggleBasedOnFields();
// embeddedFileDescriptionBox.updateBmffHeadersBasedOnBox();

// BinaryDataBox binaryDataBox = new BinaryDataBox();
// binaryDataBox.setFileUrl(TEST_FILE_PATH);
// binaryDataBox.updateBmffHeadersBasedOnBox();

// EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();
// embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBox);
// embeddedFileBox.setBinaryDataBox(binaryDataBox);

// EmbeddedFileContentType service = new EmbeddedFileContentType();

// String result = service.getFileUrlFromBox(embeddedFileBox);

// assertEquals(EXAMPLE_URL, result);
// }

// }
