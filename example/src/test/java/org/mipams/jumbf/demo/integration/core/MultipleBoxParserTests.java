package org.mipams.jumbf.demo.integration.core;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.demo.integration.AbstractParserTests;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MultipleBoxParserTests extends AbstractParserTests {

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testGenerateMultipleFileBoxRequest() throws Exception {
        testGenerateBoxEndpoint(generateMultipleFileBoxRequest());
    }

    @Test
    void testParseMultipleFileBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    String generateMultipleFileBoxRequest() {
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

        return request.toString();
    }
}