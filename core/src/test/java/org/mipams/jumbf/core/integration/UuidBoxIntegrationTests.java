package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.UuidBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.services.CoreParserService;
import org.mipams.jumbf.core.util.CoreUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class UuidBoxIntegrationTests extends AbstractIntegrationTests {

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
    void testUuidBox() throws Exception {

        UuidBox uuidBox = new UuidBox();
        uuidBox.setUuid(CoreUtils.randomStringGenerator().toUpperCase());
        uuidBox.setFileUrl(TEST_FILE_PATH);
        uuidBox.updateBmffHeadersBasedOnBox();

        JumbfBox givenJumbfBox = MockJumbfBoxCreation.generateJumbfBoxWithContent(uuidBox, 10);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
        assertEquals(givenJumbfBox.getBmffBoxes(), parsedJumbfBox.getBmffBoxes());
    }

}
