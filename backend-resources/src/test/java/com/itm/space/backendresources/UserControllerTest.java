package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin", roles = "MODERATOR")
public class UserControllerTest extends BaseIntegrationTest{

    String testUserName = "test_test";

    @Test
    void testHelloController() throws Exception {
        System.out.println(mvc.perform(get( "http://backend-gateway-client:9090/api/users/hello"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

    }

    @AfterEach
    void deleteTestUser(){
        List<UserRepresentation> testUserCreated = keycloak.realm("ITM").users().search(testUserName);
        if (testUserCreated.size() > 0) {
            keycloak.realm("ITM").users().delete(testUserCreated.get(0).getId());
        }
    }

    @Test
    void testCreateAndGetUserById() throws Exception{
        UserRequest testUserExpected = new UserRequest(testUserName, "testtest@test.com", "test", "test1", "test2");

        mvc.perform(post("http://backend-gateway-client:9090/api/users").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"username\": \"" + testUserExpected.getUsername()+"\",\n" +
                "  \"email\": \"" + testUserExpected.getEmail()+"\",\n" +
                "  \"password\": \"" + testUserExpected.getPassword()+"\",\n" +
                "  \"firstName\": \"" + testUserExpected.getFirstName()+"\",\n" +
                "  \"lastName\": \"" + testUserExpected.getLastName()+"\"\n" +
                "}")).andExpect(status().isOk());

        UserRepresentation testUserCreated = keycloak.realm("ITM").users().search(testUserName).get(0);

        assumeThat(testUserCreated.getUsername()).isEqualTo(testUserExpected.getUsername());
        assumeThat(testUserCreated.getEmail()).isEqualTo(testUserExpected.getEmail());
        assumeThat(testUserCreated.getFirstName()).isEqualTo(testUserExpected.getFirstName());
        assumeThat(testUserCreated.getLastName()).isEqualTo(testUserExpected.getLastName());


        mvc.perform(get( "http://backend-gateway-client:9090/api/users/"+testUserCreated.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUserExpected.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUserExpected.getLastName()))
                .andExpect(jsonPath("$.email").value(testUserExpected.getEmail()));

    }

}
