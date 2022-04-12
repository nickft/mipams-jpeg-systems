package org.mipams.jumbf.core.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CoreGeneratorTests {

    CoreGeneratorService coreGeneratorService;

    @BeforeEach
    void setup() {

        Properties properies = new Properties();
        ReflectionTestUtils.setField(properies, "IMAGE_FOLDER", "/tmp");

        coreGeneratorService = new CoreGeneratorService();
        ReflectionTestUtils.setField(coreGeneratorService, "properties", properies);
    }

    @Test
    void testParseMetadataFromNonExistentFile() throws Exception {
        String nonExistentFileName = "/tmp/test32.jumbf";

        File file = new File(nonExistentFileName);
        try {
            file.mkdir();

            assertThrows(MipamsException.class, () -> {
                coreGeneratorService.generateJumbfFileFromBox(null, nonExistentFileName);
            });
        } finally {
            file.delete();
        }
    }
}
