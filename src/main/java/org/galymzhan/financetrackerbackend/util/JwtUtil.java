package org.galymzhan.financetrackerbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.galymzhan.financetrackerbackend.config.JwtProperties;
import org.galymzhan.financetrackerbackend.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public String generateToken(UserDetails userDetails) {
        var claims = new HashMap<String, Object>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("username", customUserDetails.getUsername());
            claims.put("role", customUserDetails.getRole().name());
        }
        return generateToken(claims, userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        var claims = new HashMap<String, Object>();
        claims.put("type", "refresh");
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
        }
        return generateRefreshToken(claims, userDetails);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails user) {
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + jwtProperties.getExpirationMs();

        log.debug("Generating JWT token for user: {} with expiration: {} ms ({})",
                user.getUsername(),
                jwtProperties.getExpirationMs(),
                new Date(expirationTime));

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> extraClaims, UserDetails user) {
        long currentTime = System.currentTimeMillis();
        long expirationTime = currentTime + jwtProperties.getRefreshExpirationMs();

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        final String userName = extractUserName(token);
        return (userName.equals(user.getUsername())) && !isTokenExpired(token);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public boolean isRefreshToken(String token) {
        String tokenType = extractClaim(token, claims -> claims.get("type", String.class));
        return "refresh".equals(tokenType);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean expired = expiration.before(new Date());

        if (expired) {
            log.debug("Token expired at: {}", expiration);
        }

        return expired;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSigningKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}