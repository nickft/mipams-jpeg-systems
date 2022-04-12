package org.mipams.jumbf.core.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CoreParserTests {

    CoreParserService coreParserService;

    @BeforeEach
    void setup() {

        Properties properies = new Properties();
        ReflectionTestUtils.setField(properies, "IMAGE_FOLDER", "/tmp");

        coreParserService = new CoreParserService();
        ReflectionTestUtils.setField(coreParserService, "properties", properies);
    }

    @Test
    void testParseMetadataFromNonExistentFile() throws Exception {
        String nonExistentFileName = "test32.jumbf";
        assertThrows(MipamsException.class, () -> {
            coreParserService.parseMetadataFromJumbfFile(nonExistentFileName);
        });
    }
}
