package com.ddimitko.personal.tools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // Method to generate the SecretKey from the base64 encoded secret key
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes); // Generates the SecretKey
    }

    public String generateAccessToken(String userTag) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "access");
        return createToken(claims, userTag, accessTokenExpiration);
    }

    public String generateRefreshToken(String userTag) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        return createToken(claims, userTag, refreshTokenExpiration);
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getClaimFromToken(token, claims -> claims.get("token_type")));
    }

    // This method creates the JWT token with claims, subject (userTag), and expiration
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims) // Set custom claims (like token_type)
                .subject(subject) // Set the subject (userTag)
                .issuedAt(new Date(System.currentTimeMillis())) // Current timestamp
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Set expiration time
                .signWith(getKey()) // Sign the token using HMAC and the secret key
                .compact();
    }

    // Extract userTag from the token
    public String getUserTagFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Get token's expiration date
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Validate the JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return !isTokenExpired(token); // Ensures the token is valid and not expired
        } catch (JwtException | IllegalArgumentException e) {
            return false; // If the token is invalid or expired
        }
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Method to extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())        // Use the SecretKey to verify the signature
                .build()
                .parseSignedClaims(token)          // Parse and verify the token
                .getPayload();                     // Get the token's claims
    }

    // Extract claims from the token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}

