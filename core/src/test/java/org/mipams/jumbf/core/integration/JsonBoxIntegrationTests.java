package org.mipams.jumbf.core.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.BmffBox;
import org.mipams.jumbf.core.entities.DescriptionBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.JumbfBoxBuilder;
import org.mipams.jumbf.core.entities.PrivateBox;
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
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
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
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.setId(12345);
        builder.setSha256Hash(
                DatatypeConverter.parseHexBinary("321242ad321242aa321242ad321242aa321242ad321242aa321242ad321242aa"));
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
        jsonBox.setContent(TEST_CONTENT.getBytes());

        DescriptionBox dBox = new DescriptionBox();

        dBox.setUuid(jsonContentType.getContentTypeUuid());

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream("123321123".getBytes());) {

            JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
            builder.setPrivateField(jsonBox);
            builder.appendContentBox(jsonBox);

            JumbfBox givenJumbfBox = builder.getResult();
            assertEquals(givenJumbfBox.getDescriptionBox().isRequestable(), false);

            JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);
            givenJumbfBox.getDescriptionBox()
                    .setPrivateField(parsedJumbfBox.getDescriptionBox().getPrivateField());

            assertEquals(givenJumbfBox, parsedJumbfBox);
        }
    }

    @Test
    void testJsonBoxWithLboxEqualsTo0() throws Exception {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();
        givenJumbfBox.setLBox(0);

        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);
        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testJsonBoxWithSinglePrivateField() throws Exception {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());
        jsonBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.appendContentBox(jsonBox);
        builder.setPrivateField(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    @Test
    void testJsonBoxWithMultiplePrivateFields() throws Exception {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.appendContentBox(jsonBox);

        JumbfBox privateBox = getPrivateBox();
        builder.setPrivateField(privateBox);

        JumbfBox givenJumbfBox = builder.getResult();
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    private JumbfBox getPrivateBox() throws MipamsException {
        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.appendContentBox(jsonBox);

        JumbfBox box1 = builder.getResult();
        JumbfBox box2 = new JumbfBoxBuilder(box1).getResult();

        PrivateBox pBox = new PrivateBox();
        pBox.setDescriptionBox(new DescriptionBox());
        pBox.getDescriptionBox().setUuid(CoreUtils.randomStringGenerator());

        builder = new JumbfBoxBuilder(pBox);
        builder.appendContentBox(box1);
        builder.appendContentBox(box2);

        return builder.getResult();
    }
}
