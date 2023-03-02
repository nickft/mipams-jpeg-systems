package org.mipams.jlink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.services.JpegCodestreamGenerator;
import org.mipams.jumbf.services.JpegCodestreamParser;
import org.mipams.jumbf.services.content_types.ContiguousCodestreamContentType;
import org.mipams.jumbf.services.content_types.XmlContentType;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jlink.config.JlinkConfig;
import org.mipams.jlink.services.JlinkContentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { JlinkConfig.class, JumbfConfig.class })
@ActiveProfiles("test")
public class JlinkTests extends AbstractParserTests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

    @Autowired
    JpegCodestreamGenerator jpegCodestreamGenerator;

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @BeforeAll
    static void initTest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void testParseJlinkInJumbfFile() throws Exception {

        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent("Hello World".getBytes());

        JumbfBoxBuilder xmlContentBoxBuilder = new JumbfBoxBuilder(new XmlContentType());
        xmlContentBoxBuilder.setJumbfBoxAsRequestable();
        xmlContentBoxBuilder.setPaddingSize(1);
        xmlContentBoxBuilder.appendContentBox(xmlBox);

        ContiguousCodestreamBox jp2c = new ContiguousCodestreamBox();
        jp2c.setFileUrl(assetFileUrl);

        JumbfBoxBuilder jp2cContentBoxBuilder = new JumbfBoxBuilder(new ContiguousCodestreamContentType());
        jp2cContentBoxBuilder.setJumbfBoxAsRequestable();
        jp2cContentBoxBuilder.setPaddingSize(1);
        jp2cContentBoxBuilder.setLabel("Image: 1");
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

        JumbfBox createdJumbfBox = mainJlinkBuilder.getResult();

        coreGeneratorService.generateJumbfMetadataToFile(List.of(createdJumbfBox), targetUrl);

        List<JumbfBox> parsedList = coreParserService.parseMetadataFromFile(targetUrl);

        assertEquals(createdJumbfBox, parsedList.get(0));
    }

    @Test
    void testParseJlinkInJpegFile() throws Exception {

        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent("Hello World".getBytes());

        JumbfBoxBuilder xmlContentBoxBuilder = new JumbfBoxBuilder(new XmlContentType());
        xmlContentBoxBuilder.setJumbfBoxAsRequestable();
        xmlContentBoxBuilder.setPaddingSize(1);
        xmlContentBoxBuilder.appendContentBox(xmlBox);

        ContiguousCodestreamBox jp2c = new ContiguousCodestreamBox();
        jp2c.setFileUrl(assetFileUrl);

        JumbfBoxBuilder jp2cContentBoxBuilder = new JumbfBoxBuilder(new ContiguousCodestreamContentType());
        jp2cContentBoxBuilder.setJumbfBoxAsRequestable();
        jp2cContentBoxBuilder.setPaddingSize(1);
        jp2cContentBoxBuilder.setLabel("Image: 1");
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

        String targetUrl = CoreUtils.getFullPath(TEST_DIRECTORY, "jlink_test.jpeg");

        JumbfBox createdJumbfBox = mainJlinkBuilder.getResult();

        jpegCodestreamGenerator.generateJumbfMetadataToFile(List.of(createdJumbfBox), assetFileUrl, targetUrl);

        List<JumbfBox> parsedList = jpegCodestreamParser.parseMetadataFromFile(targetUrl);

        assertEquals(createdJumbfBox, parsedList.get(0));
    }
}
