package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class MultipleContentBoxIntegrationTests extends AbstractIntegrationTests {

    private static String REQUEST_BODY;

    @BeforeAll
    static void initUseCase() {

        StringBuilder request = new StringBuilder();

        request.append("[")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"xml\", \"label\": \"XML Content Type JUMBF box\" },")
                .append("    \"content\": { \"type\": \"xml\", \"fileUrl\":\"/home/nikos/Desktop/test.jpeg\" }  ")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"json\", \"label\": \"JSON Content Type JUMBF box\" },")
                .append("    \"content\": { \"type\": \"json\", \"fileUrl\":\"/home/nikos/Desktop/test.jpeg\" }  ")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"jp2c\", \"label\": \"Contiguous Codestream Content Type JUMBF box\" },")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileUrl\":\"/home/nikos/Desktop/test.jpeg\" }  ")
                .append("  }")
                .append("]");

        REQUEST_BODY = request.toString();
    }

    @Override
    public String getRequestBody() {
        return REQUEST_BODY;
    }
}