package com.demo.usermng.validation.provider;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;

public interface JwkProviderCustom extends JwkProvider {
    Jwk get(String var1, String realm) throws JwkException;
}
