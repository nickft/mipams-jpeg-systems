package org.mipams.jumbf.core.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class LongBoxTests extends AbstractIntegrationTests {

    @BeforeAll
    static void initRequest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testLongBox() throws MipamsException {
        JumbfBox givenJumbfBox = generateMockLongBox();

        String test = testGenerateJumbfFileFromBox(List.of(givenJumbfBox), JUMBF_FILE_NAME);

        assertTrue(test != null);
    }

    JumbfBox generateMockLongBox() throws MipamsException {
        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl(TEST_FILE_PATH);
        jsonBox.updateBmffHeadersBasedOnBox();

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(jsonBox.getContentTypeUUID());
        dBox.setId(12345);
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        JumbfBox box = MockJumbfBoxCreation.generateJumbfBox(dBox, jsonBox, 10);

        box.setXBox(Long.valueOf(box.getLBox() + CoreUtils.LONG_BYTE_SIZE));
        box.setLBox(1);
        return box;
    }

}