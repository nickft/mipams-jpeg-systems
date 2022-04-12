package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class UuidBoxIntegrationTests extends AbstractIntegrationTests {

    private static String REQUEST_BODY;

    @BeforeAll
    static void initUseCase() {

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
                .append("    \"fileUrl\": \"/home/nikos/Desktop/test.jpeg\"")
                .append("  }")
                .append("}");

        REQUEST_BODY = request.toString();
    }

    @Override
    public String getRequestBody() {
        return REQUEST_BODY;
    }
}