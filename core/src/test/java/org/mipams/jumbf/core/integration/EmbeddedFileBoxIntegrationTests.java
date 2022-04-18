package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mipams.jumbf.core.entities.BinaryDataBox;
import org.mipams.jumbf.core.entities.EmbeddedFileBox;
import org.mipams.jumbf.core.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.services.CoreParserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class EmbeddedFileBoxIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

    @BeforeAll
    static void initRequest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testEmbeddedFileBoxWithExternalReference() throws Exception {

        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = new EmbeddedFileDescriptionBox();
        embeddedFileDescriptionBox.setFileName(TEST_FILE_NAME);
        embeddedFileDescriptionBox.setMediaTypeFromString("image/jpeg");
        embeddedFileDescriptionBox.computeAndSetToggleBasedOnFields();
        embeddedFileDescriptionBox.markFileAsExternallyReferenced();
        embeddedFileDescriptionBox.updateBmffHeadersBasedOnBox();

        BinaryDataBox binaryDataBox = new BinaryDataBox();
        binaryDataBox.setReferencedExternally(true);
        binaryDataBox.setFileUrl("http://example.org/test.jpeg");
        binaryDataBox.updateBmffHeadersBasedOnBox();

        EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();
        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBox);
        embeddedFileBox.setBinaryDataBox(binaryDataBox);

        JumbfBox givenJumbfBox = MockJumbfBoxCreation.generateJumbfBoxWithContent(embeddedFileBox, 10);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testEmbeddedFileBoxWithInternalReference() throws Exception {

        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = new EmbeddedFileDescriptionBox();
        embeddedFileDescriptionBox.setFileName(TEST_FILE_NAME);
        embeddedFileDescriptionBox.setMediaTypeFromString("image/jpeg");
        embeddedFileDescriptionBox.markFileAsInternallyReferenced();
        embeddedFileDescriptionBox.computeAndSetToggleBasedOnFields();
        embeddedFileDescriptionBox.updateBmffHeadersBasedOnBox();

        BinaryDataBox binaryDataBox = new BinaryDataBox();
        binaryDataBox.setFileUrl(TEST_FILE_PATH);
        binaryDataBox.updateBmffHeadersBasedOnBox();

        EmbeddedFileBox embeddedFileBox = new EmbeddedFileBox();
        embeddedFileBox.setDescriptionBox(embeddedFileDescriptionBox);
        embeddedFileBox.setBinaryDataBox(binaryDataBox);

        JumbfBox givenJumbfBox = MockJumbfBoxCreation.generateJumbfBoxWithContent(embeddedFileBox, 10);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

}
