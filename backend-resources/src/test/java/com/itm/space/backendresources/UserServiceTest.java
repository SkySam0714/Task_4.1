package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import com.itm.space.backendresources.service.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assumptions.assumeThat;


public class UserServiceTest extends BaseIntegrationTest{

    String testUserName = "test_test";

    @Autowired
    private UserServiceImpl userService;

    @AfterEach
    void deleteTestUser(){
        List<UserRepresentation> testUserCreated = keycloak.realm("ITM").users().search(testUserName);
        if (testUserCreated.size() > 0) {
            keycloak.realm("ITM").users().delete(testUserCreated.get(0).getId());
        }
    }

    @Test
    void testCreateAndGetUserById(){
        UserRequest testUserExpected = new UserRequest(testUserName, "testtest@test.com", "test", "test1", "test2");

        userService.createUser(testUserExpected);

        UserRepresentation testUserCreated = keycloak.realm("ITM").users().search(testUserName).get(0);

        assumeThat(testUserCreated.getUsername()).isEqualTo(testUserExpected.getUsername());
        assumeThat(testUserCreated.getEmail()).isEqualTo(testUserExpected.getEmail());
        assumeThat(testUserCreated.getFirstName()).isEqualTo(testUserExpected.getFirstName());
        assumeThat(testUserCreated.getLastName()).isEqualTo(testUserExpected.getLastName());

        UserResponse testUserReceived = userService.getUserById(UUID.fromString(testUserCreated.getId()));

        assumeThat(testUserReceived.getFirstName()).isEqualTo(testUserExpected.getFirstName());
        assumeThat(testUserReceived.getLastName()).isEqualTo(testUserExpected.getLastName());
        assumeThat(testUserReceived.getEmail()).isEqualTo(testUserExpected.getEmail());
    }
}
