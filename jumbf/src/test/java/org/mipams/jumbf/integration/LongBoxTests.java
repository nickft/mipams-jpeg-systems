package org.mipams.jumbf.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mipams.jumbf.config.JumbfConfig;
import org.mipams.jumbf.entities.JsonBox;
import org.mipams.jumbf.entities.JumbfBox;
import org.mipams.jumbf.entities.JumbfBoxBuilder;
import org.mipams.jumbf.services.content_types.JsonContentType;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JumbfConfig.class)
@ActiveProfiles("test")
public class LongBoxTests extends AbstractIntegrationTests {

    @Test
    void testLongBox() throws MipamsException {
        final String jumbfPath = CoreUtils.getFullPath(CoreUtils.getTempDir(), "test.jumbf");

        JumbfBox givenJumbfBox = generateMockLongBox();
        String test = testGenerateJumbfFileFromBox(List.of(givenJumbfBox), jumbfPath);

        assertTrue(test != null);
    }

    JumbfBox generateMockLongBox() throws MipamsException {

        JsonContentType jsonContentType = new JsonContentType();

        JsonBox jsonBox = new JsonBox();
        jsonBox.setContent(TEST_CONTENT.getBytes());

        JumbfBoxBuilder builder = new JumbfBoxBuilder(jsonContentType);
        builder.setId(12345);
        builder.setPaddingSize(10);
        builder.appendContentBox(jsonBox);

        JumbfBox givenJumbfBox = builder.getResult();

        givenJumbfBox.setXlBox(Long.valueOf(givenJumbfBox.getLBox() + CoreUtils.LONG_BYTE_SIZE));
        givenJumbfBox.setLBox(1);
        return givenJumbfBox;
    }

}