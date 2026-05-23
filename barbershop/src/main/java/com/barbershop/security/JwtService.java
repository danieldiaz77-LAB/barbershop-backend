package com.barbershop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    // lee el secret y expiración desde application.properties
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {

        byte[] keyBytes = secret.getBytes();
        // JWT con HS256 necesita mínimo 256 bits (32 bytes)
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    // genera el token con el email como subject y claims extra (role, userId)
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    // extrae el email del token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // valida que el token pertenezca al usuario y no haya expirado
    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}