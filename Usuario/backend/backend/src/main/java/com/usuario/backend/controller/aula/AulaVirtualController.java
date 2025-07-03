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

import java.security.Principal;
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

            // Enriquecer cada aula con el nombre completo del profesor
            List<Map<String, Object>> aulasConProfesor = aulas.stream().map(aula -> {
                Map<String, Object> aulaMap = new HashMap<>();
                aulaMap.put("id", aula.getId());
                aulaMap.put("nombre", aula.getNombre());
                aulaMap.put("titulo", aula.getTitulo());
                aulaMap.put("descripcion", aula.getDescripcion());
                aulaMap.put("codigoAcceso", aula.getCodigoAcceso());
                aulaMap.put("profesorId", aula.getProfesorId());
                aulaMap.put("seccionId", aula.getSeccionId());
                aulaMap.put("estado", aula.getEstado());
                aulaMap.put("fechaInicio", aula.getFechaInicio());
                aulaMap.put("fechaFin", aula.getFechaFin());
                aulaMap.put("createdAt", aula.getCreatedAt());
                aulaMap.put("updatedAt", aula.getUpdatedAt());
                // Buscar el profesor y agregar su nombre completo
                Usuario profesor = usuarioService.findById(aula.getProfesorId());
                if (profesor != null) {
                    String nombreCompleto = profesor.getNombre() + " " + profesor.getApellidos();
                    aulaMap.put("profesorNombreCompleto", nombreCompleto);
                } else {
                    aulaMap.put("profesorNombreCompleto", "Profesor desconocido");
                }
                return aulaMap;
            }).toList();

            Map<String, Object> response = new HashMap<>();
            response.put("aulas", aulasConProfesor);
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
     * Listar integrantes (participantes) activos de un aula
     */
    @GetMapping("/{aulaId}/participantes")
    public ResponseEntity<?> listarParticipantesAula(@PathVariable Long aulaId, @AuthenticationPrincipal UserDetails userDetails) {
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
            // Solo participantes pueden ver la lista
            String rolString = usuario.getRol().toString();
            boolean puedeAcceder = aulaVirtualService.puedeAccederAAula(usuario.getId(), rolString, aulaId);
            if (!puedeAcceder) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes acceso a esta aula"));
            }
            // Obtener lista de participantes activos y enriquecer con datos del usuario
            List<AulaEstudiante> participantes = aulaVirtualService.getEstudiantesDeAula(aulaId);
            List<Map<String, Object>> participantesConDatos = participantes.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("aulaId", p.getAulaId());
                map.put("estudianteId", p.getEstudianteId());
                map.put("estado", p.getEstado());
                map.put("fechaUnion", p.getFechaUnion());
                map.put("fechaSalida", p.getFechaSalida());
                // Buscar datos del estudiante
                Usuario est = usuarioService.findById(p.getEstudianteId());
                if (est != null) {
                    map.put("nombre", est.getNombre());
                    map.put("apellidos", est.getApellidos());
                    map.put("email", est.getCorreoInstitucional());
                } else {
                    map.put("nombre", "Sin nombre");
                    map.put("apellidos", "");
                    map.put("email", "");
                }
                return map;
            }).toList();
            return ResponseEntity.ok(Map.of("participantes", participantesConDatos));
        } catch (Exception e) {
            logger.error("Error al listar participantes del aula {}: {}", aulaId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar participantes", "message", e.getMessage()));
        }
    }

    /**
     * Eliminar (desactivar) integrante de un aula (solo profesor del aula)
     */
    @DeleteMapping("/{aulaId}/participantes/{estudianteId}")
    public ResponseEntity<?> eliminarParticipanteAula(
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
            String rolString = usuario.getRol().toString();
            // Solo el profesor del aula puede eliminar
            boolean esProfesorDelAula = aulaVirtualService.puedeAccederAAula(usuario.getId(), rolString, aulaId) && "PROFESOR".equals(rolString);
            if (!esProfesorDelAula) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo el profesor del aula puede eliminar integrantes"));
            }
            boolean eliminado = aulaVirtualService.eliminarParticipanteAula(aulaId, estudianteId);
            if (!eliminado) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "error", "No se encontró el participante activo en el aula",
                            "aulaId", aulaId,
                            "estudianteId", estudianteId,
                            "detalle", "Verifica que el estudiante esté inscrito y activo en el aula antes de eliminar."
                        ));
            }
            return ResponseEntity.ok(Map.of("message", "Integrante eliminado correctamente"));
        } catch (Exception e) {
            logger.error("Error al eliminar participante {} del aula {}: {}", estudianteId, aulaId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar participante", "message", e.getMessage()));
        }
    }

    /**
     * 🔥 POST: crear un aula nueva
     */
    @PostMapping
    public AulaVirtual crearAula(@RequestBody AulaVirtual aula, Principal principal) {
        String email = principal.getName();
        var usuario = usuarioService.findByCorreoInstitucional(email);
        Long usuarioId = usuario.getId();
        aula.setProfesorId(usuarioId);
        aula.setEstado("activa");
        // Puedes asignar otros campos por defecto aquí
        return aulaVirtualService.crearAula(aula);
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