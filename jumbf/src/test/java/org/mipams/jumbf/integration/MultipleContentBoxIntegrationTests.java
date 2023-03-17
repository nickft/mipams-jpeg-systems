package org.mipams.jumbf.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.CborBox;
import org.mipams.jumbf.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.services.content_types.CborContentType;
import org.mipams.jumbf.services.content_types.ContiguousCodestreamContentType;
import org.mipams.jumbf.services.content_types.JsonContentType;
import org.mipams.jumbf.services.content_types.XmlContentType;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JumbfConfig.class)
@ActiveProfiles("test")
public class MultipleContentBoxIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

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

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.appendContentBox(jsonBox);

        return builder.getResult();
    }

    private JumbfBox createXmlJumbfBox() throws MipamsException {

        XmlContentType xmlContentType = new XmlContentType();

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(xmlContentType);
        builder.appendContentBox(xmlBox);

        return builder.getResult();
    }

    private JumbfBox createJp2cJumbfBox() throws Exception {
        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

        ContiguousCodestreamContentType contentType = new ContiguousCodestreamContentType();

        ContiguousCodestreamBox jp2cBox = new ContiguousCodestreamBox();
        jp2cBox.setFileUrl(assetFileUrl);

        JumbfBoxBuilder builder = new JumbfBoxBuilder(contentType);
        builder.appendContentBox(jp2cBox);

        return builder.getResult();
    }

    private JumbfBox createCborJumbfBox() throws MipamsException {
        CborContentType cborContentType = new CborContentType();

        CborBox cborBox = new CborBox();
        cborBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(cborContentType);
        builder.appendContentBox(cborBox);

        return builder.getResult();
    }

}
