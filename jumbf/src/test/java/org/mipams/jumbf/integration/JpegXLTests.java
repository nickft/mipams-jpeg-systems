package org.mipams.jumbf.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.services.JpegXLGenerator;
import org.mipams.jumbf.services.JpegXLParser;
import org.mipams.jumbf.services.content_types.JsonContentType;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JumbfConfig.class)
@ActiveProfiles("test")
public class JpegXLTests extends AbstractIntegrationTests {

    @Autowired
    JpegXLParser jXlParser;

    @Autowired
    JpegXLGenerator jxlGenerator;

    @Test
    void verifyJxlParsing() throws MipamsException, FileNotFoundException {
        List<JumbfBox> jumbfBoxList = List.of(createJsonBox(0));
        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jxl").getAbsolutePath();

        String targetUrl = assetFileUrl + "-new";

        jxlGenerator.generateJumbfMetadataToFile(jumbfBoxList, assetFileUrl, targetUrl);

        List<JumbfBox> resultList = jXlParser.parseMetadataFromFile(targetUrl);

        assertEquals(1, resultList.size());
    }

    @Test
    void verifyJxlParsingWithLbox0() throws MipamsException, FileNotFoundException {
        JumbfBox jumbfBox = createJsonBox(0);
        jumbfBox.setLBox(0);
        List<JumbfBox> jumbfBoxList = List.of(jumbfBox);
        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jxl").getAbsolutePath();

        String targetUrl = assetFileUrl + "-new";

        jxlGenerator.generateJumbfMetadataToFile(jumbfBoxList, assetFileUrl, targetUrl);

        List<JumbfBox> resultList = jXlParser.parseMetadataFromFile(targetUrl);

        assertEquals(1, resultList.size());
    }

    @Test
    void verifyJxlParsingWithLbox1() throws MipamsException, FileNotFoundException {
        JumbfBox jumbfBox = createJsonBox(0);
        jumbfBox.setXlBox(Integer.toUnsignedLong(jumbfBox.getLBox()) + 8);
        jumbfBox.setLBox(1);
        List<JumbfBox> jumbfBoxList = List.of(jumbfBox);

        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jxl").getAbsolutePath();

        String targetUrl = assetFileUrl + "-new";

        jxlGenerator.generateJumbfMetadataToFile(jumbfBoxList, assetFileUrl, targetUrl);

        List<JumbfBox> resultList = jXlParser.parseMetadataFromFile(targetUrl);

        assertEquals(1, resultList.size());
    }


    private JumbfBox createJsonBox(int padding) throws MipamsException, FileNotFoundException {
        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.setLabel("Test label");
        builder.setJumbfBoxAsRequestable();
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        return builder.getResult();
    }
}
