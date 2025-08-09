package com.prunny.auth_service.security;

import io.jsonwebtoken.JwtException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static com.prunny.auth_service.security.SecurityUtils.AUTHORITIES_CLAIM;
import static com.prunny.auth_service.security.SecurityUtils.JWT_ALGORITHM;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(String email, Long userId) {
        Instant now = Instant.now();

        // Build the JWT claims
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
            .subject(email)
            .issuedAt(now)
            .expiresAt(now.plus(10, ChronoUnit.HOURS))
            .claim(AUTHORITIES_CLAIM, Arrays.asList("ROLE_ADMIN"))
            .claim("userId", userId)
            .build();

        // Create the JWS header with explicit algorithm - THIS IS THE KEY!
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        // Encode with both header and claims
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
