package com.usuario.backend.security;

import com.usuario.backend.service.UsuarioService;
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
            logger.debug("JWT token received for {}: {}", requestURI, 
                        jwt != null ? jwt.substring(0, Math.min(10, jwt.length())) + "..." : "null");

            if (StringUtils.hasText(jwt)) {
                
                // 🔧 NUEVA VALIDACIÓN: Verificar blacklist Y validez del token
                if (jwtTokenManager.isTokenValid(jwt)) {
                    String email = tokenProvider.getEmailFromToken(jwt);

                    if (email != null) {
                        logger.debug("JWT token válido para usuario: {}", email);

                        UserDetails userDetails = usuarioService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Usuario autenticado via JWT: {}", email);
                    } else {
                        logger.debug("No se pudo extraer email del token JWT");
                        handleInvalidToken(response, "Token JWT inválido: no se pudo extraer email");
                        return;
                    }
                } else {
                    // Token inválido (expirado, malformado, o en blacklist)
                    logger.debug("Token JWT inválido o en blacklist para: {}", requestURI);
                    
                    // Solo devolver error en endpoints protegidos
                    if (isProtectedEndpoint(request)) {
                        handleInvalidToken(response, "Token JWT inválido o expirado");
                        return;
                    }
                }
            } else if (isProtectedEndpoint(request)) {
                // No hay token en endpoint protegido
                logger.debug("No hay token JWT para endpoint protegido: {}", requestURI);
                handleInvalidToken(response, "Token JWT requerido");
                return;
            }
        } catch (Exception ex) {
            logger.error("Error al procesar autenticación JWT para {}: {}", requestURI, ex.getMessage());
            
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
        
        // Endpoints públicos (no requieren autenticación)
        String[] publicPaths = {
            "/",
            "/oauth2/",
            "/login",
            "/api/auth/login",
            "/api/usuarios/register",
            "/api/usuarios/login",
            "/api/public/",
            "/error",
            "/api/debug/"  // Temporal para debugging
        };
        
        for (String publicPath : publicPaths) {
            if (path.equals(publicPath) || path.startsWith(publicPath)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Maneja tokens inválidos con respuesta JSON estructurada
     */
    private void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        String jsonResponse = String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":%d,\"requiresLogin\":true}",
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