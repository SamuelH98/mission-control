package com.missioncontrol.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsSeededManagers() throws Exception {
        mockMvc.perform(get("/api/managers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email", containsString("nasa.gov")));
    }

    @Test
    void getsManager() throws Exception {
        mockMvc.perform(get("/api/managers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Gene"))
                .andExpect(jsonPath("$.lastName").value("Kranz"));
    }

    @Test
    void getMissingManagerReturns404() throws Exception {
        mockMvc.perform(get("/api/managers/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsManager() throws Exception {
        String body = """
                {"firstName":"Buzz","lastName":"Aldrin","email":"buzz.aldrin@nasa.gov"}
                """;
        mockMvc.perform(post("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Buzz"));
    }

    @Test
    void rejectsInvalidEmail() throws Exception {
        String body = """
                {"firstName":"Buzz","lastName":"Aldrin","email":"not-an-email"}
                """;
        mockMvc.perform(post("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        String body = """
                {"firstName":"Clone","lastName":"Kranz","email":"gene.kranz@nasa.gov"}
                """;
        mockMvc.perform(post("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void updatesManager() throws Exception {
        String body = """
                {"firstName":"Gene","lastName":"Kranz","email":"gkranz@flight-control.gov"}
                """;
        mockMvc.perform(put("/api/managers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("gkranz@flight-control.gov"));
    }

    @Test
    void deletesUnreferencedManager() throws Exception {
        String body = """
                {"firstName":"Temp","lastName":"Person","email":"temp.person@nasa.gov"}
                """;
        String location = mockMvc.perform(post("/api/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound());
    }

    @Test
    void cannotDeleteManagerAssignedToProject() throws Exception {
        mockMvc.perform(delete("/api/managers/1"))
                .andExpect(status().isConflict());
    }
}
