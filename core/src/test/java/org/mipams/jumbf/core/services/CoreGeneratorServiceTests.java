package org.mipams.jumbf.core.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.core.services.boxes.CoreGeneratorService;
import org.mipams.jumbf.core.util.MipamsException;
import org.mipams.jumbf.core.util.Properties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class CoreGeneratorServiceTests {

    protected static String TEST_DIRECTORY = "/tmp/jumbf-tests/";

    Properties properties;

    @BeforeAll
    static void generateFile() throws IOException {
        File file = new File(TEST_DIRECTORY);
        if (file.exists()) {
            return;
        }

        file.mkdir();
    }

    @AfterAll
    static void fileCleanUp() throws IOException {

        File dir = new File(TEST_DIRECTORY);
        if (!dir.exists()) {
            return;
        }

        dir.delete();
    }

    @Test
    void testGenerateJumbfFileFromBoxWithDirInsteadOfFile() {

        properties = new Properties();
        ReflectionTestUtils.setField(properties, "IMAGE_FOLDER", "\\tmp");

        CoreGeneratorService coreGeneratorService = new CoreGeneratorService();
        ReflectionTestUtils.setField(coreGeneratorService, "properties", properties);

        assertThrows(MipamsException.class, () -> {
            coreGeneratorService.generateJumbfFileFromBox(null, TEST_DIRECTORY);
        });

    }
}
