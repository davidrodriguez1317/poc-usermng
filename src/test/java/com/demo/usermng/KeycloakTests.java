package com.demo.usermng;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@SpringBootTest
class KeycloakTests {


    @Autowired
    private Keycloak keycloak;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "Bearer";

    private final HttpClient client =
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    //@Test
    public void getUserIdWithAdminClient() {

        List<UserRepresentation> users = keycloak.realm("platform-tenant01-realm")
                .users()
                .search("user13");

        Assert.notNull(users, "Users object is null");
        Assert.notEmpty(users, "No users in the list");

        log.info("Users= " + users);
        log.info("UserId= " + users.get(0).getId());

    }

    //@Test
    public void getUserIdByUserNameAndRealmId() throws ExecutionException, InterruptedException {
        String userName = "?username=testUser01";
        String endpoint = "http://localhost:8080/auth/platform-tenant01-realm/users";//.concat(userName);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .header(
                                AUTHORIZATION_HEADER,
                                String.format("%s %s", TOKEN_TYPE, getAdminTokenAsString()))
                        .header("Content-Type", "application/json")
                        .setHeader("User-Agent", "Java 11 HttpClient Bot")
                        .GET()
                        .uri(URI.create(endpoint))
                        .build();

        CompletableFuture<HttpResponse<String>> response =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result = response.thenApply(HttpResponse::body).get();

        log.info("Result= " + result);

        Type userListType = new TypeToken<ArrayList<UserRepresentation>>() {
        }.getType();

        final List<UserRepresentation> users = new Gson().fromJson(result, userListType);
        Assert.notNull(users, "Users object is null");
        Assert.notEmpty(users, "No users in the list");

        log.info("Users= " + users);
        log.info("UserId= " + users.get(0).getId());

    }


    private String getAdminTokenAsString() {
        return keycloak.tokenManager()
                .getAccessTokenString();
    }


}
