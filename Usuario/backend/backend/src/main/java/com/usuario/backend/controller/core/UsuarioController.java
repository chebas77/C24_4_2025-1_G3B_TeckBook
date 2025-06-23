package com.usuario.backend.controller.core;

import com.usuario.backend.model.entity.core.Usuario;
import com.usuario.backend.service.core.UsuarioService;
import com.usuario.backend.service.core.CarreraService;
import com.usuario.backend.model.entity.core.Carrera;

import jakarta.validation.Valid;
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
     * 👤 Obtener perfil del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no autenticado"));
        }
        
        Usuario usuario = usuarioService.findByEmail(userDetails.getUsername());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        
        return ResponseEntity.ok(buildUserResponse(usuario));
    }
    
    /**
     * 🔍 Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ID inválido"));
        }
        
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        
        return ResponseEntity.ok(buildUserResponse(usuario));
    }
    
    /**
     * ✏️ Actualizar usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody Usuario usuario, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            
            Usuario existingUsuario = usuarioService.findById(id);
            if (existingUsuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
            
            // Solo puede actualizar su propio perfil
            if (!existingUsuario.getEmail().equals(userDetails.getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Sin permisos para actualizar este usuario"));
            }
            
            // Proteger campos críticos
            usuario.setId(id);
            usuario.setEmail(existingUsuario.getEmail());
            usuario.setRol(existingUsuario.getRol());
            
            Usuario updated = usuarioService.actualizarUsuario(usuario);
            
            return ResponseEntity.ok(Map.of(
                "message", "Usuario actualizado exitosamente",
                "usuario", buildUserResponse(updated)
            ));
            
        } catch (Exception e) {
            logger.error("❌ Error actualizando usuario {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * 📝 Registrar nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Usuario usuario) {
        try {
            logger.info("📝 Registro para: {}", usuario.getEmail());
            
            // Validar carrera si se proporciona
            if (usuario.getCarreraId() != null) {
                Carrera carrera = carreraService.findById(usuario.getCarreraId());
                if (carrera == null || !carrera.getActivo()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Carrera no válida",
                        "message", "La carrera seleccionada no existe o no está activa"
                    ));
                }
            }
            
            // Establecer valores por defecto
            if (usuario.getDepartamentoId() == null) {
                usuario.setDepartamentoId(1L); // Tecnología Digital
            }
            
            Usuario registrado = usuarioService.registrarUsuario(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuario", buildUserResponse(registrado));
            
            // Información de carrera si existe
            if (registrado.getCarreraId() != null) {
                Carrera carrera = carreraService.findById(registrado.getCarreraId());
                if (carrera != null) {
                    response.put("carrera", Map.of(
                        "id", carrera.getId(),
                        "nombre", carrera.getNombre()
                    ));
                }
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error de validación",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("❌ Error en registro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Error interno del servidor"
            ));
        }
    }

    /**
     * 🔐 Login legacy (usar /api/auth/login)
     */
    @PostMapping("/login")
    @Deprecated
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Email y contraseña requeridos"
            ));
        }
        
        boolean isAuthenticated = usuarioService.autenticarUsuario(email, password);
        
        return ResponseEntity.ok(Map.of(
            "authenticated", isAuthenticated,
            "message", isAuthenticated ? "Autenticación exitosa" : "Credenciales inválidas",
            "useInstead", "/api/auth/login"
        ));
    }

    // === MÉTODOS AUXILIARES ===
    
    private Map<String, Object> buildUserResponse(Usuario usuario) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("nombres", usuario.getNombres());
        response.put("apellido", usuario.getApellido());
        response.put("email", usuario.getEmail());
        response.put("rol", usuario.getRol().getValor());
        response.put("carreraId", usuario.getCarreraId());
        response.put("departamentoId", usuario.getDepartamentoId());
        response.put("telefono", usuario.getTelefono());
        response.put("profileImageUrl", usuario.getProfileImageUrl());
        response.put("activo", usuario.getActivo());
        response.put("requiresCompletion", usuario.requiereCompletarDatos());
        response.put("createdAt", usuario.getCreatedAt());
        response.put("updatedAt", usuario.getUpdatedAt());
        return response;
    }
}