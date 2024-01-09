package com.itm.space.backendresources.controllerTests;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.keycloak.util.JsonSerialization.mapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithMockUser(username = "test_name", password = "test_pass", authorities = "ROLE_MODERATOR")
public class UserControllerTest extends BaseIntegrationTest {

    @MockBean
    Keycloak keycloakClientTest;

    @MockBean
    RealmResource realmResourceTest;

    @MockBean
    UsersResource usersResourceTest;

    @MockBean
    Response responseTest;

    @SpyBean
    UserMapper userMapperTest;
    @MockBean
    UserRepresentation userRepresentationTest;
    @MockBean
    List<RoleRepresentation> userRolesTest;
    @MockBean
    List<GroupRepresentation> userGroupsTest;
    @MockBean
    UserResource userResourceTest;
    @MockBean
    RoleMappingResource roleMappingResourceTest;
    @MockBean
    MappingsRepresentation mappingsRepresentationTest;

    @Test
    void createUserTest() throws Exception {

        when(keycloakClientTest.realm(anyString()))
                .thenReturn(realmResourceTest);
        when(realmResourceTest.users())
                .thenReturn(usersResourceTest);

        UserRequest userRequestTest = new UserRequest("TestUserName", "test@gmail.com",
                "tests", "TestFirstName", "TestLastName");

        Response responseTest = Response
                .status(Response.Status.CREATED)
                .location(new URI("user_id"))
                .build();

        when(keycloakClientTest.realm(anyString()).users().create(any()))
                .thenReturn(responseTest);

        mvc.perform(requestWithContent(post("/api/users"), userRequestTest));

        verify(usersResourceTest, times(1)).create(any());
    }

    @Test
    public void testGetUserById() {
        try {
            UUID randomUUID = UUID.randomUUID();
            UserRepresentation userRepresentationTest = new UserRepresentation();
            userRepresentationTest.setId(String.valueOf(randomUUID));
            userRepresentationTest.setFirstName("TestFirstName");
            userRepresentationTest.setLastName("TestLastName");
            userRepresentationTest.setEmail("test@gmail.com");

            when(keycloakClientTest.realm(anyString()))
                    .thenReturn(realmResourceTest);
            when(realmResourceTest.users())
                    .thenReturn(usersResourceTest);
            when(usersResourceTest.get(String.valueOf(randomUUID)))
                    .thenReturn(userResourceTest);
            when(userResourceTest.toRepresentation())
                    .thenReturn(userRepresentationTest);
            when(userResourceTest.roles())
                    .thenReturn(roleMappingResourceTest);
            when(roleMappingResourceTest.getAll())
                    .thenReturn(mappingsRepresentationTest);
            when(mappingsRepresentationTest.getRealmMappings())
                    .thenReturn(userRolesTest);
            when(userResourceTest.groups())
                    .thenReturn(userGroupsTest);

            MvcResult result = mvc.perform(get("/api/users/{id}", randomUUID))
                    .andExpect(status().isOk())
                    .andReturn();

            UserResponse userResponseTest = mapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

            assertEquals("TestFirstName", userResponseTest.getFirstName());
            assertEquals("TestLastName", userResponseTest.getLastName());
            assertEquals("test@gmail.com", userResponseTest.getEmail());

            verify(keycloakClientTest.realm(anyString()).users(), times(1)).get(any());
        } catch (Exception e) {
        }
    }


    @Test
    public void helloTest() throws Exception {
        MvcResult resultMvc = mvc.perform(get("/api/users/hello"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = resultMvc.getResponse().getContentAsString();
        assertEquals("test_name", responseContent);
    }
}