package com.codemind.ems.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
            "mysecretkeymysecretkeymysecretkeymysecretkey".getBytes()
    );

    private final Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        logger.info("Generated Token: {}", token);
        return token;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            logger.info("Validating token: {}", token);
            String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.info("Token validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}