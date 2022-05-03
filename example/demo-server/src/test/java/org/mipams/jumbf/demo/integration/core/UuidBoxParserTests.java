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
public class UuidBoxParserTests extends AbstractParserTests {

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testGenerateUuidFileBoxRequest() throws Exception {
        testGenerateBoxEndpoint(generateUuidFileBoxRequest());
    }

    @Test
    void testParseUuidFileBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    String generateUuidFileBoxRequest() {
        StringBuilder request = new StringBuilder();

        request.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": {")
                .append("    \"type\": \"jumd\",")
                .append("    \"contentTypeUuid\": \"75756964-0011-0010-8000-00AA00389B71\",")
                .append("    \"label\": \"UUID Content Type JUMBF box\"")
                .append("  },")
                .append("  \"content\": {")
                .append("    \"type\": \"uuid\",")
                .append("    \"uuid\": \"645ba7a8-b7f4-11ec-b909-0242ac120002\",")
                .append("    \"fileName\":\"").append(TEST_FILE_NAME).append("\"")
                .append("  }")
                .append("}");

        return request.toString();
    }
}