package org.yuri.userauth.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yuri.userauth.domain.user.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JWTService {

    @Value("${security.jwt.expiration-time}")
    private String expiration;

    @Value("${security.jwt.token-secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            return Jwts.builder()
                    .subject(user.getLogin())
                    .expiration(getExpirationTime())
                    .signWith(getSecretKey())
                    .compact();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating token!", e);
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expirationDate = claims.getExpiration();
            LocalDateTime dateTime = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(dateTime);

        } catch (Exception e) {
            return false;
        }
    }

    public String geLoginFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private Date getExpirationTime() {
        long expirationTime = Long.parseLong(expiration);
        return Date.from(LocalDateTime.now().plusHours(expirationTime)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    private SecretKey getSecretKey() {
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }
}
