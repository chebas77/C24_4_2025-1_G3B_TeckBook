package com.usuario.backend.security.jwt;

import com.usuario.backend.service.core.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private JwtTokenManager jwtTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // Verificar si el token es válido (incluye blacklist)
                if (jwtTokenManager.isTokenValid(jwt)) {
                    String email = tokenProvider.getEmailFromToken(jwt);

                    if (email != null) {
                        UserDetails userDetails = usuarioService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        logger.debug("Usuario autenticado: {}", email);
                    } else {
                        handleInvalidToken(response, "Token JWT inválido");
                        return;
                    }
                } else {
                    // Token inválido o en blacklist
                    if (isProtectedEndpoint(request)) {
                        handleInvalidToken(response, "Token JWT inválido o expirado");
                        return;
                    }
                }
            } else if (isProtectedEndpoint(request)) {
                // No hay token en endpoint protegido
                handleInvalidToken(response, "Token JWT requerido");
                return;
            }
        } catch (Exception ex) {
            logger.error("Error al procesar autenticación JWT: {}", ex.getMessage());
            
            if (isProtectedEndpoint(request)) {
                handleInvalidToken(response, "Error al procesar autenticación");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Determina si un endpoint requiere autenticación
     */
    private boolean isProtectedEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Endpoints públicos
        String[] publicPaths = {
            "/",
            "/oauth2/",
            "/login",
            "/api/auth/login",
            "/api/auth/google-login",
            "/api/core/usuarios/register",
            "/api/core/carreras/activas",
            "/api/public/",
            "/error"
        };
        
        for (String publicPath : publicPaths) {
            if (path.equals(publicPath) || path.startsWith(publicPath)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Maneja tokens inválidos
     */
    private void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":%d}",
            message, System.currentTimeMillis()
        );
        
        response.getWriter().write(jsonResponse);
    }

    /**
     * Extrae el token JWT del header Authorization
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}