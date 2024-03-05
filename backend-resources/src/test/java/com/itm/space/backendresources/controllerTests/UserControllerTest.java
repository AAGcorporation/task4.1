package com.itm.space.backendresources.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Тестирование post метода create контроллера UserController - вернет 200")
    @WithMockUser(username = "Admin", password = "2014", authorities = "ROLE_MODERATOR")
    void createShouldReturnStatus200() throws Exception {

        UserRequest userRequest = new UserRequest("Nagibator", "navalniy@gmail.com",
                "1488", "Aleksei", "Navalniy");

        String jsonRequest = objectMapper.writeValueAsString(userRequest);

        mvc.perform(post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тестирование get метода getUserById контроллера " +
            "UserController - вернет 200, если пользователь существует")
    @WithMockUser(username = "Admin", password = "2014", authorities = "ROLE_MODERATOR")
    void getUserByIdShouldReturnStatus200() throws Exception {
        UUID userId = UUID.fromString("892274cc-7fed-4639-aad8-862c89c4040f");

        mvc.perform(get("/api/users/{id}", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("navalniy@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Aleksei"))
                .andExpect(jsonPath("$.lastName").value("Navalniy"));
    }

    @Test
    @DisplayName("Тестирование get метода getUserById контроллера " +
            "UserController - вернет 500, при NotFoundException, пользователь не существует")
    @WithMockUser(username = "Admin", password = "2014", authorities = "ROLE_MODERATOR")
    void getUserByIdShouldReturnStatus500() throws Exception {
        UUID incorrectUUID = UUID.fromString("892274cc-7fed-4639-aad8-862c89c4440f");

        mvc.perform(get("/api/users/{id}", incorrectUUID.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Тестирование get метода hello контроллера " +
            "UserController - вернет 200 и Id пользователя")
    @WithMockUser(username = "Admin", password = "2014", authorities = "ROLE_MODERATOR")
    void helloByIdShouldReturnStatus200() throws Exception {

        mvc.perform(get("/api/users/hello")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin"));
    }
}