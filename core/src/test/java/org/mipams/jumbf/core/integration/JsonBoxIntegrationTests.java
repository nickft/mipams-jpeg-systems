package org.mipams.jumbf.core.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.JumbfBoxBuilder;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.services.CoreParserService;
import org.mipams.jumbf.core.services.content_types.JsonContentType;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class JsonBoxIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

    @BeforeAll
    static void initRequest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testJsonBox() throws Exception {
        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl(TEST_FILE_PATH);
        jsonBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder();
        builder.setContentType(jsonContentType);
        builder.setLabel("Test label");
        builder.setJumbfBoxAsRequestable();
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testJsonBoxWithIdAndSignatureInDescriptionBox() throws Exception {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl(TEST_FILE_PATH);
        jsonBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder();
        builder.setId(12345);
        builder.setSha256Hash(
                DatatypeConverter.parseHexBinary("321242ad321242aa321242ad321242aa321242ad321242aa321242ad321242aa"));
        builder.setContentType(jsonContentType);
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();
        assertEquals(givenJumbfBox.getDescriptionBox().isRequestable(), false);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);
        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testJsonBoxWithPrivateFieldBox() throws Exception {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl(TEST_FILE_PATH);
        jsonBox.updateBmffHeadersBasedOnBox();

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(jsonContentType.getContentTypeUuid());

        String tempFileUrl = TEST_DIRECTORY + "private-field";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream("123321123".getBytes());
                OutputStream outputStream = new FileOutputStream(tempFileUrl);) {
            outputStream.write(inputStream.readAllBytes());
        }

        JumbfBoxBuilder builder = new JumbfBoxBuilder();
        builder.setContentType(jsonContentType);
        builder.setPrivateField(tempFileUrl);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();
        assertEquals(givenJumbfBox.getDescriptionBox().isRequestable(), false);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);
        givenJumbfBox.getDescriptionBox()
                .setPrivateBmffBoxUrl(parsedJumbfBox.getDescriptionBox().getPrivateBmffBoxUrl());

        assertEquals(givenJumbfBox, parsedJumbfBox);

        CoreUtils.deleteFile(tempFileUrl);
    }

    @Test
    void testJsonBoxWithInsufficientLengthPrivateFieldBox() throws Exception {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setFileUrl(TEST_FILE_PATH);
        jsonBox.updateBmffHeadersBasedOnBox();

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(jsonContentType.getContentTypeUuid());

        String tempFileUrl = TEST_DIRECTORY + CoreUtils.randomStringGenerator() + "-privateField";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream("12".getBytes());
                OutputStream outputStream = new FileOutputStream(tempFileUrl);) {
            outputStream.write(inputStream.readAllBytes());
        }

        JumbfBoxBuilder builder = new JumbfBoxBuilder();
        builder.setContentType(jsonContentType);
        builder.setPrivateField(tempFileUrl);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();
        assertEquals(givenJumbfBox.getDescriptionBox().isRequestable(), false);

        assertThrows(MipamsException.class, () -> {
            generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);
        });

        CoreUtils.deleteFile(tempFileUrl);
    }
}
