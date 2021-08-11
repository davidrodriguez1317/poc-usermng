package com.demo.usermng.repository.keycloakserver;

import com.demo.usermng.exception.UserHandlingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@AllArgsConstructor
@Service
public class KeycloakService {

    private Keycloak keycloak;

    public String getUserIdByRealmAndUserName(String realmId, String userName) {

        log.info(String.format("KeycloakService - Getting from keycloak user for realm= %s and username= %s", realmId, userName));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<UserRepresentation> allUsers = keycloak.realm(realmId)
                .users()
                .list();

        log.info("Users size= " + allUsers.size());

        allUsers.stream()
                .forEach(
                        u -> log.info("Users in keycloak= " + u.getUsername())
                );


        List<UserRepresentation> users = keycloak.realm(realmId)
                .users()
                .search(userName);

        log.info("FILTERED Users got from keycloak= " + users);

        if (users == null || users.isEmpty()) {
            throw new UserHandlingException(
                    String.format("No user found for for realm= %s and username= %s", realmId, userName));
        } else if (users.size() > 1) {
            throw new UserHandlingException(
                    String.format("More than one userId found in Keycloak for for realm= %s and username= %s", realmId, userName));
        }

        return users.get(0).getId();
    }

    public List<UserRepresentation> getAllUsersOfRealmFromKeycloak(String realmId) {
        return keycloak.realm(realmId)
                .users()
                .list();
    }
}
