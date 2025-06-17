package com.usuario.backend.controller.user;

import com.usuario.backend.model.entity.Usuario;
import com.usuario.backend.service.user.UsuarioService;
import com.usuario.backend.service.carrera.CarreraService;
import com.usuario.backend.model.entity.Carrera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CarreraService carreraService;
    
    /**
     * 👤 Obtiene información del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado"));
        }
        
        String username = userDetails.getUsername();
        Usuario usuario = usuarioService.findByCorreoInstitucional(username);
        
        if (usuario != null) {
            return ResponseEntity.ok(buildUserResponse(usuario));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }
    
    /**
     * 🔍 Obtiene usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario != null) {
            return ResponseEntity.ok(buildUserResponse(usuario));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }
    
    /**
     * 📝 Actualiza usuario existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Verificar autenticación
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            
            // Verificar que el usuario existe
            Usuario existingUsuario = usuarioService.findById(id);
            if (existingUsuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
            
            // Verificar permisos (solo puede actualizarse a sí mismo)
            if (!existingUsuario.getCorreoInstitucional().equals(userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permiso para actualizar este usuario"));
            }
            
            logger.info("📝 Actualizando usuario: {} (ID: {})", userDetails.getUsername(), id);
            
            // 🔧 VALIDACIONES PARA ACTUALIZACIÓN
            Map<String, String> validationErrors = validateUpdateData(usuario);
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Errores de validación",
                    "details", validationErrors
                ));
            }
            
            // Proteger campos críticos que no deben cambiar
            usuario.setId(id);
            usuario.setCorreoInstitucional(existingUsuario.getCorreoInstitucional());
            usuario.setRol(existingUsuario.getRol());
            
            // Mantener password existente si no se proporciona uno nuevo
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                usuario.setPassword(existingUsuario.getPassword());
            }
            
            // Actualizar el usuario
            Usuario updatedUsuario = usuarioService.actualizarUsuario(usuario);
            
            logger.info("✅ Usuario actualizado exitosamente: {}", updatedUsuario.getId());
            
            return ResponseEntity.ok(Map.of(
                "message", "Usuario actualizado exitosamente",
                "usuario", buildUserResponse(updatedUsuario)
            ));
            
        } catch (Exception e) {
            logger.error("❌ Error actualizando usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor", "message", e.getMessage()));
        }
    }
    
    /**
     * 📝 Registro de nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            logger.info("📝 Solicitud de registro para: {}", usuario.getCorreoInstitucional());
            
            // 🔧 VALIDACIONES MEJORADAS
            Map<String, String> validationErrors = validateRegistrationData(usuario);
            if (!validationErrors.isEmpty()) {
                logger.warn("❌ Errores de validación: {}", validationErrors);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Errores de validación",
                    "details", validationErrors
                ));
            }
            
            // 🔧 VALIDAR CARRERA EXISTE Y ESTÁ ACTIVA
            if (usuario.getCarreraId() != null) {
                Carrera carrera = carreraService.findById(usuario.getCarreraId());
                if (carrera == null || !carrera.getActivo()) {
                    logger.warn("❌ Carrera no válida: {}", usuario.getCarreraId());
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Carrera no válida",
                        "message", "La carrera seleccionada no existe o no está activa"
                    ));
                }
                logger.info("✅ Carrera validada: {} - {}", carrera.getId(), carrera.getNombre());
            }
            
            // 🔧 ESTABLECER VALORES POR DEFECTO
            setupDefaultValues(usuario);
            
            // Registrar usuario
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            logger.info("✅ Usuario registrado: {} (ID: {})", 
                       usuarioRegistrado.getCorreoInstitucional(), usuarioRegistrado.getId());
            
            // 🔧 RESPUESTA COMPLETA
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuario", buildUserResponse(usuarioRegistrado));
            
            // Agregar información de la carrera
            if (usuarioRegistrado.getCarreraId() != null) {
                Carrera carrera = carreraService.findById(usuarioRegistrado.getCarreraId());
                if (carrera != null) {
                    response.put("carrera", Map.of(
                        "id", carrera.getId(),
                        "nombre", carrera.getNombre(),
                        "codigo", carrera.getCodigo() != null ? carrera.getCodigo() : ""
                    ));
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("❌ Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error de validación",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("❌ Error interno en registro: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Error interno del servidor",
                "message", "Ocurrió un error al procesar el registro. Por favor, intenta nuevamente."
            ));
        }
    }

    /**
     * 🔐 Login legacy (DEPRECATED - usar /api/auth/login)
     */
    @PostMapping("/login")
    @Deprecated
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        logger.warn("⚠️ Usando endpoint de login deprecated. Usar /api/auth/login");
        
        if (usuario.getCorreoInstitucional() == null || usuario.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Credenciales requeridas",
                "authenticated", false
            ));
        }
        
        boolean isAuthenticated = usuarioService.autenticarUsuario(
            usuario.getCorreoInstitucional(), 
            usuario.getPassword()
        );
        
        return ResponseEntity.ok(Map.of(
            "authenticated", isAuthenticated,
            "message", isAuthenticated ? "Autenticación exitosa" : "Credenciales inválidas",
            "deprecated", true,
            "useInstead", "/api/auth/login"
        ));
    }

    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * 📋 Construye respuesta de usuario consistente
     */
    private Map<String, Object> buildUserResponse(Usuario usuario) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("nombre", usuario.getNombre());
        response.put("apellidos", usuario.getApellidos());
        response.put("correoInstitucional", usuario.getCorreoInstitucional());
        
        // 🔧 FIX: Usar enum correctamente
        response.put("rol", usuario.getRol() != null ? usuario.getRol().getValor() : "estudiante");
        
        // 🔧 FIX: Usar cicloActual de la BD
        response.put("cicloActual", usuario.getCicloActual());
        
        response.put("departamentoId", usuario.getDepartamentoId());
        response.put("carreraId", usuario.getCarreraId());
        response.put("seccionId", usuario.getSeccionId());
        response.put("telefono", usuario.getTelefono());
        response.put("profileImageUrl", usuario.getProfileImageUrl());
        
        // Información adicional
        response.put("requiresCompletion", usuario.requiereCompletarDatos());
        response.put("createdAt", usuario.getCreatedAt());
        response.put("updatedAt", usuario.getUpdatedAt());
        
        return response;
    }
    
    /**
     * ✅ Valida datos de registro
     */
    private Map<String, String> validateRegistrationData(Usuario usuario) {
        Map<String, String> errors = new HashMap<>();
        
        // Campos obligatorios
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            errors.put("nombre", "El nombre es requerido");
        }
        
        if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
            errors.put("apellidos", "Los apellidos son requeridos");
        }
        
        if (usuario.getCorreoInstitucional() == null || usuario.getCorreoInstitucional().trim().isEmpty()) {
            errors.put("correoInstitucional", "El correo institucional es requerido");
        } else if (!usuario.getCorreoInstitucional().endsWith("@tecsup.edu.pe")) {
            errors.put("correoInstitucional", "Debe usar un correo institucional (@tecsup.edu.pe)");
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            errors.put("password", "La contraseña es requerida");
        } else if (usuario.getPassword().length() < 6) {
            errors.put("password", "La contraseña debe tener al menos 6 caracteres");
        }
        
        // 🔧 FIX: Validar cicloActual como Integer
        if (usuario.getCicloActual() == null) {
            errors.put("cicloActual", "El ciclo es requerido");
        } else if (usuario.getCicloActual() < 1 || usuario.getCicloActual() > 6) {
            errors.put("cicloActual", "El ciclo debe ser un número entre 1 y 6");
        }
        
        if (usuario.getCarreraId() == null) {
            errors.put("carreraId", "Debe seleccionar una carrera");
        }
        
        return errors;
    }
    
    /**
     * ✅ Valida datos de actualización
     */
    private Map<String, String> validateUpdateData(Usuario usuario) {
        Map<String, String> errors = new HashMap<>();
        
        // Solo validar campos que se pueden actualizar
        if (usuario.getNombre() != null && usuario.getNombre().trim().isEmpty()) {
            errors.put("nombre", "El nombre no puede estar vacío");
        }
        
        if (usuario.getApellidos() != null && usuario.getApellidos().trim().isEmpty()) {
            errors.put("apellidos", "Los apellidos no pueden estar vacíos");
        }
        
        if (usuario.getCicloActual() != null && (usuario.getCicloActual() < 1 || usuario.getCicloActual() > 6)) {
            errors.put("cicloActual", "El ciclo debe ser un número entre 1 y 6");
        }
        
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty() && usuario.getPassword().length() < 6) {
            errors.put("password", "La contraseña debe tener al menos 6 caracteres");
        }
        
        return errors;
    }
    
    /**
     * 🔧 Establece valores por defecto para registro
     */
    private void setupDefaultValues(Usuario usuario) {
        // Limpiar y normalizar datos
        if (usuario.getNombre() != null) {
            usuario.setNombre(usuario.getNombre().trim());
        }
        if (usuario.getApellidos() != null) {
            usuario.setApellidos(usuario.getApellidos().trim());
        }
        if (usuario.getCorreoInstitucional() != null) {
            usuario.setCorreoInstitucional(usuario.getCorreoInstitucional().trim().toLowerCase());
        }
        
        // Establecer valores por defecto
        if (usuario.getRol() == null) {
            usuario.setRol(Usuario.RolUsuario.ESTUDIANTE);
        }
        
        // Departamento por defecto (Tecnología Digital)
        if (usuario.getDepartamentoId() == null) {
            usuario.setDepartamentoId(1L);
        }
        
        // Campos opcionales inicialmente null
        if (usuario.getSeccionId() == null) {
            usuario.setSeccionId(null);
        }
        if (usuario.getTelefono() == null) {
            usuario.setTelefono(null);
        }
        
        logger.debug("✅ Valores por defecto establecidos para: {}", usuario.getCorreoInstitucional());
    }
}