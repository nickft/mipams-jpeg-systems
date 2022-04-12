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
public class ReplacementBoxTests extends AbstractIntegrationTests {

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
    void testGenerateBoxReplacementBoxRequestWithOffset() throws Exception {
        testGenerateBoxEndpoint(getBoxReplacementBoxRequestWithOffset());
    }

    @Test
    void testParseBoxReplacementBoxRequestWithOffset() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateBoxReplacementBoxRequestWithLabel() throws Exception {
        testGenerateBoxEndpoint(getBoxReplacementBoxRequestWithLabel());
    }

    @Test
    void testParseBoxReplacementBoxRequestWithLabel() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateBoxReplacementBoxRequestWithMultipleReplacement() throws Exception {
        testGenerateBoxEndpoint(getBoxReplacementBoxRequestWithMultipleReplacement());
    }

    @Test
    void testParseBoxReplacementBoxRequestWithMultipleReplacement() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateAppReplacementBoxRequest() throws Exception {
        testGenerateBoxEndpoint(getAppReplacementBoxRequest());
    }

    @Test
    void testParseAppReplacementBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateFileReplacementBoxRequest() throws Exception {
        testGenerateBoxEndpoint(getFileReplacementBoxRequest());
    }

    @Test
    void testParseFileReplacementBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    @Test
    void testGenerateRoiReplacementBoxRequest() throws Exception {
        testGenerateBoxEndpoint(getRoiReplacementBoxRequest());
    }

    @Test
    void testParseRoiReplacementBoxRequest() throws Exception {
        testParseBoxFromFile(JUMBF_FILE_NAME);
    }

    String getBoxReplacementBoxRequestWithOffset() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{ ")
                .append("  \"type\": \"jumb\", ")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"rplc\", \"label\": \"test\" }, ")
                .append("  \"content\": { ")
                .append("    \"replacementDescription\": {")
                .append("      \"type\": \"psrd\", ")
                .append("      \"replacementType\": \"box\", ")
                .append("      \"auto-apply\": false, ")
                .append("      \"offset\": 123123123123 ")
                .append("    }, ")
                .append("    \"content\": { ")
                .append("      \"type\": \"jumb\", ")
                .append("      \"description\": { \"type\": \"jumd\", \"contentType\": \"jp2c\", \"label\": \"Content which replaces the referenced box\" }, ")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("    } ")
                .append("  } ")
                .append("}");

        return requestBody.toString();
    }

    String getBoxReplacementBoxRequestWithLabel() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{ ")
                .append("  \"type\": \"jumb\", ")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"rplc\", \"label\": \"test\" }, ")
                .append("  \"content\": { ")
                .append("    \"replacementDescription\": {")
                .append("      \"type\": \"psrd\", ")
                .append("      \"replacementType\": \"box\", ")
                .append("      \"auto-apply\": false, ")
                .append("      \"label\": \"reference-box\" ")
                .append("    }, ")
                .append("    \"content\": { ")
                .append("      \"type\": \"jumb\", ")
                .append("      \"description\": { \"type\": \"jumd\", \"contentType\": \"jp2c\", \"label\": \"Content which replaces the referenced box\" }, ")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("    } ")
                .append("  } ")
                .append("}");

        return requestBody.toString();
    }

    String getBoxReplacementBoxRequestWithMultipleReplacement() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{ ")
                .append("  \"type\": \"jumb\", ")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"rplc\", \"label\": \"test\" }, ")
                .append("  \"content\": { ")
                .append("    \"replacementDescription\": { ")
                .append("      \"type\": \"psrd\", ")
                .append("      \"replacementType\": \"box\", ")
                .append("      \"auto-apply\": false, ")
                .append("      \"label\": \"reference-box\" ")
                .append("    }, ")
                .append("    \"content\": [")
                .append("      { ")
                .append("        \"type\": \"jumb\", ")
                .append("        \"description\": { \"type\": \"jumd\", \"contentType\": \"jp2c\", \"label\": \"One of the content which replaces the referenced box\" }, ")
                .append("        \"content\": { \"type\": \"jp2c\", \"fileUrl\":\"").append(TEST_FILE_PATH)
                .append("\" }")
                .append("      },")
                .append("      { ")
                .append("        \"type\": \"jumb\", ")
                .append("        \"description\": { \"type\": \"jumd\", \"contentType\": \"xml\", \"label\": \"One of the content which replaces the referenced box\" }, ")
                .append("        \"content\": { \"type\": \"xml\", \"fileUrl\":\"").append(TEST_FILE_PATH)
                .append("\" }")
                .append("      }")
                .append("    ]  ")
                .append("  } ")
                .append("}");

        return requestBody.toString();
    }

    String getAppReplacementBoxRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"rplc\", \"label\": \"test\" },")
                .append("  \"content\": {")
                .append("    \"replacementDescription\": ")
                .append("    {")
                .append("      \"type\": \"psrd\",")
                .append("      \"replacementType\": \"app\",")
                .append("      \"auto-apply\": false,")
                .append("      \"offset\": 123123123123")
                .append("    },")
                .append("    \"content\": { \"type\": \"bidb\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getFileReplacementBoxRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"rplc\", \"label\": \"test\" },")
                .append("  \"content\": {")
                .append("    \"replacementDescription\": ")
                .append("    {")
                .append("      \"type\": \"psrd\",")
                .append("      \"replacementType\": \"file\",")
                .append("      \"auto-apply\": false")
                .append("    },")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getRoiReplacementBoxRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentType\": \"rplc\", \"label\": \"test\" },")
                .append("  \"content\": {")
                .append("    \"replacementDescription\": ")
                .append("    {")
                .append("      \"type\": \"psrd\",")
                .append("      \"replacementType\": \"roi\",")
                .append("      \"auto-apply\": false,")
                .append("      \"offset-X\": 12232,")
                .append("      \"offset-Y\": 21312")
                .append("    },")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileUrl\": \"").append(TEST_FILE_PATH).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

}
