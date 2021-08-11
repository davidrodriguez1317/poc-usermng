package com.demo.usermng.validation.jwt;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.demo.usermng.utils.StringHelper;
import com.demo.usermng.validation.token.AccessToken;
import com.demo.usermng.validation.exception.InvalidTokenException;
import com.demo.usermng.validation.provider.JwkProviderCustom;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * Custom class that encapsulates the logic for validating a token
 */
@Log4j2
@RequiredArgsConstructor
public class JwtTokenValidator {
    private final JwkProviderCustom jwkProvider;

    public AccessToken validateAuthorizationHeader(String authorizationHeader) throws InvalidTokenException {
        String tokenValue = subStringBearer(authorizationHeader);

        DecodedJWT decodedJWT = decodeToken(tokenValue);
        String realmId = getRealmNameByToken(decodedJWT);
        validateToken(decodedJWT, realmId);
        return new AccessToken(tokenValue, realmId);
    }

    private void validateToken(DecodedJWT decodedJWT, String realmId) {

        verifyTokenHeader(decodedJWT);
        verifySignature(decodedJWT, realmId);
        verifyPayload(decodedJWT);
    }

    /**
     * This way for getting the realm is linked to Keycloak, so it will fail with other IDPs
     *
     * @param decodedJWT
     * @return
     */
    private String getRealmNameByToken(DecodedJWT decodedJWT) {

        String[] issuerParts = decodedJWT.getIssuer().split("/");

        Optional<Integer> indexOfRealmsString =
                StringHelper.getIndexOfStringInArrayIgnoringCase(issuerParts, "realms");

        if (indexOfRealmsString.isPresent() && indexOfRealmsString.get() < issuerParts.length) {
            return issuerParts[indexOfRealmsString.get() + 1];
        }

        throw new OAuth2AuthenticationException("Realm could not be got from JWT");
    }

    private DecodedJWT decodeToken(String value) {
        if (isNull(value)) {
            throw new InvalidTokenException("Token has not been provided");
        }
        DecodedJWT decodedJWT = JWT.decode(value);
        log.debug("Token decoded successfully");
        return decodedJWT;
    }

    private void verifyTokenHeader(DecodedJWT decodedJWT) {
        try {
            Preconditions.checkArgument(decodedJWT.getType().equals("JWT"));
            log.debug("Token's header is correct");
        } catch (IllegalArgumentException ex) {
            throw new InvalidTokenException("Token is not JWT type", ex);
        }
    }

    /**
     * It implies using RS256 as algorithm for the signature
     *
     * @param decodedJWT
     * @param realm
     */
    private void verifySignature(DecodedJWT decodedJWT, String realm) {
        try {
            Jwk jwk = jwkProvider.get(decodedJWT.getKeyId(), realm);
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(decodedJWT);
            log.debug("Token's signature is correct");
        } catch (JwkException | SignatureVerificationException ex) {
            throw new InvalidTokenException("Token has invalid signature", ex);
        }
    }

    private void verifyPayload(DecodedJWT decodedJWT) {
        JsonObject payloadAsJson = decodeTokenPayloadToJsonObject(decodedJWT);
        if (hasTokenExpired(payloadAsJson)) {
            throw new InvalidTokenException("Token has expired");
        }
        log.debug("Token has not expired");

        if (!hasTokenRealmRolesClaim(payloadAsJson)) {
            throw new InvalidTokenException("Token doesn't contain claims with realm roles");
        }
        log.debug("Token's payload contain claims with realm roles");

        if (!hasTokenScopeInfo(payloadAsJson)) {
            throw new InvalidTokenException("Token doesn't contain scope information");
        }
        log.debug("Token's payload contain scope information");
    }

    private JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
        try {
            String payloadAsString = decodedJWT.getPayload();
            return new Gson().fromJson(
                    new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
                    JsonObject.class);
        } catch (RuntimeException exception) {
            throw new InvalidTokenException("Invalid JWT or JSON format of each of the jwt parts", exception);
        }
    }

    private boolean hasTokenExpired(JsonObject payloadAsJson) {
        Instant expirationDatetime = extractExpirationDate(payloadAsJson);
        return Instant.now().isAfter(expirationDatetime);
    }

    private Instant extractExpirationDate(JsonObject payloadAsJson) {
        try {
            return Instant.ofEpochSecond(payloadAsJson.get("exp").getAsLong());
        } catch (NullPointerException ex) {
            throw new InvalidTokenException("There is no 'exp' claim in the token payload");
        }
    }

    private boolean hasTokenRealmRolesClaim(JsonObject payloadAsJson) {
        try {
            return payloadAsJson.getAsJsonObject("realm_access").getAsJsonArray("roles").size() > 0;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    private boolean hasTokenScopeInfo(JsonObject payloadAsJson) {
        return payloadAsJson.has("scope");
    }

    private String subStringBearer(String authorizationHeader) {
        try {
            return authorizationHeader.substring(AccessToken.BEARER.length());
        } catch (Exception ex) {
            throw new InvalidTokenException("There is no AccessToken in a request header");
        }
    }
}
