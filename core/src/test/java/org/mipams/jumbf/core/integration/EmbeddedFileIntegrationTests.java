package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class EmbeddedFileIntegrationTests extends AbstractIntegrationTests {

    private static String REQUEST_BODY;

    @BeforeAll
    static void initUseCase() {

        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"bfbd\" },")
                .append("  \"content\": {")
                .append("    \"embeddedFileDescription\": {")
                .append("      \"type\": \"bfdb\",")
                .append("      \"mediaType\": \"image/jpeg\",")
                .append("      \"fileName\": \"test.jpeg\",")
                .append("      \"fileExternallyReferenced\": \"true\"")
                .append("    },")
                .append("    \"content\": {")
                .append("      \"type\": \"bidb\",")
                .append("      \"fileUrl\": \"http://example.org/test.jpeg\"")
                .append("    }")
                .append("  }")
                .append("}");

        REQUEST_BODY = request.toString();
    }

    @Override
    public String getRequestBody() {
        return REQUEST_BODY;
    }
}
