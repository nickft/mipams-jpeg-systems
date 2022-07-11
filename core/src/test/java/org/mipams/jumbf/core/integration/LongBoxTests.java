package org.mipams.jumbf.core.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.JumbfBoxBuilder;
import org.mipams.jumbf.core.services.content_types.JsonContentType;
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

        String test = testGenerateJumbfFileFromBox(List.of(givenJumbfBox), JUMBF_FILE_PATH);

        assertTrue(test != null);
    }

    JumbfBox generateMockLongBox() throws MipamsException {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());
        jsonBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.setId(12345);
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();

        givenJumbfBox.setXlBox(Long.valueOf(givenJumbfBox.getLBox() + CoreUtils.LONG_BYTE_SIZE));
        givenJumbfBox.setLBox(1);
        return givenJumbfBox;
    }

}