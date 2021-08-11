package com.demo.usermng.validation.config;

import com.demo.usermng.validation.provider.KeycloakAuthenticationProvider;
import com.demo.usermng.validation.provider.KeycloakJwkProvider;
import com.demo.usermng.validation.filter.AccessTokenFilter;
import com.demo.usermng.validation.provider.JwkProviderCustom;
import com.demo.usermng.validation.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Log4j2
@Order(1)
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.ignored}")
    private String[] nonSecureUrls;

    @Value("${usermng.keycloak.jwk}")
    private String jwkProviderUrl;

    /**
     * - SessionCreationPolicy.STATELESS : user info is not stored in memory between requests, preventing
     * Spring from creating HTTP sessions
     * -
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .cors()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .addFilterBefore(
                        new AccessTokenFilter(
                                jwtTokenValidator(keycloakJwkProvider()),
                                authenticationManagerBean(),
                                authenticationFailureHandler()),
                        BasicAuthenticationFilter.class);
    }

    /**
     * In this method some urls are being excluded from security
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(nonSecureUrls);
    }

    @ConditionalOnMissingBean
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * Keycloak is being set as the IDP of the application
     *
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new KeycloakAuthenticationProvider();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AccessTokenAuthenticationFailureHandler();
    }

    @Bean
    public JwtTokenValidator jwtTokenValidator(JwkProviderCustom jwkProvider) {
        return new JwtTokenValidator(jwkProvider);
    }

    @Bean
    public JwkProviderCustom keycloakJwkProvider() {
        return new KeycloakJwkProvider(jwkProviderUrl);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AuthorizationAccessDeniedHandler();
    }

    @Bean
    public Keycloak keycloak(KeycloakSpringBootProperties props) {
        Keycloak keycloak = KeycloakBuilder.builder() //
                .serverUrl(props.getAuthServerUrl()) //
                .realm(props.getRealm()) //
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
                .clientId(props.getResource()) //
                .clientSecret((String) props.getCredentials().get("secret")) //
                .build();

        log.info(String.format("Admin client info. URL= %s; realmId= %s", props.getAuthServerUrl(), props.getRealm()));


        return keycloak;
    }
}