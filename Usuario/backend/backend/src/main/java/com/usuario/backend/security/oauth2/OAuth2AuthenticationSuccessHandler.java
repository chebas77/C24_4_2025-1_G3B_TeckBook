package com.usuario.backend.security.oauth2;

import com.usuario.backend.model.entity.core.Usuario;
import com.usuario.backend.security.jwt.JwtTokenProvider;
import com.usuario.backend.service.core.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioService usuarioService;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        logger.info("OAuth2 Authentication Success iniciado");

        try {
            // Obtener datos del usuario OAuth2
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();

            String email = (String) attributes.get("email");
            String name = (String) attributes.get("given_name");
            String lastName = (String) attributes.get("family_name");
            String pictureUrl = (String) attributes.get("picture");

            logger.info("Usuario OAuth2: {} - {}", name, email);

            // Validar dominio institucional
            if (!email.endsWith("@tecsup.edu.pe")) {
                logger.error("Dominio no permitido: {}", email);
                String errorMsg = URLEncoder.encode("Solo se permiten correos con dominio @tecsup.edu.pe", StandardCharsets.UTF_8);
                response.sendRedirect(frontendUrl + "/?error=" + errorMsg);
                return;
            }

            try {
                Usuario usuario = usuarioService.findByEmail(email).orElse(null);
                boolean isNewUser = (usuario == null);
                
                if (isNewUser) {
                    // Crear nuevo usuario
                    logger.info("Creando nuevo usuario OAuth2: {}", email);
                    usuario = crearUsuarioOAuth2(email, name, lastName, pictureUrl);
                } else {
                    // Actualizar usuario existente
                    logger.info("Actualizando usuario existente: {}", email);
                    usuario = actualizarUsuarioOAuth2(usuario, name, lastName, pictureUrl);
                }

                // Generar token y redirigir
                String token = tokenProvider.generateToken(email);
                StringBuilder redirectUrl = new StringBuilder(frontendUrl + "/home?token=" + token);
                
                if (isNewUser) {
                    redirectUrl.append("&new=true");
                }
                
                if (requiereCompletarDatos(usuario)) {
                    redirectUrl.append("&incomplete=true");
                }

                logger.info("Redirigiendo a: {}", redirectUrl.toString());
                response.sendRedirect(redirectUrl.toString());

            } catch (Exception e) {
                logger.error("Error procesando usuario OAuth2: {}", e.getMessage(), e);
                String errorMsg = URLEncoder.encode("Error al procesar usuario: " + e.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(frontendUrl + "/?error=" + errorMsg);
            }

        } catch (Exception e) {
            logger.error("Error general en OAuth2 Success Handler", e);
            String errorMsg = URLEncoder.encode("Error general: " + e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(frontendUrl + "/?error=" + errorMsg);
        }
    }

    /**
     * Crea un nuevo usuario OAuth2
     */
    private Usuario crearUsuarioOAuth2(String email, String name, String lastName, String pictureUrl) {
        Usuario newUser = new Usuario();
        newUser.setEmail(email);
        newUser.setNombres(name != null ? name : "");
        newUser.setApellido(lastName != null ? lastName : "");
        newUser.setRol(Usuario.Rol.ALUMNO);
        
        // Imagen de Google si está disponible
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            newUser.setProfileImageUrl(pictureUrl);
        }
      
        // Valores por defecto
        newUser.setDepartamentoId(1L); // Tecnología Digital por defecto
        newUser.setActivo(true);
        
        return usuarioService.save(newUser);
    }

    /**
     * Actualiza usuario existente con datos de Google
     */
    private Usuario actualizarUsuarioOAuth2(Usuario usuario, String name, String lastName, String pictureUrl) {
        // Actualizar nombre y apellidos si han cambiado
        if (name != null && !name.isEmpty()) {
            usuario.setNombres(name);
        }
        if (lastName != null && !lastName.isEmpty()) {
            usuario.setApellido(lastName);
        }
        
        // Actualizar imagen solo si no tiene una personalizada
        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            if (usuario.getProfileImageUrl() == null || 
                usuario.getProfileImageUrl().isEmpty() || 
                usuario.getProfileImageUrl().contains("googleusercontent.com")) {
                usuario.setProfileImageUrl(pictureUrl);
            }
        }
        
        return usuarioService.save(usuario);
    }

    /**
     * Verifica si el usuario requiere completar datos
     */
    private boolean requiereCompletarDatos(Usuario usuario) {
        return usuario.getCarreraId() == null;
    }
}