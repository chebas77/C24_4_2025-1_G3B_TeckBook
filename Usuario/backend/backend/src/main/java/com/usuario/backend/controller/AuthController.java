package com.usuario.backend.controller;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.security.JwtTokenProvider;
import com.usuario.backend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String correoInstitucional = loginRequest.get("correoInstitucional");
        String password = loginRequest.get("password");
        
        logger.info("Intento de login para: {}", correoInstitucional);
        
        if (correoInstitucional == null || password == null) {
            logger.warn("Login fallido: faltan credenciales");
            return ResponseEntity.badRequest().body("Correo institucional y contraseña son requeridos");
        }
        
        boolean isAuthenticated = usuarioService.autenticarUsuario(correoInstitucional, password);

        if (isAuthenticated) {
            logger.info("Login exitoso para: {}", correoInstitucional);
            
            // Generar JWT token
            String token = jwtTokenProvider.generateToken(correoInstitucional);
            logger.debug("Token generado: {}", token.substring(0, 20) + "...");

            // Obtener información del usuario
            Usuario user = usuarioService.findByCorreoInstitucional(correoInstitucional);
            
            // Devolver token y datos básicos del usuario
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("id", user.getId());
            response.put("nombre", user.getNombre());
            response.put("apellidos", user.getApellidos());
            response.put("correoInstitucional", user.getCorreoInstitucional());
            response.put("rol", user.getRol());

            return ResponseEntity.ok(response);
        } else {
            logger.warn("Login fallido para: {}", correoInstitucional);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @GetMapping("/google-login")
    public ResponseEntity<?> googleLogin() {
        // Esta URL redirecciona a la página de autenticación de Google
        // Spring Security se encargará de la redirección
        // Este endpoint es solo para proporcionar la URL al frontend
        String redirectUrl = "/oauth2/authorize/google";
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", redirectUrl);

        logger.info("Redirección a login de Google: {}", redirectUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
public ResponseEntity<?> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
    logger.debug("Solicitud de información de usuario con cabecera: {}", 
            (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));
    
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        logger.warn("Cabecera de autorización inválida o ausente");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Se requiere una cabecera de autorización válida"));
    }
    
    String token = authHeader.substring(7);
    
    if (token.isEmpty()) {
        logger.warn("Token vacío proporcionado");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Token vacío"));
    }

    try {
        if (jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmailFromToken(token);
            
            if (email == null) {
                logger.warn("No se pudo extraer el email del token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido"));
            }
            
            logger.debug("Token validado para: {}", email);
            Usuario user = usuarioService.findByCorreoInstitucional(email);

            if (user != null) {
                logger.info("Información de usuario encontrada para: {}", email);
                
                // 🔧 CRÍTICO: Incluir profileImageUrl en la respuesta
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("nombre", user.getNombre());
                userInfo.put("apellidos", user.getApellidos());
                userInfo.put("correoInstitucional", user.getCorreoInstitucional());
                userInfo.put("rol", user.getRol());
                userInfo.put("codigo", user.getCodigo());
                userInfo.put("ciclo", user.getCiclo());
                userInfo.put("departamentoId", user.getDepartamentoId());
                userInfo.put("carreraId", user.getCarreraId());
                userInfo.put("seccionId", user.getSeccionId());
                
                // 🎯 ESTE ES EL FIX PRINCIPAL
                userInfo.put("profileImageUrl", user.getProfileImageUrl());
                
                // 🔍 DEBUG LOGS
                logger.info("ProfileImageUrl desde BD: {}", user.getProfileImageUrl());
                logger.info("Respuesta completa enviada al frontend: {}", userInfo);

                return ResponseEntity.ok(userInfo);
            } else {
                logger.warn("Usuario no encontrado para el email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
        } else {
            logger.warn("Token JWT inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token JWT inválido"));
        }
    } catch (Exception e) {
        logger.error("Error al procesar el token JWT: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al procesar el token", "message", e.getMessage()));
    }
}

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT es stateless, así que técnicamente no hay nada que hacer en el servidor
        // El cliente simplemente elimina el token almacenado
        logger.info("Solicitud de logout recibida");
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente"));
    }
}