package org.mipams.jumbf.privacy_security.integration;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProtectionBoxTests extends AbstractIntegrationTests {

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp(TEST_FILE_PATH);
        fileCleanUp(JUMBF_FILE_PATH);
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
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"prtc\" },")
                .append("  \"content\": {")
                .append("    \"protectionDescription\": {")
                .append("      \"type\": \"pspd\",")
                .append("      \"method\": \"aes-256-cbc\"")
                .append("    },")
                .append("    \"content\": { \"type\": \"bidb\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getAesWithIVRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"prtc\" },")
                .append("  \"content\": {")
                .append("    \"protectionDescription\": {")
                .append("      \"type\": \"pspd\",")
                .append("      \"method\": \"aes-256-cbc-iv\",")
                .append("      \"ivHexString\": \"D9BBA15016D876F67532FAFB8B851D24\"")
                .append("    },")
                .append("    \"content\": { \"type\": \"bidb\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getRequestWithExternalEncryption() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("[")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"prtc\" },")
                .append("    \"content\": {")
                .append("      \"protectionDescription\": {")
                .append("        \"type\": \"pspd\",")
                .append("        \"method\": \"external\",")
                .append("        \"external-label\": \"json-encryption\"")
                .append("      },")
                .append("      \"content\": { \"type\": \"bidb\", \"fileUrl\": \"").append(TEST_FILE_PATH)
                .append("\" }")
                .append("    }")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"json\", \"label\": \"json-encryption\" },")
                .append("    \"content\": { \"type\": \"json\", \"fileUrl\":\"").append(TEST_FILE_PATH).append("\" }")
                .append("  }")
                .append("]");

        return requestBody.toString();
    }

    private String getRequestWithAccessRules() {
        StringBuilder requestBody = new StringBuilder();

        requestBody.append("[")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"prtc\" },")
                .append("    \"content\": {")
                .append("      \"protectionDescription\": {")
                .append("        \"type\": \"pspd\",")
                .append("        \"method\": \"aes-256-cbc-iv\",")
                .append("        \"ivHexString\": \"D9BBA15016D876F67532FAFB8B851D24\",")
                .append("        \"access-rules-label\": \"xaml-rules-box\"")
                .append("      },")
                .append("      \"content\": { \"type\": \"bidb\", \"fileUrl\": \"").append(TEST_FILE_PATH)
                .append("\" }")
                .append("    }")
                .append("  },")
                .append("  {")
                .append("    \"type\": \"jumb\",")
                .append("    \"description\": { \"type\": \"jumd\", \"contentType\": \"xml\", \"label\": \"xacml-rules-box\" },")
                .append("    \"content\":{ \"type\": \"xml\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("  }")
                .append("]");

        return requestBody.toString();
    }

}
