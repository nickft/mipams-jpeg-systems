package org.mipams.jumbf.demo.integration.core;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.mipams.jumbf.demo.integration.AbstractParserTests;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JsonBoxParserTests extends AbstractParserTests {

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testGenerateJsonFileBoxRequest() throws Exception {
        testGenerateBoxEndpoint(generateJsonFileBoxRequest());
    }

    @Test
    void testParseJsonFileBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateJsonFileBoxRequestWithPathNotSpecified() throws Exception {
        mockMvc.perform(post("/demo/generateBox")
                .contentType(MediaType.APPLICATION_JSON)
                .content(generateJsonFileBoxRequestWithPathNotSpecified()))
                .andExpect(status().isBadRequest());
    }

    String generateJsonFileBoxRequest() {
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
                .append("    \"fileUrl\":\"").append(TEST_FILE_PATH).append("\"")
                .append("  }")
                .append("}");

        return request.toString();
    }

    String generateJsonFileBoxRequestWithPathNotSpecified() {
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

        return request.toString();
    }
}