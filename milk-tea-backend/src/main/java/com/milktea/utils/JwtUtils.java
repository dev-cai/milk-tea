package com.milktea.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 - Spring Boot 3
 * @author MilkTea Team
 */
@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration:604800}")
    private Long refreshExpiration;

    private final Environment environment;

    public JwtUtils(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateConfiguration() {
        boolean dev = java.util.Arrays.asList(environment.getActiveProfiles()).contains("dev");
        if (!dev && (secret == null || secret.startsWith("dev-only") || secret.getBytes(StandardCharsets.UTF_8).length < 32)) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 bytes outside the dev profile");
        }
    }
    
    /**
     * 生成密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成Token
     */
    public String generateToken(Long userId, String username, Integer userType) {
        return generateToken(userId, username, userType, null, expiration, "access");
    }

    public String generateToken(Long userId, String username, Integer userType, Integer tokenVersion) {
        return generateToken(userId, username, userType, tokenVersion, expiration, "access");
    }

    public String generateRefreshToken(Long userId, String username, Integer userType) {
        return generateToken(userId, username, userType, null, refreshExpiration, "refresh");
    }

    public String generateRefreshToken(Long userId, String username, Integer userType, Integer tokenVersion) {
        return generateToken(userId, username, userType, tokenVersion, refreshExpiration, "refresh");
    }

    private String generateToken(Long userId, String username, Integer userType, Integer tokenVersion, Long ttl, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("userType", userType);
        claims.put("tokenType", tokenType);
        if (tokenVersion != null) claims.put("tokenVersion", tokenVersion);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ttl * 1000);
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null && "refresh".equals(claims.get("tokenType", String.class));
    }

    public Integer getTokenVersionFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims == null ? null : claims.get("tokenVersion", Integer.class);
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? Long.valueOf(claims.get("userId").toString()) : null;
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getSubject() : null;
    }
    
    /**
     * 从Token中获取用户类型
     */
    public String getUserTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("userType").toString() : null;
    }
    
    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && !isTokenExpired(claims);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 判断Token是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
