package com.albaExpress.api.alba.security;

import com.albaExpress.api.alba.entity.Master;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String createToken(Master master) {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS512)
                .setIssuer("albaExpress")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                .setSubject(master.getId())
                .claim("name", master.getMasterName())
                .compact();
    }

    public TokenUserInfo validateAndGetTokenInfo(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.info("claims: {}", claims);

        return TokenUserInfo.builder()
                .id(claims.getSubject())
                .name(claims.get("name", String.class))
                .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token", e);
            return false;
        }
    }

    @Getter @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenUserInfo {
        private String id;
        private String name;
    }
}
