package com.titsuite.managers;

import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import javax.ws.rs.container.ContainerRequestContext;
import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Map;
import java.util.Properties;

public class TokenManager {

    public enum KeySelection {
        AUTHENTICATION_KEY,
        REFRESH_KEY
    }

    private static RsaJsonWebKey rsaJsonAuthKey = null;
    private static RsaJsonWebKey rsaJsonRefreshKey = null;
    private static final String issuer = "titsuite.com";
    private static final int authTTL = 17; // 17 minutes until authentication token expiration
    private static final int refreshTTL = 10080; // 1 Week == 10080 Minutes

    static {
        try {
            Properties properties = new Properties();
            properties.load(TokenManager.class.getClassLoader().getResourceAsStream("config.properties"));
            String jwkAuthJson = properties.getProperty("authKey");
            String jwkRefreshJson = properties.getProperty("refreshKey");
            rsaJsonAuthKey = (RsaJsonWebKey) PublicJsonWebKey.Factory.newPublicJwk(jwkAuthJson);
            rsaJsonRefreshKey = (RsaJsonWebKey) PublicJsonWebKey.Factory.newPublicJwk(jwkRefreshJson);
        } catch (JoseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateJWT(Map<String, Object> toEncryptMap, KeySelection selection) throws JoseException {
        int ttl = 0;
        String keyId = null;
        PrivateKey privateKey = null;
        String header = "";

        if (selection == KeySelection.AUTHENTICATION_KEY) {
            rsaJsonAuthKey.setKeyId("auth_key");
            ttl = authTTL;
            keyId = rsaJsonAuthKey.getKeyId();
            privateKey = rsaJsonAuthKey.getPrivateKey();
            header += "Bearer ";
        }
        else if (selection == KeySelection.REFRESH_KEY) {
            rsaJsonRefreshKey.setKeyId("refresh_key");
            ttl = refreshTTL;
            keyId = rsaJsonRefreshKey.getKeyId();
            privateKey = rsaJsonRefreshKey.getPrivateKey();
        }

        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setGeneratedJwtId();
        claims.setExpirationTimeMinutesInTheFuture(ttl);
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);

        for (Map.Entry<String, Object> entry : toEncryptMap.entrySet())
            claims.setClaim(entry.getKey(), entry.getValue());

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(privateKey);
        jws.setKeyIdHeaderValue(keyId);
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return header + jws.getCompactSerialization();
    }

    public static Map<String, Object> validateJWT(String token, KeySelection selection) throws InvalidJwtException {
        int maximumValidity = 0;
        Key key = null;

        if (selection == KeySelection.AUTHENTICATION_KEY) {
            token = token.split("Bearer ")[1];
            maximumValidity = authTTL;
            key = rsaJsonAuthKey.getKey();
        }
        else if (selection == KeySelection.REFRESH_KEY) {
            maximumValidity = refreshTTL;
            key = rsaJsonRefreshKey.getKey();
        }

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setRequireExpirationTime()
            .setMaxFutureValidityInMinutes(maximumValidity)
            .setAllowedClockSkewInSeconds(30)
            .setExpectedIssuer(issuer)
            .setVerificationKey(key)
            .build();

        JwtClaims jwtClaims = jwtConsumer.processToClaims(token);

        return jwtClaims.getClaimsMap();
    }

    public static int getAuthTTL() { return authTTL; }

}
