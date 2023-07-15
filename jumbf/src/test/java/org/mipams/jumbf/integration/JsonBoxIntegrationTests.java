package org.mipams.jumbf.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.DescriptionBox;
import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.entities.PrivateBox;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.services.boxes.JsonBoxService;
import org.mipams.jumbf.services.boxes.PrivateBoxService;
import org.mipams.jumbf.services.content_types.JsonContentType;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JumbfConfig.class)
@ActiveProfiles("test")
public class JsonBoxIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

    @Autowired
    JsonBoxService jsonBoxService;

    @Autowired
    PrivateBoxService privateBoxService;

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
                CoreUtils.convertHexToByteArray("321242ad321242aa321242ad321242aa321242ad321242aa321242ad321242aa"));
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
        builder.setId(12345);
        builder.setSha256Hash(
                CoreUtils.convertHexToByteArray("321242ad321242aa321242ad321242aa321242ad321242aa321242ad321242aa"));
        builder.setPaddingSize(10);
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

        PrivateBox privateBox = getPrivateBox();
        builder.setPrivateField(privateBox);

        JumbfBox givenJumbfBox = builder.getResult();
        JumbfBox parsedJumbfBox = generateJumbfFileAndParseBox(List.of(givenJumbfBox)).get(0);

        assertEquals(givenJumbfBox, parsedJumbfBox);
    }

    private PrivateBox getPrivateBox() throws MipamsException {
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

        return (PrivateBox) builder.getResult();
    }

    @Test
    void generateBoxes() throws Exception {
        PrivateBox pBox = (PrivateBox) getPrivateBox();
        String privateBoxWithMultipleBoxesUrl = ResourceUtils.getFile("classpath:multiple-private.jumbf")
                .getAbsolutePath();
        privateBoxService.writeToJumbfFile(pBox, new FileOutputStream(privateBoxWithMultipleBoxesUrl));

        JsonBox jBox = new JsonBox();
        jBox.setContent(TEST_CONTENT.getBytes());
        jBox.updateBmffHeadersBasedOnBox();
        String privateBoxWithSingleBoxUrl = ResourceUtils.getFile("classpath:single-private.obj")
                .getAbsolutePath();
        jsonBoxService.writeToJumbfFile(jBox, new FileOutputStream(privateBoxWithSingleBoxUrl));

        final String content = "{ \"widget\": { \"debug\": \"on\", \"window\": { \"title\": \"Sample Konfabulator Widget\", \"name\": \"main_window\", \"width\": 500, \"height\": 500 }, \"image\": { \"src\": \"Images/Sun.png\", \"name\": \"sun1\", \"hOffset\": 250, \"vOffset\": 250, \"alignment\": \"center\" }, \"text\": { \"data\": \"Click Here\", \"size\": 36, \"style\": \"bold\", \"name\": \"text1\", \"hOffset\": 250, \"vOffset\": 100, \"alignment\": \"center\", \"onMouseUp\": \"sun1.opacity = (sun1.opacity / 100) * 90;\" } }}";

        CBORMapper mapper = new CBORMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);

        byte[] cborData = mapper.writeValueAsBytes(content);
        String cborContentUrl = ResourceUtils.getFile("classpath:content.cbor")
                .getAbsolutePath();
        CoreUtils.writeByteArrayToOutputStream(cborData, new FileOutputStream(cborContentUrl));
    }

    @Test
    void ignoreUnsupportedBoxesWhenGenerating() throws Exception {
        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.setLabel("Test label");
        builder.setJumbfBoxAsRequestable();
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        JumbfBox supportedBox = builder.getResult();

        builder = new JumbfBoxBuilder(jsonContentType);
        builder.setLabel("Test label");
        builder.setJumbfBoxAsRequestable();
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        JumbfBox unsupportedBox = builder.getResult();
        unsupportedBox.getDescriptionBox().setUuid(CoreUtils.randomStringGenerator());

        List<JumbfBox> parsedJumbfBoxes = generateJumbfFileAndParseBox(List.of(supportedBox, unsupportedBox));
        assertEquals(1, parsedJumbfBoxes.size());
    }

    @Test
    void ignoreUnsupportedBoxesWhenParsing() throws Exception {
        String corruptedUuidOnJumbf = ResourceUtils.getFile("classpath:corrupted_uuid_json_box.jumbf")
                .getAbsolutePath();
        List<JumbfBox> parsedJumbfBoxes = testParseMetadataFromJumbfFile(corruptedUuidOnJumbf);
        assertEquals(0, parsedJumbfBoxes.size());
    }
}
