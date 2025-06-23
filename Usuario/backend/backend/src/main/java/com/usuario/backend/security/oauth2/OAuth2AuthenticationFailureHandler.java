package com.usuario.backend.security.oauth2;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, 
                                      AuthenticationException exception) throws IOException, ServletException {

        logger.error("OAuth2 Authentication Failed: {}", exception.getMessage());

        String errorMessage = determineErrorMessage(exception);
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        String targetUrl = frontendUrl + "/?error=" + encodedError;
        
        logger.info("Redirigiendo después de error OAuth2 a: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determina el mensaje de error apropiado
     */
    private String determineErrorMessage(AuthenticationException exception) {
        String message = exception.getMessage();
        
        if (message == null) {
            return "Error de autenticación OAuth2";
        }
        
        if (message.contains("invalid_domain")) {
            return "Solo se permiten correos con dominio @tecsup.edu.pe";
        }
        
        if (message.contains("access_denied")) {
            return "Acceso denegado. Debes autorizar el acceso a tu cuenta de Google.";
        }
        
        if (message.contains("invalid_request")) {
            return "Solicitud OAuth2 inválida. Por favor, intenta nuevamente.";
        }
        
        if (message.contains("server_error")) {
            return "Error del servidor de Google. Por favor, intenta más tarde.";
        }
        
        if (message.contains("temporarily_unavailable")) {
            return "Servicio de Google temporalmente no disponible. Intenta más tarde.";
        }
        
        return "Error de autenticación con Google. Por favor, intenta nuevamente.";
    }
}