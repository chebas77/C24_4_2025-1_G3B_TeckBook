package com.usuario.backend.controller.auth;

import com.usuario.backend.model.entity.Usuario;
import com.usuario.backend.security.jwt.JwtTokenProvider;
import com.usuario.backend.security.jwt.JwtTokenManager;
import com.usuario.backend.service.user.UsuarioService;
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
    
    @Autowired
    private JwtTokenManager jwtTokenManager;

    /**
     * 🔐 Login tradicional con email y contraseña
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String correoInstitucional = loginRequest.get("correoInstitucional");
        String password = loginRequest.get("password");
        
        logger.info("🔐 Intento de login para: {}", correoInstitucional);
        
        if (correoInstitucional == null || password == null) {
            logger.warn("❌ Login fallido: faltan credenciales");
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Credenciales requeridas",
                "message", "Correo institucional y contraseña son requeridos"
            ));
        }
        
        boolean isAuthenticated = usuarioService.autenticarUsuario(correoInstitucional, password);

        if (isAuthenticated) {
            logger.info("✅ Login exitoso para: {}", correoInstitucional);
            
            // Obtener información del usuario
            Usuario user = usuarioService.findByCorreoInstitucional(correoInstitucional);
            
            // 🔍 VERIFICAR SI PERFIL ESTÁ COMPLETO
            if (user.requiereCompletarDatos()) {
                logger.info("📝 Usuario requiere completar datos: {}", correoInstitucional);
                
                // Generar token temporal para completar datos
                String token = jwtTokenProvider.generateToken(correoInstitucional);
                
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "type", "Bearer",
                    "requiresCompletion", true,
                    "redirectTo", "/completar-perfil",
                    "message", "Perfil incompleto. Redirigiendo para completar datos."
                ));
            }
            
            // Generar JWT token normal
            String token = jwtTokenProvider.generateToken(correoInstitucional);
            
            // Devolver respuesta completa
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("requiresCompletion", false);
            response.put("user", buildUserResponse(user));

            return ResponseEntity.ok(response);
        } else {
            logger.warn("❌ Login fallido para: {}", correoInstitucional);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Credenciales inválidas",
                "message", "Email o contraseña incorrectos"
            ));
        }
    }

    /**
     * 🌐 Endpoint para obtener URL de Google OAuth2
     */
    @GetMapping("/google-login")
    public ResponseEntity<?> googleLogin() {
        String redirectUrl = "/oauth2/authorization/google";
        
        logger.info("🌐 Redirección a Google OAuth2: {}", redirectUrl);
        
        return ResponseEntity.ok(Map.of(
            "redirectUrl", redirectUrl,
            "message", "Redirigir a autenticación de Google"
        ));
    }

    /**
     * 👤 Obtiene información del usuario autenticado
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.debug("👤 Solicitud de información de usuario");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("❌ Cabecera de autorización inválida");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token requerido", "message", "Se requiere una cabecera de autorización válida"));
        }
        
        String token = authHeader.substring(7);
        
        if (token.isEmpty()) {
            logger.warn("❌ Token vacío");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token vacío"));
        }

        try {
            // 🔐 Validar token (incluye verificación de blacklist)
            if (!jwtTokenManager.isTokenValid(token)) {
                logger.warn("❌ Token inválido o en blacklist");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido", "message", "Token expirado o invalidado"));
            }
            
            String email = jwtTokenProvider.getEmailFromToken(token);
            
            if (email == null) {
                logger.warn("❌ No se pudo extraer email del token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token malformado"));
            }
            
            Usuario user = usuarioService.findByCorreoInstitucional(email);

            if (user != null) {
                logger.info("✅ Información de usuario encontrada para: {}", email);
                
                // 🔍 VERIFICAR SI REQUIERE COMPLETAR DATOS
                Map<String, Object> userInfo = buildUserResponse(user);
                userInfo.put("requiresCompletion", user.requiereCompletarDatos());

                return ResponseEntity.ok(userInfo);
            } else {
                logger.warn("❌ Usuario no encontrado: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            logger.error("❌ Error procesando token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error del servidor", "message", e.getMessage()));
        }
    }

    /**
     * 🚪 Logout - Invalida el token
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("🚪 Solicitud de logout recibida");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (!token.trim().isEmpty()) {
                    // Obtener información del usuario antes de invalidar
                    String email = null;
                    try {
                        if (jwtTokenProvider.validateToken(token)) {
                            email = jwtTokenProvider.getEmailFromToken(token);
                        }
                    } catch (Exception e) {
                        logger.debug("Token ya expirado: {}", e.getMessage());
                    }
                    
                    // 🔧 INVALIDAR TOKEN EN BLACKLIST
                    jwtTokenManager.blacklistToken(token);
                    
                    logger.info("✅ Token invalidado para usuario: {}", email != null ? email : "desconocido");
                    
                    response.put("message", "Sesión cerrada correctamente");
                    response.put("userEmail", email);
                    response.put("tokenInvalidated", true);
                    response.put("timestamp", System.currentTimeMillis());
                    
                    // Estadísticas opcionales
                    JwtTokenManager.TokenStats stats = jwtTokenManager.getTokenStats();
                    response.put("stats", Map.of(
                        "blacklistedTokens", stats.getBlacklistedCount(),
                        "expiredTokensCache", stats.getExpiredCacheCount()
                    ));
                    
                } else {
                    logger.warn("⚠️ Token vacío en logout");
                    response.put("message", "Sesión cerrada (token vacío)");
                    response.put("tokenInvalidated", false);
                }
            } else {
                logger.info("ℹ️ Logout sin token");
                response.put("message", "Sesión cerrada (sin token activo)");
                response.put("tokenInvalidated", false);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error durante logout: {}", e.getMessage(), e);
            response.put("message", "Error al cerrar sesión, pero sesión terminada");
            response.put("error", e.getMessage());
            response.put("tokenInvalidated", false);
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 🔍 Verifica el estado del token
     */
    @GetMapping("/token/status")
    public ResponseEntity<?> getTokenStatus(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            boolean isValid = jwtTokenManager.isTokenValid(token);
            boolean isBlacklisted = jwtTokenManager.isTokenBlacklisted(token);
            
            String email = null;
            
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    email = jwtTokenProvider.getEmailFromToken(token);
                }
            } catch (Exception e) {
                logger.debug("Error obteniendo info del token: {}", e.getMessage());
            }
            
            response.put("isValid", isValid);
            response.put("isBlacklisted", isBlacklisted);
            response.put("userEmail", email);
            response.put("timestamp", System.currentTimeMillis());
            
        } else {
            response.put("isValid", false);
            response.put("isBlacklisted", false);
            response.put("error", "No token provided");
        }
        
        return ResponseEntity.ok(response);
    }

    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * 📋 Construye respuesta de usuario con campos actualizados
     */
    private Map<String, Object> buildUserResponse(Usuario user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("nombre", user.getNombre());
        userInfo.put("apellidos", user.getApellidos());
        userInfo.put("correoInstitucional", user.getCorreoInstitucional());
        
        // 🔧 FIX: Usar getRolString() para compatibilidad
        userInfo.put("rol", user.getRol() != null ? user.getRol().getValor() : "estudiante");
        
        // 🔧 FIX: Usar cicloActual en lugar de ciclo
        userInfo.put("cicloActual", user.getCicloActual());
        
        userInfo.put("departamentoId", user.getDepartamentoId());
        userInfo.put("carreraId", user.getCarreraId());
        userInfo.put("seccionId", user.getSeccionId());
        userInfo.put("telefono", user.getTelefono());
        
        // 🔧 CRÍTICO: Incluir profileImageUrl
        userInfo.put("profileImageUrl", user.getProfileImageUrl());
        
        // 🔧 DEBUG LOG
        logger.debug("ProfileImageUrl desde BD: {}", user.getProfileImageUrl());
        
        return userInfo;
    }
}