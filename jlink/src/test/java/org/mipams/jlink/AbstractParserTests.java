package org.mipams.jlink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AbstractParserTests {

    protected static String TEST_DIRECTORY = "/tmp/jumbf-tests/";
    protected static String TEST_FILE_NAME = "test.jpeg";
    protected static String TEST_FILE_PATH = TEST_DIRECTORY + TEST_FILE_NAME;
    protected static String JUMBF_FILE_NAME = "test.jumbf";

    public static void generateFile() throws IOException {
        File file = new File(TEST_DIRECTORY);
        if (file.exists()) {
            return;
        }

        file.mkdir();

        file = new File(TEST_DIRECTORY);

        try (FileOutputStream fos = new FileOutputStream(TEST_FILE_PATH)) {
            fos.write("Hello world".getBytes());
        }
    }

    public static void fileCleanUp() throws IOException {

        File dir = new File(TEST_DIRECTORY);
        if (!dir.exists()) {
            return;
        }

        File[] directoryListing = dir.listFiles();

        for (File file : directoryListing) {
            file.delete();
        }

        dir.delete();
    }

    protected static boolean fileWithPathExists(String filePath) {
        File f = new File(filePath);
        return f.exists();
    }

}