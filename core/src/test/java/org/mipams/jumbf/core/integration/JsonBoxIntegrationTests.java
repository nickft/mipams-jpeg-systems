package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.services.CoreParserService;
import org.mipams.jumbf.core.services.content_types.JsonContentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class JsonBoxIntegrationTests extends AbstractIntegrationTests {

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
    void testJsonBox() throws Exception {
        JsonContentType jsonContentType = new JsonContentType();
        String contentType = jsonContentType.getContentTypeUuid();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl(TEST_FILE_PATH);
        jsonBox.updateBmffHeadersBasedOnBox();

        JumbfBox givenJumbfBox = MockJumbfBoxCreation.generateJumbfBoxWithContent(List.of(jsonBox), contentType, 10);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testJsonBoxWithIdAndSignatureInDescriptionBox() throws Exception {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl(TEST_FILE_PATH);
        jsonBox.updateBmffHeadersBasedOnBox();

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(jsonContentType.getContentTypeUuid());
        dBox.setId(12345);
        dBox.setSha256Hash(
                DatatypeConverter.parseHexBinary("321242ad321242aa321242ad321242aa321242ad321242aa321242ad321242aa"));
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        assertEquals(dBox.isRequestable(), false);

        JumbfBox givenJumbfBox = MockJumbfBoxCreation.generateJumbfBox(dBox, List.of(jsonBox), 10);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }
}
