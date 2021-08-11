package com.demo.usermng.validation.provider;


import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.NetworkException;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * This class implements the way of getting the JWK from the Keycloak server in order
 * to validate the signature of the token
 * JWK - Json Web Key (Public Key)
 */
@Log4j2
public class KeycloakJwkProvider implements JwkProviderCustom {

    private final String jwkProviderGenericUrl;
    private final ObjectReader reader;

    public KeycloakJwkProvider(String jwkProviderGenericUrl) {

        this.jwkProviderGenericUrl = jwkProviderGenericUrl;
        log.info("keycloak jwkProviderUrl = " + this.jwkProviderGenericUrl);
        this.reader = new ObjectMapper().readerFor(Map.class);
    }

    @Override
    public Jwk get(String keyId) throws JwkException {
        return getJwkWithKeyId(keyId, jwkProviderGenericUrl);
    }

    //TODO First check with the stored keys in our app to avoid overload
    @Override
    public Jwk get(String keyId, String realmId) throws JwkException {

        String jwkProviderUrl = String.format(this.jwkProviderGenericUrl, realmId);
        log.info("Building uri for realm --> " + jwkProviderUrl);

        return getJwkWithKeyId(keyId, jwkProviderUrl);
    }

    /**
     * The kid identifies the key of a realm when the realm has more than one
     *
     * @param keyId
     * @param jwkProviderUrl
     * @return
     * @throws SigningKeyNotFoundException
     */
    private Jwk getJwkWithKeyId(String keyId, String jwkProviderUrl) throws SigningKeyNotFoundException {

        final List<Jwk> jwks = getAll(jwkProviderUrl);
        if (keyId == null && jwks.size() == 1) {
            return jwks.get(0);
        }
        if (keyId != null) {
            for (Jwk jwk : jwks) {
                if (keyId.equals(jwk.getId())) {
                    return jwk;
                }
            }
        }
        throw new SigningKeyNotFoundException("No key found in " + jwkProviderUrl + " with kid " + keyId, null);

    }


    private List<Jwk> getAll(String jwkProviderUrl) throws SigningKeyNotFoundException {
        List<Jwk> jwks = Lists.newArrayList();
        final List<Map<String, Object>> keys = (List<Map<String, Object>>) getJwks(jwkProviderUrl).get("keys");

        if (keys == null || keys.isEmpty()) {
            throw new SigningKeyNotFoundException("No keys found in " + jwkProviderUrl, null);
        }

        try {
            for (Map<String, Object> values : keys) {
                jwks.add(Jwk.fromValues(values));
            }
        } catch (IllegalArgumentException e) {
            throw new SigningKeyNotFoundException("Failed to parse jwk from json", e);
        }
        return jwks;
    }


    private Map<String, Object> getJwks(String jwkProviderUrl) throws SigningKeyNotFoundException {
        try {
            URI uri = new URI(jwkProviderUrl).normalize();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .headers("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return reader.readValue(response.body());

        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new NetworkException("Cannot obtain jwks from url " + jwkProviderUrl, e);
        }
    }
}