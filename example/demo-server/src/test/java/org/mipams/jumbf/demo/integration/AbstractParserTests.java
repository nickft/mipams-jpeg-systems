package org.mipams.jumbf.demo.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AbstractParserTests {

    @Autowired
    protected MockMvc mockMvc;

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

    protected void testGenerateBoxEndpoint(String requestBody) throws Exception {
        mockMvc.perform(post("/demo/generateBox")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    protected void testParseBoxFromFile(String fileName) throws Exception {
        mockMvc.perform(get("/demo/parseMetadata?fileName=" + fileName)).andExpect(status().isOk());
    }

}