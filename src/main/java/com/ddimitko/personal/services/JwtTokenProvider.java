package com.ddimitko.personal.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private String jwtSecret = generateSecretKey();
    private long jwtExpiration = 3600000;

    public String generateToken(Authentication authentication){

        String usertag = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .subject(usertag)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //extract usertag from JWT token
    public String getUserTag(String token) {

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //validate JWT token
    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parse(token);
        return true;
    }

    public String generateSecretKey(){

        //length means (32 bytes are required for 256-bit key)
        int length = 32;

        //Create a secure random generator
        SecureRandom secureRandom = new SecureRandom();

        //Generate the random bytes
        byte[] keyBytes = new byte[length];

        //Encode the key in Base64 format for easier storage and usage
        secureRandom.nextBytes(keyBytes);

        return Base64.getEncoder().encodeToString(keyBytes);
    }

}
