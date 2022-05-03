package org.mipams.jumbf.demo.integration.privacy_security;

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
public class ProtectionBoxTests extends AbstractParserTests {

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testGenerateProtectionBoxAesRequest() throws Exception {
        testGenerateBoxEndpoint(getAesRequest());
    }

    @Test
    void testParseProtectionBoxAesRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testProtectionBoxAesRequest() throws Exception {
        testGenerateBoxEndpoint(getAesRequest());
    }

    @Test
    void testGenerateProtectionBoxAesWithIVRequest() throws Exception {
        testGenerateBoxEndpoint(getAesWithIVRequest());
    }

    @Test
    void testParseProtectionBoxAesWithIVRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateProtectionBoxRequestWithExternalEncryption() throws Exception {
        testGenerateBoxEndpoint(getRequestWithExternalEncryption());
    }

    @Test
    void testParseProtectionBoxAesWithExternalEncryption() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateProtectionBoxRequestWithAccessRules() throws Exception {
        testGenerateBoxEndpoint(getRequestWithAccessRules());
    }

    @Test
    void testParseProtectionBoxAesWithAccessRules() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    String getAesRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3\" },")
                .append("  \"content\": {")
                .append("    \"protectionDescription\": {")
                .append("      \"type\": \"pspd\",")
                .append("      \"method\": \"aes-256-cbc\"")
                .append("    },")
                .append("    \"content\": { \"type\": \"bidb\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getAesWithIVRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3\" },")
                .append("  \"content\": {")
                .append("    \"protectionDescription\": {")
                .append("      \"type\": \"pspd\",")
                .append("      \"method\": \"aes-256-cbc-iv\",")
                .append("      \"ivHexString\": \"D9BBA15016D876F67532FAFB8B851D24\"")
                .append("    },")
                .append("    \"content\": { \"type\": \"bidb\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getRequestWithExternalEncryption() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("[")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3\" },")
                .append("    \"content\": {")
                .append("      \"protectionDescription\": {")
                .append("        \"type\": \"pspd\",")
                .append("        \"method\": \"external\",")
                .append("        \"external-label\": \"json-encryption\"")
                .append("      },")
                .append("      \"content\": { \"type\": \"bidb\", \"fileName\": \"").append(TEST_FILE_NAME)
                .append("\" }")
                .append("    }")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"6A736F6E-0011-0010-8000-00AA00389B71\", \"label\": \"json-encryption\" },")
                .append("    \"content\": { \"type\": \"json\", \"fileName\":\"").append(TEST_FILE_NAME).append("\" }")
                .append("  }")
                .append("]");

        return requestBody.toString();
    }

    private String getRequestWithAccessRules() {
        StringBuilder requestBody = new StringBuilder();

        requestBody.append("[")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"74B11BBF-F21D-4EEA-98C1-0BEBF23AEFD3\" },")
                .append("    \"content\": {")
                .append("      \"protectionDescription\": {")
                .append("        \"type\": \"pspd\",")
                .append("        \"method\": \"aes-256-cbc-iv\",")
                .append("        \"ivHexString\": \"D9BBA15016D876F67532FAFB8B851D24\",")
                .append("        \"access-rules-label\": \"xaml-rules-box\"")
                .append("      },")
                .append("      \"content\": { \"type\": \"bidb\", \"fileName\": \"").append(TEST_FILE_NAME)
                .append("\" }")
                .append("    }")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"786D6C20-0011-0010-8000-00AA00389B71\", \"label\": \"xacml-rules-box\" },")
                .append("    \"content\":{ \"type\": \"xml\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("  }")
                .append("]");

        return requestBody.toString();
    }

}