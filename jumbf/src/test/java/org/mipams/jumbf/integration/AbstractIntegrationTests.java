package org.mipams.jumbf.integration;

import java.util.List;

import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.services.CoreGeneratorService;
import org.mipams.jumbf.services.CoreParserService;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractIntegrationTests {

    protected final static String TEST_CONTENT = "Hello world";

    @Autowired
    protected CoreGeneratorService coreGeneratorService;

    @Autowired
    protected CoreParserService coreParserService;

    protected List<JumbfBox> generateJumbfFileAndParseBox(List<JumbfBox> givenJumbfBoxList) throws MipamsException {
        final String jumbfPath = CoreUtils.getFullPath(CoreUtils.getTempDir(), "test.jumbf");

        testGenerateJumbfFileFromBox(givenJumbfBoxList, jumbfPath);
        return testParseMetadataFromJumbfFile(jumbfPath);
    }

    protected String testGenerateJumbfFileFromBox(List<JumbfBox> givenJumbfBoxList, String assetUrl)
            throws MipamsException {
        coreGeneratorService.generateJumbfMetadataToFile(givenJumbfBoxList, assetUrl);
        return assetUrl;
    }

    protected List<JumbfBox> testParseMetadataFromJumbfFile(String assetUrl) throws MipamsException {
        return coreParserService.parseMetadataFromFile(assetUrl);
    }

}
