package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JsonBoxIntegrationTests extends AbstractIntegrationTests {

    private static String REQUEST_BODY;

    @BeforeAll
    static void initUseCase() {

        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": {")
                .append("    \"type\": \"jumd\",")
                .append("    \"contentType\": \"json\",")
                .append("    \"label\": \"JSON Content Type JUMBF box\",")
                .append("    \"id\": 4334431")
                .append("  },")
                .append("  \"content\": {")
                .append("    \"type\": \"json\",")
                .append("    \"fileUrl\":\"/home/nikos/Desktop/test.jpeg\"")
                .append("  }")
                .append("}");

        REQUEST_BODY = request.toString();
    }

    @Override
    public String getRequestBody() {
        return REQUEST_BODY;
    }

    @Test
    void testJsonBoxWithPathNotSpecified() throws Exception {
        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": {")
                .append("    \"type\": \"jumd\",")
                .append("    \"contentType\": \"json\"")
                .append("  },")
                .append("  \"content\": {")
                .append("    \"type\": \"json\"")
                .append("  }")
                .append("}");

        request.toString();

        mockMvc.perform(post("/core/v1/generateBox")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString()))
                .andExpect(status().isBadRequest());
    }

}
