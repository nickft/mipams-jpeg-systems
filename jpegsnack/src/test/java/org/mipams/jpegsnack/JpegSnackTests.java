package org.mipams.jpegsnack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mipams.jpegsnack.config.JpegSnackConfig;
import org.mipams.jpegsnack.entities.Composition;
import org.mipams.jpegsnack.entities.InstructionParameter;
import org.mipams.jpegsnack.entities.InstructionSetBox;
import org.mipams.jpegsnack.entities.JpegSnackDescriptionBox;
import org.mipams.jpegsnack.entities.ObjectMetadataBox;
import org.mipams.jpegsnack.services.content_types.JpegSnackContentType;
import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.services.JpegCodestreamGenerator;
import org.mipams.jumbf.services.JpegCodestreamParser;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { JpegSnackConfig.class, JumbfConfig.class })
@ActiveProfiles("test")
public class JpegSnackTests {

    @Autowired
    JpegCodestreamGenerator jpegCodestreamGenerator;

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @Autowired
    CoreGeneratorService coreGeneratorService;

    @Autowired
    CoreParserService coreParserService;

    @Test
    void generateAndParseJpegSnackAsStandalone() throws Exception {
        String targetUrl = CoreUtils.getFullPath(CoreUtils.getTempDir(), "jpegsnack_test.jumbf");

        JumbfBox createdJumbfBox = prepareJpegSnackJumbfBox();
        coreGeneratorService.generateJumbfMetadataToFile(List.of(createdJumbfBox), targetUrl);

        List<JumbfBox> parsedList = coreParserService.parseMetadataFromFile(targetUrl);

        assertEquals(createdJumbfBox, parsedList.get(0));
        CoreUtils.deleteFile(targetUrl);
    }

    @Test
    void generateAndParseJpegSnackEmbeddedInFile() throws Exception {
        String targetUrl = CoreUtils.getFullPath(CoreUtils.getTempDir(), "jpegsnack_test.jpeg");

        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();
        JumbfBox createdJumbfBox = prepareJpegSnackJumbfBox();

        jpegCodestreamGenerator.generateJumbfMetadataToFile(List.of(createdJumbfBox), assetFileUrl, targetUrl);

        List<JumbfBox> parsedList = jpegCodestreamParser.parseMetadataFromFile(targetUrl);
        assertEquals(createdJumbfBox, parsedList.get(0));
        CoreUtils.deleteFile(targetUrl);
    }

    private JumbfBox prepareJpegSnackJumbfBox() throws MipamsException {
        JumbfBoxBuilder jpegSnack = new JumbfBoxBuilder(new JpegSnackContentType());

        JpegSnackDescriptionBox jsdb = new JpegSnackDescriptionBox();
        jsdb.setVersion(1);
        jsdb.setStartTime(2000);

        Composition composition = new Composition();

        composition.setNoOfObjects(1);
        composition.getObjectIds().add(1);
        jsdb.getCompositions().add(composition);

        InstructionSetBox issb = new InstructionSetBox();
        issb.setRepetition(1);
        issb.setTick(1000);
        InstructionParameter parameter = new InstructionParameter();
        parameter.setHorizontalOffset(0);
        parameter.setVerticalOffset(0);
        parameter.setPersist(false);
        parameter.setLife(12);
        parameter.setNextUse(0);
        parameter.setWidth(2);
        parameter.setHeight(2);
        issb.getInstructionParameters().add(parameter);

        ObjectMetadataBox obmb = new ObjectMetadataBox();
        obmb.setToggle(1);
        obmb.setId(1);
        obmb.setMediaType("image/jpg");
        obmb.setNoOfMedia(2);
        obmb.getLocations().add("self#jumbf=Object1");
        obmb.getLocations().add("self#jumbf=Object2");

        jpegSnack.appendContentBox(jsdb);
        jpegSnack.appendContentBox(issb);
        jpegSnack.appendContentBox(obmb);

        return jpegSnack.getResult();
    }
}
