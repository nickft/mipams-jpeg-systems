package org.mipams.jlink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
import org.mipams.jlink.entities.JlinkImage;
import org.mipams.jlink.entities.JlinkElement;
import org.mipams.jlink.entities.JlinkLink;
import org.mipams.jlink.entities.JlinkRegion;
import org.mipams.jlink.entities.JlinkScene;
import org.mipams.jlink.services.JlinkContentType;
import org.mipams.jlink.services.JlinkXmlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { JlinkConfig.class, JumbfConfig.class })
@ActiveProfiles("test")
public class JlinkTests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

    @Autowired
    JpegCodestreamGenerator jpegCodestreamGenerator;

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @Autowired
    JlinkXmlGenerator jlinkXmlGenerator;

    @Test
    void parseJlinkElementWithoutPopulatedMetadataElements() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jlink-schema-only.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    @Test
    void parseJlinkElementWithoutOptionalXmpHeaders() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jlink.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    @Test
    void parseJlinkElementWithOptionalXmpHeaders() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jlink-xmp-headers.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    @Test
    void parseJlinkElementWithOptionalXmpHeaders2() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jlink-xmp-headers-2.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    @Test
    void parseJlinkElementWithOptionalXmpMetaHeader() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jlink-xmp-headers-3.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    @Test
    void parseJlinkElementWithOptionalXPacketHeader() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jlink-xmp-headers-4.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    void runTestWithGivenXmlFile(String xmlFileAbsolutePath) throws Exception {
        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent(Files.readAllBytes(Path.of(xmlFileAbsolutePath)));

        JumbfBoxBuilder xmlContentBoxBuilder = new JumbfBoxBuilder(new XmlContentType());
        xmlContentBoxBuilder.setJumbfBoxAsRequestable();
        xmlContentBoxBuilder.setLabel("JLINK XML Schema");
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

        String targetUrl = CoreUtils.getFullPath(CoreUtils.getTempDir(), "jlink_test.jumbf");

        JumbfBox createdJumbfBox = mainJlinkBuilder.getResult();

        coreGeneratorService.generateJumbfMetadataToFile(List.of(createdJumbfBox), targetUrl);

        List<JumbfBox> parsedList = coreParserService.parseMetadataFromFile(targetUrl);

        assertEquals(createdJumbfBox, parsedList.get(0));
        CoreUtils.deleteFile(targetUrl);
    }

    @Test
    void generateAndParseJlinkElements() throws Exception {
        JlinkElement element = prepareScenario();
        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();
        XmlBox xmlBox = jlinkXmlGenerator.getXmlBoxFromJlinkElement(element);

        JumbfBoxBuilder xmlContentBoxBuilder = new JumbfBoxBuilder(new XmlContentType());
        xmlContentBoxBuilder.setJumbfBoxAsRequestable();
        xmlContentBoxBuilder.setLabel("JLINK XML Schema");
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

        String targetUrl = CoreUtils.getFullPath(CoreUtils.getTempDir(), "jlink_test.jumbf");

        JumbfBox createdJumbfBox = mainJlinkBuilder.getResult();

        coreGeneratorService.generateJumbfMetadataToFile(List.of(createdJumbfBox), targetUrl);

        List<JumbfBox> parsedList = coreParserService.parseMetadataFromFile(targetUrl);

        assertEquals(createdJumbfBox, parsedList.get(0));
        CoreUtils.deleteFile(targetUrl);
    }

    private JlinkElement prepareScenario() {
        JlinkElement element = new JlinkElement();

        JlinkScene scene = new JlinkScene();

        JlinkImage image = new JlinkImage();
        image.setFormat("format");
        image.setHref("test");

        scene.setTitle("");
        scene.setNote("");
        scene.setImage(image);

        JlinkRegion region = new JlinkRegion();
        region.setX(12.0);
        region.setY(12.0);
        region.setW(12.0);
        region.setH(12.0);
        region.setRotation(12.0);
        region.setShape("rectangle");

        JlinkLink link1 = new JlinkLink();
        link1.setTo("test");
        link1.setSprite("test");
        link1.setRegion(region);
        link1.setDuration(600);
        link1.setVpid(1);

        JlinkLink link2 = new JlinkLink();
        link2.setTo("test2");
        link2.setSprite("test");
        link2.setRegion(null);
        link2.setRegion(region);

        element.setNextId(0);
        element.setScene(scene);
        element.setLinks(List.of(link1, link2));

        return element;
    }
}
