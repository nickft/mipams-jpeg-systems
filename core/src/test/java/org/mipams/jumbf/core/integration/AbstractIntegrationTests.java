package org.mipams.jumbf.core.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class AbstractIntegrationTests {

    @Autowired
    protected MockMvc mockMvc;

    protected abstract String getRequestBody();

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
