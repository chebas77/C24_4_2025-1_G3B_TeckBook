// Usuario/backend/backend/src/main/java/com/usuario/backend/controller/user/UsuarioController.java
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
    private CarreraService carreraService; // 🔄 NUEVO: Inyectar servicio de carreras
    
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
        
        String username = userDetails.getUsername();
        Usuario usuario = usuarioService.findByCorreoInstitucional(username);
        
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario, @AuthenticationPrincipal UserDetails userDetails) {
        // Verificar autenticación
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
        
        // Verificar que el usuario autenticado sea el mismo que se está actualizando
        Usuario existingUsuario = usuarioService.findById(id);
        if (existingUsuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        
        if (!existingUsuario.getCorreoInstitucional().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar este usuario");
        }
        
        // Proteger campos críticos
        usuario.setId(id);  // Asegurar que el ID sea el mismo
        usuario.setCorreoInstitucional(existingUsuario.getCorreoInstitucional());  // No permitir cambiar el correo
        usuario.setRol(existingUsuario.getRol());  // No permitir cambiar el rol
        
        // Actualizar el usuario
        Usuario updatedUsuario = usuarioService.actualizarUsuario(usuario);
        return ResponseEntity.ok(updatedUsuario);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        try {
            logger.info("🔄 NUEVO REGISTRO: Solicitud de registro recibida para: {}", usuario.getCorreoInstitucional());
            
            // 🔄 VALIDACIONES MEJORADAS
            Map<String, String> validationErrors = validateRegistrationData(usuario);
            if (!validationErrors.isEmpty()) {
                logger.warn("Errores de validación en registro: {}", validationErrors);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Errores de validación",
                    "details", validationErrors
                ));
            }
            
            // 🔄 VALIDAR QUE LA CARRERA EXISTE
            if (usuario.getCarreraId() != null) {
                Carrera carrera = carreraService.findById(usuario.getCarreraId());
                if (carrera == null || !carrera.getActivo()) {
                    logger.warn("Carrera no válida seleccionada: {}", usuario.getCarreraId());
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Carrera no válida",
                        "message", "La carrera seleccionada no existe o no está activa"
                    ));
                }
                logger.info("Carrera validada: {} - {}", carrera.getId(), carrera.getNombre());
            }
            
            // 🔄 ESTABLECER VALORES POR DEFECTO MEJORADOS
            setupDefaultValues(usuario);
            
            // Registrar usuario
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            logger.info("✅ Usuario registrado exitosamente: {} (ID: {})", 
                       usuarioRegistrado.getCorreoInstitucional(), usuarioRegistrado.getId());
            
            // 🔄 RESPUESTA MEJORADA CON INFORMACIÓN DE LA CARRERA
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuario", Map.of(
                "id", usuarioRegistrado.getId(),
                "nombre", usuarioRegistrado.getNombre(),
                "apellidos", usuarioRegistrado.getApellidos(),
                "correoInstitucional", usuarioRegistrado.getCorreoInstitucional(),
                "codigo", usuarioRegistrado.getCodigo(),
                "ciclo", usuarioRegistrado.getCiclo(),
                "rol", usuarioRegistrado.getRol(),
                "carreraId", usuarioRegistrado.getCarreraId()
            ));
            
            // Agregar información de la carrera si existe
            if (usuario.getCarreraId() != null) {
                Carrera carrera = carreraService.findById(usuario.getCarreraId());
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
            logger.warn("Error de validación en registro: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error de validación",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Error interno en registro: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Error interno del servidor",
                "message", "Ocurrió un error al procesar el registro. Por favor, intenta nuevamente."
            ));
        }
    }
    
    /**
     * 🔄 NUEVO: Método para validar datos de registro
     */
    private Map<String, String> validateRegistrationData(Usuario usuario) {
    Map<String, String> errors = new HashMap<>();
    
    // Validar campos requeridos
    if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
        errors.put("nombre", "El nombre es requerido");
    }
    
    if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
        errors.put("apellidos", "Los apellidos son requeridos");
    }
    
    if (usuario.getCodigo() == null || usuario.getCodigo().trim().isEmpty()) {
        errors.put("codigo", "El código de estudiante es requerido");
    }
    
    if (usuario.getCorreoInstitucional() == null || usuario.getCorreoInstitucional().trim().isEmpty()) {
        errors.put("correoInstitucional", "El correo institucional es requerido");
    } else if (!usuario.getCorreoInstitucional().endsWith("@tecsup.edu.pe")) {
        errors.put("correoInstitucional", "Debe usar un correo institucional (@tecsup.edu.pe)");
    }
    
    // 🔥 FIX: Cambiar de "contrasena" a "password"
    if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
        errors.put("password", "La contraseña es requerida");
    } else if (usuario.getPassword().length() < 6) {
        errors.put("password", "La contraseña debe tener al menos 6 caracteres");
    }
    
    if (usuario.getCiclo() == null || usuario.getCiclo().trim().isEmpty()) {
        errors.put("ciclo", "El ciclo es requerido");
    } else {
        try {
            int cicloNum = Integer.parseInt(usuario.getCiclo());
            if (cicloNum < 1 || cicloNum > 6) {
                errors.put("ciclo", "El ciclo debe ser un número entre 1 y 6");
            }
        } catch (NumberFormatException e) {
            errors.put("ciclo", "El ciclo debe ser un número válido");
        }
    }
    
    if (usuario.getCarreraId() == null) {
        errors.put("carreraId", "Debe seleccionar una carrera");
    }
    
    return errors;
}
    
    /**
     * 🔄 NUEVO: Método para establecer valores por defecto
     */
    private void setupDefaultValues(Usuario usuario) {
        // Limpiar y normalizar datos
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setApellidos(usuario.getApellidos().trim());
        usuario.setCodigo(usuario.getCodigo().trim());
        usuario.setCorreoInstitucional(usuario.getCorreoInstitucional().trim().toLowerCase());
        usuario.setCiclo(usuario.getCiclo().trim());
        
        // Establecer valores por defecto
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            usuario.setRol("ESTUDIANTE");
        }
        
        // Departamento por defecto (Tecnología Digital)
        if (usuario.getDepartamentoId() == null) {
            usuario.setDepartamentoId(1L);
        }
        
        // Campos que se mantendrán nulos por ahora (asignados por admin)
        usuario.setSeccionId(null);
        usuario.setTelefono(null);
        usuario.setDireccion(null);
        usuario.setFechaNacimiento(null);
        usuario.setProfileImageUrl(null);
        
        logger.debug("Valores por defecto establecidos para usuario: {}", usuario.getCorreoInstitucional());
    }

    @PostMapping("/login")
    public boolean login(@RequestBody Usuario usuario) {
        System.out.println("Login recibido: correo_institucional=" + usuario.getCorreoInstitucional() + ", password=" + usuario.getPassword());
        if (usuario.getCorreoInstitucional() == null || usuario.getPassword() == null) {
            return false;
        }
        return usuarioService.autenticarUsuario(usuario.getCorreoInstitucional(), usuario.getPassword());
    }
}