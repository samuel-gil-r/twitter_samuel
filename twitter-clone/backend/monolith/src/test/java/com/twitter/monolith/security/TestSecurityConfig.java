package com.twitter.monolith.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.List;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() throws Exception {
        // Decoder backed by the local RSA key — no HTTP call to Auth0
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey(JwtTestHelper.TEST_RSA_KEY.toRSAPublicKey())
                .build();

        OAuth2TokenValidator<Jwt> audienceValidator = token -> {
            List<String> audiences = token.getAudience();
            if (audiences != null && audiences.contains("https://twitter-clone-api")) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
            }
            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                    new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "Bad audience", null));
        };

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(), audienceValidator));

        return decoder;
    }
}
