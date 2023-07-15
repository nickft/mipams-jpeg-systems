package org.mipams.jumbf.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.util.MipamsException;

public class CoreGeneratorServiceTests {

    protected static String TEST_DIRECTORY = "/tmp/jumbf-tests/";

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
        CoreGeneratorService coreGeneratorService = new CoreGeneratorService();

        assertThrows(MipamsException.class, () -> {
            coreGeneratorService.generateJumbfMetadataToFile(List.of(), TEST_DIRECTORY);
        });
    }
}
