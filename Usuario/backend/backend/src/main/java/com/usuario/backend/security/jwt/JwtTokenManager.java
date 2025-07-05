package com.usuario.backend.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtTokenManager {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenManager.class);
    
    // Blacklist de tokens invalidados (en memoria)
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    // Cache de tokens expirados para evitar procesamiento innecesario
    private final Set<String> expiredTokens = ConcurrentHashMap.newKeySet();
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Agregar token a la blacklist (logout)
     */
    public void blacklistToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
            logger.info("Token agregado a blacklist. Total: {}", blacklistedTokens.size());
        }
    }
    
    /**
     * Verificar si un token está en la blacklist
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Verificar si un token es válido (no blacklisted y no expirado)
     */
    public boolean isTokenValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // 1. Verificar blacklist primero (más rápido)
        if (isTokenBlacklisted(token)) {
            return false;
        }
        
        // 2. Verificar cache de expirados
        if (expiredTokens.contains(token)) {
            return false;
        }
        
        // 3. Verificar con JwtTokenProvider
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        if (!isValid) {
            // Agregar a cache de expirados
            expiredTokens.add(token);
        }
        
        return isValid;
    }
    
    /**
     * Obtener estadísticas de tokens
     */
    public TokenStats getTokenStats() {
        return new TokenStats(blacklistedTokens.size(), expiredTokens.size());
    }
    
    /**
     * Limpiar tokens expirados cada hora
     */
    @Scheduled(fixedRate = 3600000) // 1 hora
    public void cleanupExpiredTokens() {
        logger.info("Limpiando tokens expirados...");
        
        int initialSize = blacklistedTokens.size();
        
        // Limpiar blacklist de tokens expirados
        blacklistedTokens.removeIf(token -> {
            try {
                return !jwtTokenProvider.validateToken(token);
            } catch (Exception e) {
                return true; // Remover si hay error
            }
        });
        
        // Limpiar cache si es muy grande
        if (expiredTokens.size() > 1000) {
            expiredTokens.clear();
        }
        
        logger.info("Limpieza completada. Blacklist: {} -> {}", initialSize, blacklistedTokens.size());
    }
    
    /**
     * Clase para estadísticas
     */
    public static class TokenStats {
        private final int blacklistedCount;
        private final int expiredCacheCount;
        
        public TokenStats(int blacklistedCount, int expiredCacheCount) {
            this.blacklistedCount = blacklistedCount;
            this.expiredCacheCount = expiredCacheCount;
        }
        
        public int getBlacklistedCount() { return blacklistedCount; }
        public int getExpiredCacheCount() { return expiredCacheCount; }
    }
}
