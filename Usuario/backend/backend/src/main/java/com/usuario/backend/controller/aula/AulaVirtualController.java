package com.usuario.backend.controller.aula;

import com.usuario.backend.model.entity.AulaVirtual;
import com.usuario.backend.model.entity.AulaEstudiante;
import com.usuario.backend.model.entity.Usuario;
import com.usuario.backend.service.aula.AulaVirtualService;
import com.usuario.backend.service.user.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aulas")
@CrossOrigin(origins = "*")
public class AulaVirtualController {

    private static final Logger logger = LoggerFactory.getLogger(AulaVirtualController.class);

    @Autowired
    private AulaVirtualService aulaVirtualService;
    
    @Autowired
    private UsuarioService usuarioService;

    /**
     * 🔥 ENDPOINT PRINCIPAL: Obtiene aulas del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<?> getAulasDelUsuario(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            logger.info("Obteniendo aulas para usuario: {} ({})", email, usuario.getRol());

            // 🔥 OBTENER AULAS SEGÚN ROL
            String rolString = usuario.getRol().toString(); // Convertir enum a String
            List<AulaVirtual> aulas = aulaVirtualService.getAulasByUsuario(usuario.getId(), rolString);

            Map<String, Object> response = new HashMap<>();
            response.put("aulas", aulas);
            response.put("totalAulas", aulas.size());
            response.put("rol", rolString);
            response.put("usuarioId", usuario.getId());
            response.put("message", aulas.isEmpty() 
                ? ("PROFESOR".equals(rolString) ? "No has creado aulas aún" : "No estás inscrito en ningún aula")
                : "Aulas obtenidas exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener aulas del usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener las aulas", "message", e.getMessage()));
        }
    }

    /**
     * 🔥 OBTENER DETALLES DE UN AULA ESPECÍFICA
     */
    @GetMapping("/{aulaId}")
    public ResponseEntity<?> getAulaById(@PathVariable Long aulaId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            // Verificar si el usuario puede acceder al aula
            String rolString = usuario.getRol().toString();
            boolean puedeAcceder = aulaVirtualService.puedeAccederAAula(usuario.getId(), rolString, aulaId);
            if (!puedeAcceder) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes acceso a esta aula"));
            }

            AulaVirtual aula = aulaVirtualService.findById(aulaId);
            if (aula == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Aula no encontrada"));
            }

            // Obtener estudiantes del aula
            List<AulaEstudiante> estudiantes = aulaVirtualService.getEstudiantesDeAula(aulaId);
            long totalEstudiantes = aulaVirtualService.contarEstudiantesEnAula(aulaId);

            Map<String, Object> response = new HashMap<>();
            response.put("aula", aula);
            response.put("estudiantes", estudiantes);
            response.put("totalEstudiantes", totalEstudiantes);
            response.put("esProfesor", "PROFESOR".equals(rolString));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener aula {}: {}", aulaId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el aula", "message", e.getMessage()));
        }
    }

    /**
     * 🔥 AGREGAR ESTUDIANTE A AULA (Solo profesores)
     */
    @PostMapping("/{aulaId}/estudiantes/{estudianteId}")
    public ResponseEntity<?> agregarEstudianteAAula(
            @PathVariable Long aulaId, 
            @PathVariable Long estudianteId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            // Verificar que es profesor
            String rolString = usuario.getRol().toString();
            if (!"PROFESOR".equals(rolString)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo los profesores pueden agregar estudiantes"));
            }

            // Verificar que es el profesor del aula
            boolean esProfesorDelAula = aulaVirtualService.puedeAccederAAula(usuario.getId(), rolString, aulaId);
            if (!esProfesorDelAula) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permisos para modificar esta aula"));
            }

            // Verificar que el estudiante existe
            Usuario estudiante = usuarioService.findById(estudianteId);
            if (estudiante == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Estudiante no encontrado"));
            }

            if (!"ESTUDIANTE".equals(estudiante.getRol().toString())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El usuario debe tener rol de ESTUDIANTE"));
            }

            // Agregar estudiante al aula
            aulaVirtualService.agregarEstudianteAAula(aulaId, estudianteId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Estudiante agregado exitosamente al aula");
            response.put("aulaId", aulaId);
            response.put("estudianteId", estudianteId);
            response.put("estudianteNombre", estudiante.getNombre() + " " + estudiante.getApellidos());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al agregar estudiante {} al aula {}: {}", estudianteId, aulaId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al agregar estudiante al aula", "message", e.getMessage()));
        }
    }

    /**
     * 🔥 BUSCAR AULAS POR NOMBRE
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarAulas(
            @RequestParam String nombre,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El parámetro 'nombre' es requerido"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            String rolString = usuario.getRol().toString();
            List<AulaVirtual> aulas = aulaVirtualService.buscarAulasPorNombre(usuario.getId(), rolString, nombre.trim());

            Map<String, Object> response = new HashMap<>();
            response.put("aulas", aulas);
            response.put("totalResultados", aulas.size());
            response.put("terminoBusqueda", nombre.trim());
            response.put("message", aulas.isEmpty() ? "No se encontraron aulas" : "Búsqueda completada");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al buscar aulas por nombre '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar aulas", "message", e.getMessage()));
        }
    }

    /**
     * 🔥 ENDPOINT DE SALUD
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "AulaVirtualController",
            "status", "OK",
            "timestamp", System.currentTimeMillis(),
            "endpoints", Map.of(
                "getAulas", "GET /api/aulas",
                "getAulaById", "GET /api/aulas/{aulaId}",
                "agregarEstudiante", "POST /api/aulas/{aulaId}/estudiantes/{estudianteId}",
                "buscarAulas", "GET /api/aulas/buscar?nombre={nombre}"
            )
        ));
    }
}