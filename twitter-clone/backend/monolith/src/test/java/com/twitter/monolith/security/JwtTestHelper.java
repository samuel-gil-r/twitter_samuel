package com.twitter.monolith.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtTestHelper {

    public static final RSAKey TEST_RSA_KEY;

    static {
        try {
            TEST_RSA_KEY = new RSAKeyGenerator(2048)
                    .keyID("test-key-id")
                    .generate();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate test RSA key", e);
        }
    }

    public static String generateToken(String subject, String email, List<String> scopes) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer("https://test.auth0.com/")
                    .audience("https://twitter-clone-api")
                    .claim("email", email)
                    .claim("scope", String.join(" ", scopes))
                    .expirationTime(new Date(System.currentTimeMillis() + 3_600_000))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("test-key-id").build(),
                    claims
            );
            jwt.sign(new RSASSASigner(TEST_RSA_KEY));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign test JWT", e);
        }
    }
}
