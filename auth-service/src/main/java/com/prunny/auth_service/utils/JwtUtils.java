package com.prunny.auth_service.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final Key signingKey;

    public JwtUtils(@Value("${jwt.secret}") String jwtSecret) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA512");
        this.signingKey = key;
    }

    public String generateToken(String email, Long userId) {
        return Jwts.builder()
            .claim("auth", Arrays.asList("ROLE_USER"))
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
            .signWith(signingKey, SignatureAlgorithm.HS512)
            .compact();
    }

    public  boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new JwtException(e.getMessage());

        }
    }
}
