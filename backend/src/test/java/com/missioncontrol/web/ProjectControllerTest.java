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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsSeededProjects() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title").value("Artemis III Landing"))
                .andExpect(jsonPath("$[0].status").value("planned"))
                .andExpect(jsonPath("$[0].managerName", containsString("Hamilton")));
    }

    @Test
    void getsSingleProject() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Artemis III Landing"));
    }

    @Test
    void getMissingProjectReturns404() throws Exception {
        mockMvc.perform(get("/api/projects/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createsProject() throws Exception {
        String body = """
                {"title":"Mars Sample Return","description":"Collect samples at Jezero.","status":"planned","managerId":1}
                """;
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Mars Sample Return"))
                .andExpect(jsonPath("$.status").value("planned"))
                .andExpect(jsonPath("$.managerId").value(1))
                .andExpect(jsonPath("$.managerName", containsString("Kranz")));
    }

    @Test
    void rejectsInvalidStatus() throws Exception {
        String body = """
                {"title":"Bad Mission","description":"","status":"on-hold","managerId":1}
                """;
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsBlankTitle() throws Exception {
        String body = """
                {"title":"   ","description":"","status":"active","managerId":1}
                """;
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }

    @Test
    void rejectsTitleLongerThan50Chars() throws Exception {
        String title = "X".repeat(51);
        String body = """
                {"title":"%s","description":"","status":"active","managerId":1}
                """.formatted(title);
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }

    @Test
    void rejectsUnknownManager() throws Exception {
        String body = """
                {"title":"Lost","description":"","status":"active","managerId":99999}
                """;
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatesProject() throws Exception {
        String body = """
                {"title":"Europa Clipper - Extended","description":"More flybys.","status":"active","managerId":2}
                """;
        mockMvc.perform(put("/api/projects/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Europa Clipper - Extended"))
                .andExpect(jsonPath("$.description").value("More flybys."));
    }

    @Test
    void updatesMissingProjectReturns404() throws Exception {
        String body = """
                {"title":"Nope","description":"","status":"active","managerId":1}
                """;
        mockMvc.perform(put("/api/projects/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletesProject() throws Exception {
        String body = """
                {"title":"Expendable Mission","description":"Will be deleted","status":"planned","managerId":3}
                """;
        String location = mockMvc.perform(post("/api/projects")
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
    void createsProjectWithEmptyDescription() throws Exception {
        String body = """
                {"title":"No Description","description":null,"status":"active","managerId":1}
                """;
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(""));
    }
}
