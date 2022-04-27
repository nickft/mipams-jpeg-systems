package org.mipams.jumbf.core.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.boxes.CoreGeneratorService;
import org.mipams.jumbf.core.services.boxes.CoreParserService;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractIntegrationTests {

    protected static String TEST_DIRECTORY = "/tmp/jumbf-tests/";
    protected static String TEST_FILE_NAME = "test.jpeg";
    protected static String TEST_FILE_PATH = TEST_DIRECTORY + TEST_FILE_NAME;
    protected static String JUMBF_FILE_NAME = "test.jumbf";

    @Autowired
    protected CoreGeneratorService coreGeneratorService;

    @Autowired
    protected CoreParserService coreParserService;

    static void generateFile() throws IOException {
        File file = new File(TEST_DIRECTORY);
        if (file.exists()) {
            return;
        }

        file.mkdir();

        file = new File(TEST_DIRECTORY);

        try (FileOutputStream fos = new FileOutputStream(TEST_FILE_PATH)) {
            fos.write("Hello world".getBytes());
        }
    }

    static void fileCleanUp() throws IOException {

        File dir = new File(TEST_DIRECTORY);
        if (!dir.exists()) {
            return;
        }

        File[] directoryListing = dir.listFiles();

        for (File file : directoryListing) {
            file.delete();
        }

        dir.delete();
    }

    protected List<JumbfBox> generateJumbfFileAndParseBox(List<JumbfBox> givenJumbfBoxList) throws MipamsException {

        testGenerateJumbfFileFromBox(givenJumbfBoxList, JUMBF_FILE_NAME);

        return testParseMetadataFromJumbfFile(JUMBF_FILE_NAME);
    }

    protected String testGenerateJumbfFileFromBox(List<JumbfBox> givenJumbfBoxList, String fileName)
            throws MipamsException {
        return coreGeneratorService.generateJumbfFileFromBox(givenJumbfBoxList, fileName);
    }

    protected List<JumbfBox> testParseMetadataFromJumbfFile(String fileName) throws MipamsException {
        return coreParserService.parseMetadataFromJumbfFile(fileName);
    }

}
