package by.testtask.balancehub.services.impl;

import by.testtask.balancehub.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static by.testtask.balancehub.utils.Constants.USER_CLAIM_KEY;

@Component
@Slf4j
public class JwtProvider {
    @Value("${spring.application.security.jwt.access-key.secret}")
    private String jwtAccessSecret;

    @Value("${spring.application.security.jwt.refresh-key.secret}")
    private String jwtRefreshSecret;

    @Value("${spring.application.security.jwt.access-key.expiration-time}")
    private Integer jwtAccessExpirationTime;

    @Value(("${spring.application.security.jwt.refresh-key.expiration-time}"))
    private Integer jwtRefreshExpirationTime;

    public String generateAccessToken(@NotNull User user) {
        final Date accessExpiration = generateAccessExpiration(jwtAccessExpirationTime);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(accessExpiration)
                .signWith(getJwtAccessSecret())
                .claim(USER_CLAIM_KEY, user.getId())
                .compact();
    }

    public String generateRefreshToken(@NotNull User user) {
        final Date refreshExpiration = generateAccessExpiration(jwtRefreshExpirationTime);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(refreshExpiration)
                .signWith(getJwtRefreshSecret())
                .compact();

    }

    public boolean validateAccessToken(@NotNull String accessToken) {
        return validateToken(accessToken, getJwtAccessSecret());
    }

    public boolean validateRefreshToken(@NotNull String refreshToken) {
        return validateToken(refreshToken, getJwtRefreshSecret());
    }

    private boolean validateToken(@NotNull String token, @NotNull Key secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            //TODO переделать
            log.warn("JWT validation failed: {}", e.getMessage());
        }

        return false;
    }

    public Claims getAccessClaims(@NotNull String token) {
        return getClaims(token, getJwtAccessSecret());
    }

    public Claims getRefreshClaims(@NotNull String token) {
        return getClaims(token, getJwtRefreshSecret());
    }

    private SecretKey getJwtAccessSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
    }

    private SecretKey getJwtRefreshSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    private Claims getClaims(@NotNull String token, @NotNull Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Date generateAccessExpiration(@NotNull Integer expirationTime) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(expirationTime)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Date.from(accessExpirationInstant);
    }

}