package com.twitter.posts.auth;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtValidator {

    private final ConfigurableJWTProcessor<SecurityContext> processor;
    private final String audience;

    public JwtValidator(
            @Value("${auth0.issuer-uri}") String issuerUri,
            @Value("${auth0.audience}") String audience) throws Exception {
        this.audience = audience;
        processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(new JWSVerificationKeySelector<>(
                JWSAlgorithm.RS256,
                new RemoteJWKSet<>(new URL(issuerUri + ".well-known/jwks.json"))
        ));
    }

    public JWTClaimsSet validate(String bearerToken) throws Exception {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or malformed Authorization header");
        }
        JWTClaimsSet claims = processor.process(bearerToken.substring(7), null);
        if (!claims.getAudience().contains(audience)) {
            throw new IllegalArgumentException("Invalid audience");
        }
        return claims;
    }

    public boolean hasScope(JWTClaimsSet claims, String requiredScope) {
        String scopeClaim = (String) claims.getClaim("scope");
        if (scopeClaim == null) return false;
        return Arrays.asList(scopeClaim.split(" ")).contains(requiredScope);
    }
}
