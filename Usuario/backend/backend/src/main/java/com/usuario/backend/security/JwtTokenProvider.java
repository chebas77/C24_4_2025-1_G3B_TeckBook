package com.usuario.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // Cambiado de javax.annotation a jakarta.annotation
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            // Inicializar la clave una vez al inicio
            this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            logger.info("JWT Token Provider initialized successfully");

            // Generar un token de prueba para verificar que la configuración es correcta
            String testToken = generateToken("test@tecsup.edu.pe");
            logger.info("Test token generated successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize JWT Token Provider: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize JWT Token Provider", e);
        }
    }

    // Generar token para usuario autenticado
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername());
    }

    // Generar token para el email del usuario (OAuth2)
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        logger.debug("Generating token for email: {}", email);

        try {
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(key)
                    .compact();

            logger.debug("Token generation successful");
            return token;
        } catch (Exception e) {
            logger.error("Failed to generate token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    // Extraer email del token
    public String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            logger.error("Failed to get email from token: {}", e.getMessage());
            return null;
        }
    }

    // Validar token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }
}