package org.mipams.privsec.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mipams.jumbf.entities.BinaryDataBox;
import org.mipams.jumbf.entities.DescriptionBox;
import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.services.content_types.JsonContentType;
import org.mipams.jumbf.services.content_types.XmlContentType;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.privsec.entities.ProtectionDescriptionBox;
import org.mipams.privsec.services.content_types.ProtectionContentType;

import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.privsec.config.PrivsecConfig;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PrivsecConfig.class, JumbfConfig.class })
@ActiveProfiles("test")
public class ProtectionBoxTests extends AbstractIntegrationTests {

    @BeforeAll
    static void initRequest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testProtectionBoxAes() throws Exception {

        ProtectionDescriptionBox protectionDescriptionBox = new ProtectionDescriptionBox();
        protectionDescriptionBox.setAes256CbcProtection();
        protectionDescriptionBox.updateBmffHeadersBasedOnBox();

        JumbfBox givenJumbfBox = getProtectionJumbfBoxBasedOnProtectionDescriptionBox(protectionDescriptionBox);
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testProtectionBoxAesWithIv() throws Exception {
        ProtectionDescriptionBox protectionDescriptionBox = new ProtectionDescriptionBox();
        protectionDescriptionBox.setAes256CbcWithIvProtection();
        byte[] iv = CoreUtils.convertHexToByteArray("D9BBA15016D876F67532FAFB8B851D24");
        protectionDescriptionBox.setIv(iv);
        protectionDescriptionBox.updateBmffHeadersBasedOnBox();

        JumbfBox givenJumbfBox = getProtectionJumbfBoxBasedOnProtectionDescriptionBox(protectionDescriptionBox);
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testProtectionBoxWithExternalEncryption() throws Exception {
        ProtectionDescriptionBox protectionDescriptionBox = new ProtectionDescriptionBox();
        protectionDescriptionBox.setProtectionMethodAsExternallyReferenced();
        protectionDescriptionBox.setEncLabel("encryption-reference");
        protectionDescriptionBox.updateBmffHeadersBasedOnBox();

        JumbfBox protectionJumbfBox = getProtectionJumbfBoxBasedOnProtectionDescriptionBox(protectionDescriptionBox);

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());
        jsonBox.updateBmffHeadersBasedOnBox();

        JsonContentType jsonContentType = new JsonContentType();
        String contentTypeUuid = jsonContentType.getContentTypeUuid();

        DescriptionBox dBox = new DescriptionBox();
        dBox.setUuid(contentTypeUuid);
        dBox.setLabel("encryption-reference");
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        JumbfBox jsonJumbfBox = MockJumbfBox.generateJumbfBox(dBox, List.of(jsonBox));

        List<JumbfBox> givenJumbfBoxList = List.of(protectionJumbfBox, jsonJumbfBox);
        List<JumbfBox> parsedJumbfBoxList = generateJumbfFileAndParseBox(givenJumbfBoxList);

        assertEquals(givenJumbfBoxList, parsedJumbfBoxList);
    }

    @Test
    void testProtectionBoxWithAccessRules() throws Exception {
        ProtectionDescriptionBox protectionDescriptionBox = new ProtectionDescriptionBox();
        protectionDescriptionBox.setAes256CbcProtection();
        protectionDescriptionBox.setArLabel("access-rules-reference");
        protectionDescriptionBox.includeAccessRulesInToggle();
        protectionDescriptionBox.updateBmffHeadersBasedOnBox();
        JumbfBox protectionJumbfBox = getProtectionJumbfBoxBasedOnProtectionDescriptionBox(protectionDescriptionBox);

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent(TEST_CONTENT.getBytes());
        xmlBox.updateBmffHeadersBasedOnBox();

        XmlContentType xmlContentType = new XmlContentType();
        String contentTypeUuid = xmlContentType.getContentTypeUuid();

        DescriptionBox dBox = new DescriptionBox();
        dBox.setUuid(contentTypeUuid);
        dBox.setLabel("access-rules-reference");
        dBox.computeAndSetToggleBasedOnFields();
        dBox.updateBmffHeadersBasedOnBox();

        JumbfBox xmlJumbfBox = MockJumbfBox.generateJumbfBox(dBox, List.of(xmlBox));

        List<JumbfBox> givenJumbfBoxList = List.of(protectionJumbfBox, xmlJumbfBox);
        List<JumbfBox> parsedJumbfBoxList = generateJumbfFileAndParseBox(givenJumbfBoxList);

        assertEquals(givenJumbfBoxList, parsedJumbfBoxList);
    }

    JumbfBox getProtectionJumbfBoxBasedOnProtectionDescriptionBox(ProtectionDescriptionBox pdBox)
            throws MipamsException {

        ProtectionContentType protectionContentType = new ProtectionContentType();
        String contentTypeUuid = protectionContentType.getContentTypeUuid();

        BinaryDataBox binaryDataBox = new BinaryDataBox();
        binaryDataBox.setFileUrl(TEST_FILE_PATH);
        binaryDataBox.updateBmffHeadersBasedOnBox();

        return MockJumbfBox.generateJumbfBoxWithContent(contentTypeUuid, List.of(pdBox, binaryDataBox));
    }
}
