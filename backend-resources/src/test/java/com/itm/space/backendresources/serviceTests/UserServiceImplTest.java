package com.itm.space.backendresources.serviceTests;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.mapper.UserMapper;
import com.itm.space.backendresources.service.UserService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceImplTest extends BaseIntegrationTest {

    @MockBean
    Keycloak keycloakClientTest;

    @MockBean
    RealmResource realmResourceTest;

    @MockBean
    UsersResource usersResourceTest;

    @MockBean
    Response responseTest;

    @Autowired
    private UserService userServiceTest;

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
    void createUserTest() throws URISyntaxException {
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

        when(usersResourceTest.create(any()))
                .thenReturn(responseTest);
        userServiceTest.createUser(userRequestTest);

        verify(usersResourceTest, times(1)).create(any());
    }

    @Test
    void getUserByIdTest() {
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

        UserResponse userResponseTest = userServiceTest.getUserById(randomUUID);

        assertEquals("TestFirstName", userResponseTest.getFirstName());
        assertEquals("TestLastName", userResponseTest.getLastName());
        assertEquals("test@gmail.com", userResponseTest.getEmail());
    }
}
