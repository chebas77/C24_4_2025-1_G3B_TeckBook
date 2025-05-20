package com.usuario.backend.security;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        logger.info("OAuth2 Authentication Success: Starting handler for URI: {}", request.getRequestURI());

        try {
            // Obtener datos del usuario OAuth2
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();

            logger.info("OAuth2 User attributes: {}", attributes);

            String email = (String) attributes.get("email");
            String name = (String) attributes.get("given_name");
            String lastName = (String) attributes.get("family_name");

            logger.info("User authenticated with OAuth2: {}", email);

            // Verificar dominio institucional
            if (!email.endsWith("@tecsup.edu.pe")) {
                logger.error("Error: Email domain not allowed: {}", email);
                response.sendRedirect("http://localhost:5173/login?error=domain_not_allowed");
                return;
            }

            try {
                // Verificar si el usuario ya existe
                Usuario existingUser = usuarioService.findByCorreoInstitucional(email);

                if (existingUser == null) {
                    // Crear nuevo usuario con los datos básicos
                    Usuario newUser = new Usuario();
                    newUser.setCorreoInstitucional(email);
                    newUser.setNombre(name != null ? name : "");
                    newUser.setApellidos(lastName != null ? lastName : "");
                    newUser.setRol("ESTUDIANTE");

                    // Generar código de usuario basado en el email
                    String codigo = email.split("@")[0];
                    newUser.setCodigo(codigo);

                    // Establecer ciclo por defecto
                    newUser.setCiclo("1");

                    // Usar IDs válidos existentes
                    newUser.setDepartamentoId(1L); // Tecnología Digital

                    // Si necesitas valores para campos opcionales pero con restricción de clave foránea
                    // newUser.setCarreraId(1L);
                    // newUser.setSeccionId(1L);

                    // Registrar usuario
                    usuarioService.registrarUsuarioOAuth(newUser);
                    logger.info("Created new user for: {}", email);
                } else {
                    logger.info("User already exists: {}", email);
                }
            } catch (Exception e) {
                logger.error("Error processing user: {}", e.getMessage(), e);
                response.sendRedirect("http://localhost:5173/login?error=user_processing");
                return;
            }

            // Generar token JWT
            String token = null;
            try {
                token = tokenProvider.generateToken(email);
                logger.info("JWT token generated for user: {}", email);
            } catch (Exception e) {
                logger.error("Error generating token: {}", e.getMessage(), e);
                response.sendRedirect("http://localhost:5173/login?error=token_generation");
                return;
            }

            // URL frontend con el token
            String targetUrl = "http://localhost:5173/home?token=" + token;
            logger.info("About to redirect to: {}", targetUrl);

            // Redirección al frontend
            response.sendRedirect(targetUrl);
            logger.info("Redirect sent to: {}", targetUrl);

        } catch (Exception e) {
            logger.error("General error in OAuth authentication success handler", e);
            response.sendRedirect("http://localhost:5173/login?error=general_error");
        }
    }
}