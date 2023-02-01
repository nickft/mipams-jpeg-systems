package org.mipams.jumbf.integration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractIntegrationTests {

    protected static String TEST_DIRECTORY = "/tmp/jumbf-tests/";
    protected static String TEST_FILE_NAME = "sample";
    protected static String TEST_FILE_PATH = TEST_DIRECTORY + TEST_FILE_NAME;
    protected static String JUMBF_FILE_PATH = TEST_DIRECTORY + "test.jumbf";

    protected final static String TEST_CONTENT = "Hello world";

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
            fos.write(TEST_CONTENT.getBytes());
        }
    }

    static void fileCleanUp() throws IOException {
        CoreUtils.deleteDir(TEST_DIRECTORY);
    }

    protected List<JumbfBox> generateJumbfFileAndParseBox(List<JumbfBox> givenJumbfBoxList) throws MipamsException {

        testGenerateJumbfFileFromBox(givenJumbfBoxList, JUMBF_FILE_PATH);

        return testParseMetadataFromJumbfFile(JUMBF_FILE_PATH);
    }

    protected String testGenerateJumbfFileFromBox(List<JumbfBox> givenJumbfBoxList, String assetUrl)
            throws MipamsException {
        coreGeneratorService.generateJumbfMetadataToFile(givenJumbfBoxList, assetUrl);
        return assetUrl;
    }

    protected List<JumbfBox> testParseMetadataFromJumbfFile(String assetUrl) throws MipamsException {
        return coreParserService.parseMetadataFromFile(assetUrl);
    }

}
