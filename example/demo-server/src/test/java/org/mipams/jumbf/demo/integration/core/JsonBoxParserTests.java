package org.mipams.jumbf.demo.integration.core;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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
@TestMethodOrder(OrderAnnotation.class)
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
    @Order(1)
    void testGenerateJsonFileBoxRequest() throws Exception {
        testGenerateBoxEndpoint(generateJsonFileBoxRequest());
    }

    @Test
    @Order(2)
    void testParseJsonFileBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateJsonFileBoxRequestWithPathNotSpecified() throws Exception {
        mockMvc.perform(post("/api/demo/generateBox")
                .contentType(MediaType.APPLICATION_JSON)
                .content(generateJsonFileBoxRequestWithFileNameNotSpecified()))
                .andExpect(status().isBadRequest());
    }

    String generateJsonFileBoxRequest() {
        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": {")
                .append("    \"type\": \"jumd\",")
                .append("    \"contentTypeUuid\": \"6A736F6E-0011-0010-8000-00AA00389B71\",")
                .append("    \"label\": \"JSON Content Type JUMBF box\",")
                .append("    \"id\": 4334431")
                .append("  },")
                .append("  \"content\": {")
                .append("    \"type\": \"json\",")
                .append("    \"fileName\":\"").append(TEST_FILE_NAME).append("\"")
                .append("   }")
                .append("}");

        return request.toString();
    }

    String generateJsonFileBoxRequestWithFileNameNotSpecified() {
        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": {")
                .append("    \"type\": \"jumd\",")
                .append("    \"contentTypeUuid\": \"6A736F6E-0011-0010-8000-00AA00389B71\"")
                .append("  },")
                .append("  \"content\": {")
                .append("    \"type\": \"json\"")
                .append("  }")
                .append("}");

        return request.toString();
    }
}
