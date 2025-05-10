package com.spacedlearning.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Provider for JWT token operations. Handles token generation, validation, and
 * parsing.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";
    private static final String TOKEN_TYPE_KEY = "type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String TOKEN_ID_KEY = "jti";
    private static final SecureRandom secureRandom = new SecureRandom();
    @Value("${spring.jwt.secret}")
    private String jwtSecret;
    @Value("${spring.jwt.expiration}")
    private long jwtExpiration;
    @Value("${spring.jwt.refresh.expiration:604800000}") // Default 7 days
    private long refreshTokenExpiration;
    @Value("${spring.jwt.issuer:kardio-api}")
    private String jwtIssuer;

    /**
     * Generates a refresh token for the given authentication.
     *
     * @param authentication The authentication object
     * @return A refresh token
     */
    public String generateRefreshToken(Authentication authentication) {
        final var userDetails = (UserDetails) authentication.getPrincipal();

        final Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_KEY, TOKEN_TYPE_REFRESH);
        claims.put(TOKEN_ID_KEY, generateTokenId());

        return generateRefreshToken(claims, userDetails);
    }

    /**
     * Generates a refresh token with custom claims for the given user details.
     *
     * @param extraClaims Additional claims to include in the token
     * @param userDetails The user details
     * @return A refresh token
     */
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        final var now = new Date();
        final var expiryDate = new Date(now.getTime() + this.refreshTokenExpiration);

        log.debug("Generating JWT refresh token for user: {}", userDetails.getUsername());

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(this.jwtIssuer)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generates a JWT token for the given authentication.
     *
     * @param authentication The authentication object
     * @return A JWT token
     */
    public String generateToken(Authentication authentication) {
        final var userDetails = (UserDetails) authentication.getPrincipal();

        // Extract authorities as a comma-separated string, safely handling null
        final var authorities = Optional.ofNullable(authentication.getAuthorities())
                .map(auths -> auths.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .orElse("");

        // Add authorities to claims
        final Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, authorities);
        claims.put(TOKEN_TYPE_KEY, TOKEN_TYPE_ACCESS);
        claims.put(TOKEN_ID_KEY, generateTokenId());

        return generateToken(claims, userDetails);
    }

    /**
     * Generates a JWT token with custom claims for the given user details.
     *
     * @param extraClaims Additional claims to include in the token
     * @param userDetails The user details
     * @return A JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        final var now = new Date();
        final var expiryDate = new Date(now.getTime() + this.jwtExpiration);

        log.debug("Generating JWT access token for user: {}", userDetails.getUsername());

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(this.jwtIssuer)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Generates a secure random token ID.
     *
     * @return A random string to use as token ID
     */
    private String generateTokenId() {
        final var randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token
     * @return The claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Extracts the authorities from a JWT token.
     *
     * @param token The JWT token
     * @return The authorities as a comma-separated string
     */
    public String getAuthoritiesFromToken(String token) {
        final var claims = getAllClaimsFromToken(token);
        return claims.get(AUTHORITIES_KEY, String.class);
    }

    /**
     * Gets the signing key for JWT token generation and validation.
     *
     * @return The signing key
     */
    private SecretKey getSigningKey() {
        final var keyBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token
     * @return The username
     */
    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Checks if the token is a refresh token.
     *
     * @param token The JWT token
     * @return true if it's a refresh token, false otherwise
     */
    public boolean isRefreshToken(String token) {
        final var claims = getAllClaimsFromToken(token);
        return TOKEN_TYPE_REFRESH.equals(claims.get(TOKEN_TYPE_KEY));
    }

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Parse the token and verify signature
            final var claims = getAllClaimsFromToken(token);

            // Check expiration
            return !claims.getExpiration().before(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token");
        }
    }
}