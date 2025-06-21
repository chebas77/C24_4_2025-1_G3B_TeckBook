package com.usuario.backend.controller.aula;

import com.usuario.backend.model.entity.AulaVirtual;
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
     * 🎯 OBTENER AULAS FILTRADAS POR ROL
     * - Profesores: Solo sus aulas creadas
     * - Estudiantes: Solo aulas donde están inscritos
     */
    @GetMapping
    public ResponseEntity<?> getAulasByUserRole(@AuthenticationPrincipal UserDetails userDetails) {
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

            logger.info("Obteniendo aulas para usuario: {} con rol: {}", email, usuario.getRol());

            List<AulaVirtual> aulas;
            String mensaje;

            if ("PROFESOR".equals(usuario.getRol())) {
                // 🎓 PROFESORES: Solo sus aulas creadas
                aulas = aulaVirtualService.getAulasByProfesor(usuario.getId());
                mensaje = "Aulas creadas por el profesor";
                logger.info("Profesor {} tiene {} aulas creadas", email, aulas.size());
            } else {
                // 👨‍🎓 ESTUDIANTES: Solo aulas donde están inscritos
                aulas = aulaVirtualService.getAulasByEstudiante(usuario.getId());
                mensaje = "Aulas donde el estudiante está inscrito";
                logger.info("Estudiante {} está inscrito en {} aulas", email, aulas.size());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("aulas", aulas);
            response.put("totalAulas", aulas.size());
            response.put("rol", usuario.getRol());
            response.put("mensaje", mensaje);
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellidos", usuario.getApellidos(),
                "rol", usuario.getRol()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener aulas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener las aulas", "message", e.getMessage()));
        }
    }

    /**
     * 🆕 CREAR AULA (Solo profesores)
     */
    @PostMapping
    public ResponseEntity<?> crearAula(@RequestBody AulaVirtual aula, 
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

            // 🔒 VALIDAR QUE SEA PROFESOR
            if (!"PROFESOR".equals(usuario.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo los profesores pueden crear aulas"));
            }

            logger.info("Creando aula '{}' para profesor: {}", aula.getNombre(), email);

            // 🎯 ASIGNAR AUTOMÁTICAMENTE EL PROFESOR
            aula.setProfesorId(usuario.getId());
            
            // 🔧 VALIDAR DATOS ACADÉMICOS DEL PROFESOR
            if (usuario.getDepartamentoId() != null) {
                // El profesor debe crear aulas dentro de su departamento (opcional)
                logger.info("Profesor pertenece al departamento: {}", usuario.getDepartamentoId());
            }

            AulaVirtual aulaCreada = aulaVirtualService.crearAula(aula);
            
            Map<String, Object> response = new HashMap<>();
            response.put("aula", aulaCreada);
            response.put("mensaje", "Aula creada exitosamente");
            response.put("profesor", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre() + " " + usuario.getApellidos()
            ));

            logger.info("Aula '{}' creada exitosamente con ID: {}", aulaCreada.getNombre(), aulaCreada.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error al crear aula: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el aula", "message", e.getMessage()));
        }
    }

    /**
     * 🎯 OBTENER AULA POR ID (Con verificación de permisos)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAulaById(@PathVariable Long id, 
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

            AulaVirtual aula = aulaVirtualService.findById(id);
            if (aula == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Aula no encontrada"));
            }

            // 🔒 VERIFICAR PERMISOS
            boolean tieneAcceso = false;
            if ("PROFESOR".equals(usuario.getRol())) {
                // Profesor: Solo puede ver sus propias aulas
                tieneAcceso = aula.getProfesorId().equals(usuario.getId());
            } else {
                // Estudiante: Solo puede ver aulas donde está inscrito
                tieneAcceso = aulaVirtualService.isEstudianteInscritoEnAula(usuario.getId(), id);
            }

            if (!tieneAcceso) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permisos para acceder a esta aula"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("aula", aula);
            response.put("tieneAcceso", true);
            response.put("rolUsuario", usuario.getRol());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener aula por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el aula", "message", e.getMessage()));
        }
    }

    /**
     * 🔄 ACTUALIZAR AULA (Solo el profesor propietario)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAula(@PathVariable Long id, 
                                            @RequestBody AulaVirtual aulaActualizada,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null || !"PROFESOR".equals(usuario.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo los profesores pueden actualizar aulas"));
            }

            AulaVirtual aulaExistente = aulaVirtualService.findById(id);
            if (aulaExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Aula no encontrada"));
            }

            // 🔒 VERIFICAR QUE SEA EL PROFESOR PROPIETARIO
            if (!aulaExistente.getProfesorId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo puedes actualizar tus propias aulas"));
            }

            // 🔧 MANTENER DATOS CRÍTICOS
            aulaActualizada.setId(id);
            aulaActualizada.setProfesorId(usuario.getId());

            AulaVirtual aulaGuardada = aulaVirtualService.actualizarAula(aulaActualizada);
            
            Map<String, Object> response = new HashMap<>();
            response.put("aula", aulaGuardada);
            response.put("mensaje", "Aula actualizada exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al actualizar aula {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar el aula", "message", e.getMessage()));
        }
    }

    /**
     * 🗑️ ELIMINAR AULA (Solo el profesor propietario)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAula(@PathVariable Long id, 
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null || !"PROFESOR".equals(usuario.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo los profesores pueden eliminar aulas"));
            }

            AulaVirtual aula = aulaVirtualService.findById(id);
            if (aula == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Aula no encontrada"));
            }

            // 🔒 VERIFICAR QUE SEA EL PROFESOR PROPIETARIO
            if (!aula.getProfesorId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo puedes eliminar tus propias aulas"));
            }

            aulaVirtualService.eliminarAula(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Aula eliminada exitosamente");
            response.put("aulaId", id);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al eliminar aula {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar el aula", "message", e.getMessage()));
        }
    }

    /**
     * 📊 ESTADÍSTICAS DEL PROFESOR
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> getEstadisticasProfesor(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null || !"PROFESOR".equals(usuario.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo disponible para profesores"));
            }

            List<AulaVirtual> aulas = aulaVirtualService.getAulasByProfesor(usuario.getId());
            
            // 📊 CALCULAR ESTADÍSTICAS
            long aulasActivas = aulas.stream().filter(a -> "activa".equals(a.getEstado())).count();
            long aulasInactivas = aulas.stream().filter(a -> "inactiva".equals(a.getEstado())).count();
            int totalEstudiantes = aulaVirtualService.getTotalEstudiantesByProfesor(usuario.getId());

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalAulas", aulas.size());
            estadisticas.put("aulasActivas", aulasActivas);
            estadisticas.put("aulasInactivas", aulasInactivas);
            estadisticas.put("totalEstudiantes", totalEstudiantes);
            estadisticas.put("profesor", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre() + " " + usuario.getApellidos(),
                "departamento", usuario.getDepartamentoId()
            ));

            return ResponseEntity.ok(estadisticas);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener estadísticas", "message", e.getMessage()));
        }
    }
}