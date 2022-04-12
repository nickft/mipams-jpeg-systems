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
public class UuidBoxIntegrationTests extends AbstractIntegrationTests {

    private static String REQUEST_BODY;

    @BeforeAll
    static void initUseCase() throws IOException {

        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": {")
                .append("    \"type\": \"jumd\",")
                .append("    \"contentType\": \"uuid\",")
                .append("    \"label\": \"UUID Content Type JUMBF box\"")
                .append("  },")
                .append("  \"content\": {")
                .append("    \"type\": \"uuid\",")
                .append("    \"uuid\": \"645ba7a8-b7f4-11ec-b909-0242ac120002\",")
                .append("    \"fileUrl\":\"").append(TEST_FILE_PATH).append("\"")
                .append("  }")
                .append("}");

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