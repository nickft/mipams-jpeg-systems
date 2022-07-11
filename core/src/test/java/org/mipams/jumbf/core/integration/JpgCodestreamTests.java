package org.mipams.jumbf.core.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.entities.JsonBox;
import org.mipams.jumbf.core.entities.JumbfBox;
import org.mipams.jumbf.core.entities.JumbfBoxBuilder;
import org.mipams.jumbf.core.entities.XmlBox;
import org.mipams.jumbf.core.services.JpegCodestreamGenerator;
import org.mipams.jumbf.core.services.JpegCodestreamParser;
import org.mipams.jumbf.core.services.content_types.JsonContentType;
import org.mipams.jumbf.core.services.content_types.XmlContentType;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class JpgCodestreamTests extends AbstractIntegrationTests {

    @Autowired
    JpegCodestreamGenerator jpegCodestreamGenerator;

    @Autowired
    JpegCodestreamParser jpegCodestreamParser;

    @Autowired
    Properties properties;

    @BeforeAll
    static void initRequest() throws IOException {
        generateFile();
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        fileCleanUp();
    }

    @Test
    void verifyParsing() throws MipamsException, FileNotFoundException {

        List<JumbfBox> jumbfBoxList = List.of(createJsonJumbfFile(10));
        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();

        String resultUrl = jpegCodestreamGenerator.generateJumbfMetadataToFile(jumbfBoxList, assetFileUrl);
        resultUrl = jpegCodestreamGenerator.generateJumbfMetadataToFile(jumbfBoxList, resultUrl);

        List<JumbfBox> resultList = jpegCodestreamParser.parseMetadataFromFile(resultUrl);

        assertEquals(2, resultList.size());
    }

    @Test
    void generateLargeJumbfBoxToAsset() throws MipamsException, FileNotFoundException {
        List<JumbfBox> jumbfBoxList = List.of(createJsonJumbfFile(0xFFFF));

        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();
        String resultUrl = jpegCodestreamGenerator.generateJumbfMetadataToFile(jumbfBoxList, assetFileUrl);
        resultUrl = jpegCodestreamGenerator.generateJumbfMetadataToFile(jumbfBoxList, resultUrl);

        List<JumbfBox> resultList = jpegCodestreamParser.parseMetadataFromFile(resultUrl);

        assertEquals(2, resultList.size());
    }

    @Test
    void testStripJumbfFromFile() throws MipamsException, FileNotFoundException {
        List<JumbfBox> jumbfBoxList = List.of(createJsonJumbfFile(0), createXmlJumbfFile(0));

        String assetFileUrl = ResourceUtils.getFile("classpath:sample.jpeg").getAbsolutePath();
        String resultUrl = jpegCodestreamGenerator.generateJumbfMetadataToFile(jumbfBoxList, assetFileUrl);

        String strippedAsset = resultUrl + ".tmp";
        jpegCodestreamGenerator.stripJumbfMetadataWithUuidEqualTo(resultUrl, strippedAsset,
                (new JsonContentType()).getContentTypeUuid());

        List<JumbfBox> resultList = jpegCodestreamParser.parseMetadataFromFile(strippedAsset);
        assertEquals(1, resultList.size());
    }

    @Test
    void testCorruptedJpegImage() throws MipamsException, FileNotFoundException {
        List<JumbfBox> jumbfBoxList = List.of(createJsonJumbfFile(0));

        String assetFileUrl = TEST_FILE_PATH;
        assertThrows(MipamsException.class, () -> {
            jpegCodestreamGenerator.generateJumbfMetadataToFile(jumbfBoxList, assetFileUrl);
        });
    }

    private JumbfBox createXmlJumbfFile(int padding) throws MipamsException {

        XmlContentType xmlContentType = new XmlContentType();

        XmlBox xmlBox = new XmlBox();
        xmlBox.setContent(TEST_CONTENT.getBytes());
        xmlBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(xmlContentType);
        builder.setPaddingSize(10);
        builder.appendContentBox(xmlBox);

        JumbfBox givenJumbfBox = builder.getResult();
        return givenJumbfBox;
    }

    private JumbfBox createJsonJumbfFile(int padding) throws MipamsException {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());
        jsonBox.updateBmffHeadersBasedOnBox();

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();
        return givenJumbfBox;
    }
}
