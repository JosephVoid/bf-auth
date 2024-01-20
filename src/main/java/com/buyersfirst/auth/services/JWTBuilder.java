package com.buyersfirst.auth.services;

import java.security.Key;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.security.auth.message.AuthException;

@Component
public class JWTBuilder {
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiry}")
    private Float jwtExpiry;

    Key key;

    public JWTBuilder() {

    }

    public String generateToken(String usersId, String roles) {
        try {
            JwtClaims jwtClaims = new JwtClaims();
            jwtClaims.setIssuer(jwtIssuer);
            jwtClaims.setExpirationTimeMinutesInTheFuture(jwtExpiry);
            jwtClaims.setAudience("ALL");
            jwtClaims.setStringListClaim("groups", roles);
            jwtClaims.setGeneratedJwtId();
            jwtClaims.setIssuedAtToNow();
            jwtClaims.setSubject("AUTHTOKEN");
            jwtClaims.setClaim("userId", usersId);
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(jwtClaims.toJson());
            jws.setKey(key);
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);

            return jws.getCompactSerialization();
        } catch (JoseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public JwtClaims generateParseToken(String token) throws AuthException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setSkipSignatureVerification()
                .setAllowedClockSkewInSeconds(60)
                .setRequireSubject()
                .setExpectedIssuer(jwtIssuer)
                .setExpectedAudience("ALL")
                .setExpectedSubject("AUTHTOKEN")
                .setVerificationKey(key)
                .setJwsAlgorithmConstraints(
                        new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                                AlgorithmIdentifiers.HMAC_SHA256))
                .build();
        try
        {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            return jwtClaims;
        } catch (InvalidJwtException e) {
            try {
                if (e.hasExpired())
                {
                    throw new AuthException("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
                }
                if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
                {
                    throw new AuthException("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
                }
                throw new AuthException(e.getMessage());
            } catch (MalformedClaimException innerE) {
                throw new AuthException("invalid Token");
            }

        }
    }

    @PostConstruct
    public void init() {
        try {
            key = new HmacKey(jwtSecret.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
