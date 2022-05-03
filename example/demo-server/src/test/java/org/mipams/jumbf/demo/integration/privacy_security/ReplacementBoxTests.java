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
public class ReplacementBoxTests extends AbstractParserTests {

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
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
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"DC28B95F-B68A-498E-8064-0FCA845D6B0E\", \"label\": \"test\" }, ")
                .append("  \"content\": { ")
                .append("    \"replacementDescription\": {")
                .append("      \"type\": \"psrd\", ")
                .append("      \"replacementType\": \"box\", ")
                .append("      \"auto-apply\": false, ")
                .append("      \"offset\": 123123123123 ")
                .append("    }, ")
                .append("    \"content\": { ")
                .append("      \"type\": \"jumb\", ")
                .append("      \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1\", \"label\": \"Content which replaces the referenced box\" }, ")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("    } ")
                .append("  } ")
                .append("}");

        return requestBody.toString();
    }

    String getBoxReplacementBoxRequestWithLabel() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{ ")
                .append("  \"type\": \"jumb\", ")
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"DC28B95F-B68A-498E-8064-0FCA845D6B0E\", \"label\": \"test\" }, ")
                .append("  \"content\": { ")
                .append("    \"replacementDescription\": {")
                .append("      \"type\": \"psrd\", ")
                .append("      \"replacementType\": \"box\", ")
                .append("      \"auto-apply\": false, ")
                .append("      \"label\": \"reference-box\" ")
                .append("    }, ")
                .append("    \"content\": { ")
                .append("      \"type\": \"jumb\", ")
                .append("      \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1\", \"label\": \"Content which replaces the referenced box\" }, ")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("    } ")
                .append("  } ")
                .append("}");

        return requestBody.toString();
    }

    String getBoxReplacementBoxRequestWithMultipleReplacement() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{ ")
                .append("  \"type\": \"jumb\", ")
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"DC28B95F-B68A-498E-8064-0FCA845D6B0E\", \"label\": \"test\" }, ")
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
                .append("        \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"6579D6FB-DBA2-446B-B2AC-1B82FEEB89D1\", \"label\": \"One of the content which replaces the referenced box\" }, ")
                .append("        \"content\": { \"type\": \"jp2c\", \"fileName\":\"").append(TEST_FILE_NAME)
                .append("\" }")
                .append("      },")
                .append("      { ")
                .append("        \"type\": \"jumb\", ")
                .append("        \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"786D6C20-0011-0010-8000-00AA00389B71\", \"label\": \"One of the content which replaces the referenced box\" }, ")
                .append("        \"content\": { \"type\": \"xml\", \"fileName\":\"").append(TEST_FILE_NAME)
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
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"DC28B95F-B68A-498E-8064-0FCA845D6B0E\", \"label\": \"test\" },")
                .append("  \"content\": {")
                .append("    \"replacementDescription\": ")
                .append("    {")
                .append("      \"type\": \"psrd\",")
                .append("      \"replacementType\": \"app\",")
                .append("      \"auto-apply\": false,")
                .append("      \"offset\": 123123123123")
                .append("    },")
                .append("    \"content\": { \"type\": \"bidb\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getFileReplacementBoxRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"DC28B95F-B68A-498E-8064-0FCA845D6B0E\", \"label\": \"test\" },")
                .append("  \"content\": {")
                .append("    \"replacementDescription\": ")
                .append("    {")
                .append("      \"type\": \"psrd\",")
                .append("      \"replacementType\": \"file\",")
                .append("      \"auto-apply\": false")
                .append("    },")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

    String getRoiReplacementBoxRequest() throws IOException {

        StringBuilder requestBody = new StringBuilder();

        requestBody.append("{")
                .append("  \"type\": \"jumb\",")
                .append("  \"description\": { \"type\": \"jumd\", \"contentTypeUuid\": \"DC28B95F-B68A-498E-8064-0FCA845D6B0E\", \"label\": \"test\" },")
                .append("  \"content\": {")
                .append("    \"replacementDescription\": ")
                .append("    {")
                .append("      \"type\": \"psrd\",")
                .append("      \"replacementType\": \"roi\",")
                .append("      \"auto-apply\": false,")
                .append("      \"offset-X\": 12232,")
                .append("      \"offset-Y\": 21312")
                .append("    },")
                .append("    \"content\": { \"type\": \"jp2c\", \"fileName\": \"").append(TEST_FILE_NAME).append("\" }")
                .append("  }")
                .append("}");

        return requestBody.toString();
    }

}