package org.mipams.jpeg360;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mipams.jpeg360.config.Jpeg360Config;
import org.mipams.jpeg360.entities.Jpeg360AcceleratedRoi;
import org.mipams.jpeg360.entities.Jpeg360ImageMetadata;
import org.mipams.jpeg360.entities.Jpeg360Metadata;
import org.mipams.jpeg360.entities.Jpeg360Viewport;
import org.mipams.jpeg360.services.Jpeg360ContentType;
import org.mipams.jpeg360.services.Jpeg360XmlGenerator;
import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.ContiguousCodestreamBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.entities.XmlBox;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.services.content_types.ContiguousCodestreamContentType;
import org.mipams.jumbf.services.content_types.XmlContentType;
import org.mipams.jumbf.util.CoreUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Jpeg360Config.class, JumbfConfig.class })
@ActiveProfiles("test")
public class Jpeg360Tests {

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

    @Autowired
    Jpeg360XmlGenerator jpeg360XmlGenerator;

    @Test
    void generateAndParseJpeg360Elements() throws Exception {
        Jpeg360Metadata element = prepareScenario();

        XmlBox xmlBox = jpeg360XmlGenerator.getXmlBoxFromJpeg360MetadataElement(element);
        JumbfBox createdJumbfBox = createJpeg360JumbfBoxBasedOnXmlBox(xmlBox);

        String targetUrl = CoreUtils.getFullPath(CoreUtils.getTempDir(), "jpeg360.jumbf");
        coreGeneratorService.generateJumbfMetadataToFile(List.of(createdJumbfBox), targetUrl);

        List<JumbfBox> parsedList = coreParserService.parseMetadataFromFile(targetUrl);

        assertEquals(createdJumbfBox, parsedList.get(0));
        CoreUtils.deleteFile(targetUrl);
    }

    private Jpeg360Metadata prepareScenario() {

        Jpeg360Metadata jpeg360Metadata = new Jpeg360Metadata();
        jpeg360Metadata.setNextId(0);
        // Add Image
        Jpeg360ImageMetadata imageMetadata = new Jpeg360ImageMetadata();
        imageMetadata.setVersion(2);
        imageMetadata.setUmfId(0);
        jpeg360Metadata.setImageMetadata(imageMetadata);

        // Add viewport
        Jpeg360Viewport viewport = new Jpeg360Viewport();
        viewport.setUmfId(1);
        jpeg360Metadata.addViewport(viewport);

        // Add AcceleratedRoi
        Jpeg360AcceleratedRoi acceleratedRoi = new Jpeg360AcceleratedRoi();
        acceleratedRoi.setUmfId(2);
        acceleratedRoi.setRoiNumber(0);
        acceleratedRoi.setRoiPosX(0);
        acceleratedRoi.setRoiPosX(1);
        acceleratedRoi.setBlockWidth(4);
        acceleratedRoi.setBlockHeight(4);
        acceleratedRoi.setOffsetX(0);
        acceleratedRoi.setWidthInBlocks(1);
        acceleratedRoi.setHeightInBlocks(1);
        acceleratedRoi.setAssociatedViewport(0);
        acceleratedRoi.setBoxReference("self#jumbf=label");
        jpeg360Metadata.addAcceleratedRois(acceleratedRoi);
        return jpeg360Metadata;
    }

    @Test
    void parseJpeg360Element() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jpeg360v1.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    @Test
    void parseJpeg360ElementWithStereostopicFormat() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jpeg360v2.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    @Test
    void parseJpeg360ElementWithEmptyMetadataElements() throws Exception {
        String xmlFileUrl = ResourceUtils.getFile("classpath:jpeg360_schema_only.rdf").getAbsolutePath();
        runTestWithGivenXmlFile(xmlFileUrl);
    }

    void runTestWithGivenXmlFile(String xmlFileAbsolutePath) throws Exception {
        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent(Files.readAllBytes(Path.of(xmlFileAbsolutePath)));

        JumbfBox createdJumbfBox = createJpeg360JumbfBoxBasedOnXmlBox(xmlBox);

        String targetUrl = CoreUtils.getFullPath(CoreUtils.getTempDir(), "jpeg360.jumbf");
        coreGeneratorService.generateJumbfMetadataToFile(List.of(createdJumbfBox), targetUrl);

        List<JumbfBox> parsedList = coreParserService.parseMetadataFromFile(targetUrl);

        assertEquals(createdJumbfBox, parsedList.get(0));
        CoreUtils.deleteFile(targetUrl);
    }

    private JumbfBox createJpeg360JumbfBoxBasedOnXmlBox(XmlBox xmlBox) throws Exception {
        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

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

        JumbfBoxBuilder monoscopicJpeg360Builder = new JumbfBoxBuilder(new Jpeg360ContentType());
        monoscopicJpeg360Builder.setJumbfBoxAsRequestable();
        monoscopicJpeg360Builder.setLabel("JLINK Label: 2");
        monoscopicJpeg360Builder.setId(2);
        monoscopicJpeg360Builder.setPaddingSize(10);

        monoscopicJpeg360Builder.appendContentBox(xmlContentBoxBuilder.getResult());
        monoscopicJpeg360Builder.appendContentBox(jp2cContentBoxBuilder.getResult());

        return monoscopicJpeg360Builder.getResult();
    }

}
