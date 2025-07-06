package com.usuario.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlConfig {
    
    // URLs del Frontend
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendBaseUrl;
    
    @Value("${app.frontend.login.path:/}")
    private String frontendLoginPath;
    
    @Value("${app.frontend.home.path:/home}")
    private String frontendHomePath;
    
    @Value("${app.frontend.error.path:/error}")
    private String frontendErrorPath;
    
    // URLs del Backend
    @Value("${app.backend.url:http://localhost:8080}")
    private String backendBaseUrl;
    
    // Getters
    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }
    
    public String getFrontendLoginUrl() {
        return frontendBaseUrl + frontendLoginPath;
    }
    
    public String getFrontendHomeUrl() {
        return frontendBaseUrl + frontendHomePath;
    }
    
    public String getFrontendErrorUrl() {
        return frontendBaseUrl + frontendErrorPath;
    }
    
    public String getBackendBaseUrl() {
        return backendBaseUrl;
    }
    
    // Métodos helper para construir URLs específicas
    public String buildFrontendUrl(String path) {
        return frontendBaseUrl + (path.startsWith("/") ? path : "/" + path);
    }
    
    public String buildBackendUrl(String path) {
        return backendBaseUrl + (path.startsWith("/") ? path : "/" + path);
    }
    
    /**
     * 🔧 Construye URL de redirección con token y parámetros
     */
    public String buildTokenRedirectUrl(String token, boolean isNewUser, boolean isIncomplete) {
        StringBuilder url = new StringBuilder(getFrontendHomeUrl());
        url.append("?token=").append(token);
        
        if (isNewUser) {
            url.append("&new=true");
        }
        if (isIncomplete) {
            url.append("&incomplete=true");
        }
        
        return url.toString();
    }
    
    /**
     * 🔧 Construye URL de error con mensaje
     */
    public String buildErrorUrl(String errorMessage) {
        try {
            String encodedError = java.net.URLEncoder.encode(errorMessage, "UTF-8");
            return getFrontendLoginUrl() + "?error=" + encodedError;
        } catch (Exception e) {
            return getFrontendLoginUrl() + "?error=unknown_error";
        }
    }
}
