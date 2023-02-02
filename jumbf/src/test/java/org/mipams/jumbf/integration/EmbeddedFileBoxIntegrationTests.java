package org.mipams.jumbf.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.BmffBox;
import org.mipams.jumbf.entities.EmbeddedFileDescriptionBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.services.content_types.EmbeddedFileContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JumbfConfig.class)
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

        EmbeddedFileContentType embeddedFileContentType = new EmbeddedFileContentType();

        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = new EmbeddedFileDescriptionBox();
        embeddedFileDescriptionBox.setFileName(TEST_FILE_NAME);
        embeddedFileDescriptionBox.setMediaTypeFromString("image/jpeg");
        embeddedFileDescriptionBox.markFileAsExternallyReferenced();

        assertEquals(TEST_FILE_NAME, embeddedFileDescriptionBox.discoverFileName());

        BinaryDataBox binaryDataBox = new BinaryDataBox();
        binaryDataBox.setReferencedExternally(true);
        binaryDataBox.setFileUrl("http://example.org/test.jpeg");

        List<BmffBox> contentBoxes = List.of(embeddedFileDescriptionBox, binaryDataBox);

        JumbfBoxBuilder builder = new JumbfBoxBuilder(embeddedFileContentType);
        builder.setPaddingSize(10);
        builder.appendAllContentBoxes(contentBoxes);

        JumbfBox givenJumbfBox = builder.getResult();
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testEmbeddedFileBoxWithInternalReference() throws Exception {

        EmbeddedFileContentType embeddedFileContentType = new EmbeddedFileContentType();

        EmbeddedFileDescriptionBox embeddedFileDescriptionBox = new EmbeddedFileDescriptionBox();
        embeddedFileDescriptionBox.setMediaTypeFromString("image/jpeg");
        embeddedFileDescriptionBox.markFileAsInternallyReferenced();
        embeddedFileDescriptionBox.updateBmffHeadersBasedOnBox();

        assertTrue(!TEST_FILE_NAME.equals(embeddedFileDescriptionBox.discoverFileName()));

        BinaryDataBox binaryDataBox = new BinaryDataBox();
        binaryDataBox.setFileUrl(TEST_FILE_PATH);
        binaryDataBox.updateBmffHeadersBasedOnBox();

        List<BmffBox> contentBoxes = List.of(embeddedFileDescriptionBox, binaryDataBox);

        JumbfBoxBuilder builder = new JumbfBoxBuilder(embeddedFileContentType);
        builder.setPaddingSize(10);
        builder.appendAllContentBoxes(contentBoxes);

        JumbfBox givenJumbfBox = builder.getResult();
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

}
