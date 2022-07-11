package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mipams.jumbf.core.entities.CborBox;
import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.JumbfBoxBuilder;
import org.mipams.jumbf.core.entities.XmlBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.services.CoreParserService;
import org.mipams.jumbf.core.services.content_types.CborContentType;
import org.mipams.jumbf.core.services.content_types.ContiguousCodestreamContentType;
import org.mipams.jumbf.core.services.content_types.JsonContentType;
import org.mipams.jumbf.core.services.content_types.XmlContentType;
import org.mipams.jumbf.core.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class MultipleContentBoxIntegrationTests extends AbstractIntegrationTests {

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
    void testMultipleJumbfBox() throws Exception {

        List<JumbfBox> givenJumbfBoxList = new ArrayList<>();

        JumbfBox jsonJumbfBox = createJsonJumbfBox();
        givenJumbfBoxList.add(jsonJumbfBox);

        JumbfBox xmlJumbfBox = createXmlJumbfBox();
        givenJumbfBoxList.add(xmlJumbfBox);

        JumbfBox jp2cJumbfBox = createJp2cJumbfBox();
        givenJumbfBoxList.add(jp2cJumbfBox);

        JumbfBox cborJumbfBox = createCborJumbfBox();
        givenJumbfBoxList.add(cborJumbfBox);

        List<JumbfBox> parsedJumbfBoxList = generateJumbfFileAndParseBox(givenJumbfBoxList);

        assertEquals(givenJumbfBoxList, parsedJumbfBoxList);

    }

    private JumbfBox createJsonJumbfBox() throws MipamsException {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());
        jsonBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.appendContentBox(jsonBox);

        return builder.getResult();
    }

    private JumbfBox createXmlJumbfBox() throws MipamsException {

        XmlContentType xmlContentType = new XmlContentType();

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent(TEST_CONTENT.getBytes());
        xmlBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(xmlContentType);
        builder.appendContentBox(xmlBox);

        return builder.getResult();
    }

    private JumbfBox createJp2cJumbfBox() throws MipamsException {
        ContiguousCodestreamContentType contentType = new ContiguousCodestreamContentType();

        ContiguousCodestreamBox jp2cBox = new ContiguousCodestreamBox();
        jp2cBox.setFileUrl(TEST_FILE_PATH);
        jp2cBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(contentType);
        builder.appendContentBox(jp2cBox);

        return builder.getResult();
    }

    private JumbfBox createCborJumbfBox() throws MipamsException {
        CborContentType cborContentType = new CborContentType();

        CborBox cborBox = new CborBox();
        cborBox.setContent(TEST_CONTENT.getBytes());
        cborBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(cborContentType);
        builder.appendContentBox(cborBox);

        return builder.getResult();
    }

}
