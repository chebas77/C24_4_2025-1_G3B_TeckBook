// Usuario/backend/backend/src/main/java/com/usuario/backend/controller/aula/AulaVirtualController.java
package com.usuario.backend.controller.aula;

import com.usuario.backend.model.entity.AulaVirtual;
import com.usuario.backend.service.aula.AulaVirtualService;
import com.usuario.backend.service.user.UsuarioService;
import com.usuario.backend.model.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/aulas")
@CrossOrigin(origins = "*")
public class AulaVirtualController {

    private static final Logger logger = LoggerFactory.getLogger(AulaVirtualController.class);

    @Autowired
    private AulaVirtualService aulaService;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<?> getAllAulas(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
            }

            List<AulaVirtual> aulas;
            
            // Si es profesor, mostrar sus aulas. Si es estudiante, mostrar todas las activas
            if ("PROFESOR".equals(usuario.getRol())) {
                aulas = aulaService.getAulasByProfesor(usuario.getId());
            } else {
                aulas = aulaService.getAulasActivas();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("aulas", aulas);
            response.put("totalAulas", aulas.size());
            response.put("rol", usuario.getRol());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener aulas: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearAula(@RequestBody AulaVirtual aula, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            logger.info("🆕 Solicitud para crear aula: {}", aula.getNombre());
            
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
            }

            logger.info("👤 Usuario encontrado: {} (ID: {})", email, usuario.getId());

            // 🔧 ASIGNAR EL PROFESOR ID AL AULA
            aula.setProfesorId(usuario.getId());
            logger.info("✅ Profesor ID asignado: {}", usuario.getId());
            
            AulaVirtual aulaCreada = aulaService.crearAula(aula);
            logger.info("🎉 Aula creada exitosamente: ID {}", aulaCreada.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Aula creada exitosamente");
            response.put("aula", aulaCreada);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al crear aula: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error al crear el aula: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAulaById(@PathVariable Long id) {
        try {
            AulaVirtual aula = aulaService.findById(id);
            
            if (aula == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Aula no encontrada"));
            }
            
            return ResponseEntity.ok(aula);
            
        } catch (Exception e) {
            logger.error("Error al obtener aula {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error al obtener el aula"));
        }
    }
}