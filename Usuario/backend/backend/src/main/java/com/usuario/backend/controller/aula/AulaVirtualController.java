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
     * - Estudiantes: Todas las aulas activas (temporal hasta implementar inscripciones)
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
                // 👨‍🎓 ESTUDIANTES: Todas las aulas activas (temporal)
                aulas = aulaVirtualService.getAllAulas();
                mensaje = "Aulas disponibles para estudiantes";
                logger.info("Estudiante {} puede ver {} aulas activas", email, aulas.size());
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
            
            // 🔧 ASIGNAR SECCIÓN DEL PROFESOR SI TIENE
            if (usuario.getSeccionId() != null) {
                aula.setSeccionId(usuario.getSeccionId());
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

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al crear aula: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error de validación", "message", e.getMessage()));
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
                // 🔧 TEMPORAL: Estudiantes pueden ver todas las aulas activas
                // En el futuro: verificar inscripción real
                tieneAcceso = "activa".equals(aula.getEstado());
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
            aulaActualizada.setCodigoAcceso(aulaExistente.getCodigoAcceso()); // Mantener código original

            AulaVirtual aulaGuardada = aulaVirtualService.actualizarAula(aulaActualizada);
            
            Map<String, Object> response = new HashMap<>();
            response.put("aula", aulaGuardada);
            response.put("mensaje", "Aula actualizada exitosamente");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al actualizar aula: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error de validación", "message", e.getMessage()));
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
            
            // 🔧 OBTENER ESTADÍSTICAS GENERALES DEL SERVICIO
            Map<String, Object> estadisticasGenerales = aulaVirtualService.getEstadisticasGenerales();

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalAulas", aulas.size());
            estadisticas.put("aulasActivas", aulasActivas);
            estadisticas.put("aulasInactivas", aulasInactivas);
            estadisticas.put("totalEstudiantes", 25); // Temporal hasta implementar inscripciones
            estadisticas.put("profesor", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre() + " " + usuario.getApellidos(),
                "departamento", usuario.getDepartamentoId() != null ? usuario.getDepartamentoId() : "Sin asignar"
            ));
            estadisticas.put("estadisticasGenerales", estadisticasGenerales);

            return ResponseEntity.ok(estadisticas);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener estadísticas", "message", e.getMessage()));
        }
    }

    /**
     * 🔍 BUSCAR AULAS POR CÓDIGO DE ACCESO
     */
    @GetMapping("/codigo/{codigoAcceso}")
    public ResponseEntity<?> getAulaByCodigoAcceso(@PathVariable String codigoAcceso,
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

            AulaVirtual aula = aulaVirtualService.findByCodigoAcceso(codigoAcceso);
            if (aula == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Aula no encontrada con ese código"));
            }

            // 🔧 VERIFICAR QUE EL AULA ESTÉ ACTIVA
            if (!"activa".equals(aula.getEstado())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "El aula no está activa"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("aula", aula);
            response.put("mensaje", "Aula encontrada");
            response.put("puedeAcceder", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al buscar aula por código {}: {}", codigoAcceso, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar el aula", "message", e.getMessage()));
        }
    }

    /**
     * 🔍 BUSCAR AULAS POR NOMBRE
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarAulas(@RequestParam String nombre,
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

            List<AulaVirtual> aulas;
            if ("PROFESOR".equals(usuario.getRol())) {
                // Profesor: buscar solo en sus aulas
                aulas = aulaVirtualService.searchAulasByNombre(nombre, usuario.getId());
            } else {
                // Estudiante: buscar en todas las aulas activas
                aulas = aulaVirtualService.searchAulasByNombre(nombre, null);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("aulas", aulas);
            response.put("totalEncontradas", aulas.size());
            response.put("terminoBusqueda", nombre);
            response.put("rolUsuario", usuario.getRol());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al buscar aulas por nombre '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar aulas", "message", e.getMessage()));
        }
    }
}