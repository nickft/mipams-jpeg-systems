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
public class EmbeddedFileBoxParserTests extends AbstractParserTests {

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testGenerateEmbeddedFileBoxRequest() throws Exception {
        testGenerateBoxEndpoint(generateEmbeddedFileBoxRequest());
    }

    @Test
    void testParseEmbeddedFileBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    String generateEmbeddedFileBoxRequest() {
        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"40CB0C32-BB8A-489D-A70B-2AD6F47F4369\" },")
                .append("  \"content\": {")
                .append("    \"embeddedFileDescription\": {")
                .append("      \"type\": \"bfdb\",")
                .append("      \"mediaType\": \"image/jpeg\",")
                .append("      \"fileName\": \"test.jpeg\",")
                .append("      \"fileExternallyReferenced\": \"true\"")
                .append("    },")
                .append("    \"content\": {")
                .append("      \"type\": \"bidb\",")
                .append("      \"fileName\": \"http://example.org/test.jpeg\"")
                .append("    }")
                .append("  }")
                .append("}");

        return request.toString();
    }
}