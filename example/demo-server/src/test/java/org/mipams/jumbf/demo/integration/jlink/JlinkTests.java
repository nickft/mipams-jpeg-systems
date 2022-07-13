package org.mipams.jumbf.demo.integration.jlink;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.core.entities.JumbfBoxBuilder;
import org.mipams.jumbf.core.entities.XmlBox;
import org.mipams.jumbf.core.services.CoreGeneratorService;
import org.mipams.jumbf.core.services.content_types.ContiguousCodestreamContentType;
import org.mipams.jumbf.core.services.content_types.XmlContentType;
import org.mipams.jumbf.core.util.CoreUtils;
import org.mipams.jumbf.demo.integration.AbstractParserTests;
import org.mipams.jumbf.jlink.services.JlinkContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JlinkTests extends AbstractParserTests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testParseJlink() throws Exception {

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent("Hello World".getBytes());

        JumbfBoxBuilder xmlContentBoxBuilder = new JumbfBoxBuilder(new XmlContentType());
        xmlContentBoxBuilder.setJumbfBoxAsRequestable();
        xmlContentBoxBuilder.setPaddingSize(1);
        xmlContentBoxBuilder.appendContentBox(xmlBox);

        ContiguousCodestreamBox jp2c = new ContiguousCodestreamBox();
        jp2c.setFileUrl(TEST_FILE_PATH);
        JumbfBoxBuilder jp2cContentBoxBuilder = new JumbfBoxBuilder(new ContiguousCodestreamContentType());
        jp2cContentBoxBuilder.setJumbfBoxAsRequestable();
        jp2cContentBoxBuilder.setPaddingSize(1);
        jp2cContentBoxBuilder.setLabel("Image ref");
        jp2cContentBoxBuilder.appendContentBox(jp2c);

        JumbfBoxBuilder jlinkBuilder = new JumbfBoxBuilder(new JlinkContentType());
        jlinkBuilder.setJumbfBoxAsRequestable();
        jlinkBuilder.setLabel("JLINK Label: 2");
        jlinkBuilder.setId(2);
        jlinkBuilder.setPaddingSize(10);

        jlinkBuilder.appendContentBox(xmlContentBoxBuilder.getResult());
        jlinkBuilder.appendContentBox(jp2cContentBoxBuilder.getResult());

        JumbfBoxBuilder mainJlinkBuilder = new JumbfBoxBuilder(new JlinkContentType());
        mainJlinkBuilder.setJumbfBoxAsRequestable();
        mainJlinkBuilder.setId(1);
        mainJlinkBuilder.setLabel("JLINK Label: 1");

        mainJlinkBuilder.appendContentBox(xmlContentBoxBuilder.getResult());
        mainJlinkBuilder.appendContentBox(jlinkBuilder.getResult());
        mainJlinkBuilder.appendContentBox(jp2cContentBoxBuilder.getResult());

        String targetUrl = CoreUtils.getFullPath(TEST_DIRECTORY, "jlink_test.jumbf");

        coreGeneratorService.generateJumbfMetadataToFile(List.of(mainJlinkBuilder.getResult()), targetUrl);

        testParseBoxFromFile("jlink_test.jumbf");
    }
}
