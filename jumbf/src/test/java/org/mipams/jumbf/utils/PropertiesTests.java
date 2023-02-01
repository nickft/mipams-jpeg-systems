package org.mipams.jumbf.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mipams.jumbf.util.CoreUtils;
import org.mipams.jumbf.util.MipamsException;
import org.mipams.jumbf.util.Properties;
import org.springframework.test.util.ReflectionTestUtils;

public class PropertiesTests {
    @BeforeAll
    static void initUseCase() throws IOException {

        File file = new File(getTestFilePath());
        if (file.exists()) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(getTestFilePath())) {
            fos.write("Hello world".getBytes());
        }
    }

    @AfterAll
    static void cleanUp() throws IOException {

        File file = new File(getTestFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void exceedFileSize() throws IOException {
        Properties properties = new Properties();
        ReflectionTestUtils.setField(properties, "MAX_FILE_SIZE", 1);

        Exception exception = assertThrows(MipamsException.class, () -> {
            properties.checkIfFileSizeExceedApplicationLimits(getTestFilePath());
        });

        assertEquals("File is too large for the application. Check the available limits.", exception.getMessage());
    }

    static String getTestFilePath() {
        return CoreUtils.getFullPath("/tmp", "test");
    }
}