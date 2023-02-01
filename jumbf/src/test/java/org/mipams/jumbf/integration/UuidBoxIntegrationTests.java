package org.mipams.jumbf.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.entities.UuidBox;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.services.content_types.UuidContentType;
import org.mipams.jumbf.util.CoreUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JumbfConfig.class)
@TestPropertySource(properties = "org.mipams.max_file_size_in_bytes=52428800")
@TestPropertySource(properties = "org.mipams.image_folder=/tmp/jumbf-tests")
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

        UuidContentType uuidContentType = new UuidContentType();

        UuidBox uuidBox = new UuidBox();
        uuidBox.setUuid(CoreUtils.randomStringGenerator().toUpperCase());
        uuidBox.setFileUrl(TEST_FILE_PATH);

        JumbfBoxBuilder builder = new JumbfBoxBuilder(uuidContentType);
        builder.setPaddingSize(10);
        builder.appendContentBox(uuidBox);

        JumbfBox givenJumbfBox = builder.getResult();
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

}
