package edu.tcu.cs.hogwartsartifactsonline.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Artifact API endpoints")
@Tag("integration")
class ArtifactControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    String token;

    @Value("${api.endpoint.base-url}")
    String baseUrl;


    @BeforeEach
    void setUp() throws Exception {
//        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + "/auth/login").header(HttpHeaders.AUTHORIZATION,
//                "Basic " + Base64Utils.encodeToString("john:123456".getBytes())));
        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login").with(httpBasic("john", "123456"))); // httpBasic() is from spring-security-test.
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        this.token = "Bearer " + json.getJSONObject("data").getString("token"); // Don't forget to add "Bearer " as prefix.
    }


    @Test
    @DisplayName("Check findArtifactById with non-existent id (GET)")
    void testFindArtifactByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1250808601744904199").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @Test
    @DisplayName("Check updateArtifact with valid input (PUT)")
    void testUpdateArtifactSuccess() throws Exception {
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Updated artifact name");
        a.setDescription("Updated description");
        a.setImageUrl("Updated imageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(put(this.baseUrl + "/artifacts/1250808601744904192").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data.name").value("Updated artifact name"))
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.imageUrl").value("Updated imageUrl"));
    }

    @Test
    @DisplayName("Check updateArtifact with non-existent id (PUT)")
    void testUpdateArtifactErrorWithNonExistentId() throws Exception {
        Artifact a = new Artifact();
        a.setId("1250808601744904199"); // This id does not exist in the database.
        a.setName("Updated artifact name");
        a.setDescription("Updated description");
        a.setImageUrl("Updated imageUrl");

        String json = this.objectMapper.writeValueAsString(a);

        this.mockMvc.perform(put(this.baseUrl + "/artifacts/1250808601744904199").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @Test
    @DisplayName("Check deleteArtifact with valid input (DELETE)")
    void testDeleteArtifactSuccess() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"));
        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1250808601744904191").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904191 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check deleteArtifact with non-existent id (DELETE)")
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/artifacts/1250808601744904199").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1250808601744904199 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}