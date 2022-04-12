package org.mipams.jumbf.core.integration;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MultipleContentBoxIntegrationTests extends AbstractIntegrationTests {

    private static String REQUEST_BODY;

    @BeforeAll
    static void initUseCase() throws IOException {

        StringBuilder request = new StringBuilder();

        request.append("[")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"xml\", \"label\": \"XML Content Type JUMBF box\" },")
                .append("    \"content\": { \"type\": \"xml\", \"fileUrl\":\"").append(TEST_FILE_PATH).append("\" }  ")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"json\", \"label\": \"JSON Content Type JUMBF box\" },")
                .append("    \"content\": { \"type\": \"json\", \"fileUrl\":\"").append(TEST_FILE_PATH).append("\" }  ")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"jp2c\", \"label\": \"Contiguous Codestream Content Type JUMBF box\" },")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileUrl\":\"").append(TEST_FILE_PATH).append("\" }  ")
                .append("  }")
                .append("]");

        REQUEST_BODY = request.toString();
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp(TEST_FILE_PATH);
        fileCleanUp(JUMBF_FILE_PATH);
    }

    @Override
    public String getRequestBody() {
        return REQUEST_BODY;
    }
}