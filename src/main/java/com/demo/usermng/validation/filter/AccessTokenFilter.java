package com.demo.usermng.validation.filter;

import com.demo.usermng.validation.token.AccessToken;
import com.demo.usermng.validation.exception.InvalidTokenException;
import com.demo.usermng.validation.jwt.JwtAuthentication;
import com.demo.usermng.validation.jwt.JwtTokenValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;


/**
 * This filter will examine the Authorization header and validate if the token is in
 * a proper format
 */
@Log4j2
public
class AccessTokenFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtTokenValidator tokenVerifier;

    public AccessTokenFilter(
            JwtTokenValidator jwtTokenValidator,
            AuthenticationManager authenticationManager,
            AuthenticationFailureHandler authenticationFailureHandler) {

        super(AnyRequestMatcher.INSTANCE);

        setAuthenticationManager(authenticationManager);
        setAuthenticationFailureHandler(authenticationFailureHandler);
        this.tokenVerifier = jwtTokenValidator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {

        log.info("Attempting to authenticate for a request {}", request.getRequestURI());

        String authorizationHeader = extractAuthorizationHeaderAsString(request);
        AccessToken accessToken = tokenVerifier.validateAuthorizationHeader(authorizationHeader);
        return this.getAuthenticationManager()
                .authenticate(new JwtAuthentication(accessToken));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        log.info("Successfully authentication for the request {}", request.getRequestURI());

        if (authResult instanceof JwtAuthentication) {
            JwtAuthentication jwtAuthentication = (JwtAuthentication) authResult;
            request.setAttribute("realmId", jwtAuthentication.getRealmId());
            log.info("AccessTokenFilter - RealmId {}", jwtAuthentication.getRealmId());
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    private String extractAuthorizationHeaderAsString(HttpServletRequest request) {
        try {
            return request.getHeader("Authorization");
        } catch (Exception ex) {
            throw new InvalidTokenException("There is no Authorization header in a request", ex);
        }
    }
}