package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AbstractIntegrationTests {

    @Autowired
    protected MockMvc mockMvc;

    protected abstract String getRequestBody();

    protected static String TEST_FILE_PATH = "/tmp/test.jpeg";
    protected static String JUMBF_FILE_PATH = "/tmp/test.jumbf";

    static void generateFile() throws IOException {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(TEST_FILE_PATH)) {
            fos.write("Hello world".getBytes());
        }
    }

    static void fileCleanUp(String fileName) throws IOException {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void generateBoxRequest() throws Exception {
        mockMvc.perform(post("/core/v1/generateBox")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getRequestBody()))
                .andExpect(status().isOk());
    }

    @Test
    void generateBoxRequestInSpecificFile() throws Exception {
        mockMvc.perform(post("/core/v1/generateBox?targetFile=test.jumbf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getRequestBody())).andExpect(status().isOk());
    }

    @Test
    void parseBoxFromMetadata() throws Exception {
        mockMvc.perform(get("/core/v1/parseMetadata?fileName=test.jumbf")).andExpect(status().isOk());
    }

}
