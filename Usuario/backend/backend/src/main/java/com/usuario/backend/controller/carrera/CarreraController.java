package com.usuario.backend.controller.carrera;

import com.usuario.backend.model.entity.Carrera;
import com.usuario.backend.service.carrera.CarreraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carreras")
@CrossOrigin(origins = "*")
public class CarreraController {

    private static final Logger logger = LoggerFactory.getLogger(CarreraController.class);

    @Autowired
    private CarreraService carreraService;

    // 📚 Obtiene todas las carreras activas
    @GetMapping("/activas")
    public ResponseEntity<?> getCarrerasActivas() {
        try {
            logger.info("📚 Obteniendo carreras activas");
            List<Carrera> carreras = carreraService.getAllCarrerasActivas();

            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("count", carreras.size());
            response.put("isEmpty", carreras.isEmpty());
            response.put("message", carreras.isEmpty() ? "No hay carreras disponibles actualmente" : "Carreras obtenidas exitosamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Error al obtener carreras activas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al obtener las carreras",
                    "message", e.getMessage(),
                    "carreras", List.of(),
                    "count", 0,
                    "isEmpty", true
            ));
        }
    }

    // 🔍 Obtiene una carrera específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarreraById(@PathVariable Long id) {
        try {
            logger.info("🔍 Obteniendo carrera con ID: {}", id);
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID de carrera inválido"));
            }

            Carrera carrera = carreraService.findById(id);
            if (carrera != null) {
                return ResponseEntity.ok(Map.of(
                        "carrera", carrera,
                        "message", "Carrera encontrada exitosamente"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "error", "Carrera no encontrada",
                        "message", "No existe una carrera con el ID especificado",
                        "id", id
                ));
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener carrera por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error interno del servidor",
                    "message", e.getMessage(),
                    "id", id
            ));
        }
    }

    // 🏢 Obtiene carreras por departamento
    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<?> getCarrerasByDepartamento(@PathVariable Long departamentoId) {
        try {
            logger.info("🏢 Obteniendo carreras del departamento: {}", departamentoId);
            if (departamentoId == null || departamentoId <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID de departamento inválido"));
            }

            List<Carrera> carreras = carreraService.getCarrerasByDepartamento(departamentoId);

            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("departamentoId", departamentoId);
            response.put("count", carreras.size());
            response.put("isEmpty", carreras.isEmpty());
            response.put("message", carreras.isEmpty() ? "No hay carreras para el departamento especificado" : "Carreras del departamento obtenidas exitosamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Error al obtener carreras del departamento {}: {}", departamentoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al obtener carreras del departamento",
                    "message", e.getMessage(),
                    "departamentoId", departamentoId,
                    "carreras", List.of(),
                    "count", 0
            ));
        }
    }

    // 🔎 Busca carreras por nombre
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarCarreras(@RequestParam String nombre) {
        try {
            logger.info("🔎 Búsqueda de carreras por nombre: '{}'", nombre);
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Parámetro de búsqueda requerido",
                        "message", "El parámetro 'nombre' no puede estar vacío"
                ));
            }
            if (nombre.trim().length() < 2) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Búsqueda muy corta",
                        "message", "El término de búsqueda debe tener al menos 2 caracteres"
                ));
            }

            List<Carrera> carreras = carreraService.findByNombre(nombre.trim());

            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("searchTerm", nombre.trim());
            response.put("count", carreras.size());
            response.put("isEmpty", carreras.isEmpty());
            response.put("message", carreras.isEmpty() ? "No se encontraron carreras con el término especificado" : "Búsqueda completada exitosamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Error al buscar carreras por nombre '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error en la búsqueda",
                    "message", e.getMessage(),
                    "searchTerm", nombre,
                    "carreras", List.of(),
                    "count", 0
            ));
        }
    }

    // ➕ Crea una nueva carrera
    @PostMapping
    public ResponseEntity<?> crearCarrera(@RequestBody Carrera carrera) {
        try {
            logger.info("➕ Creando nueva carrera: {}", carrera != null ? carrera.getNombre() : "null");
            Map<String, String> validationErrors = validateCarreraData(carrera);
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Errores de validación",
                        "details", validationErrors
                ));
            }

            Carrera carreraCreada = carreraService.crearCarrera(carrera);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "carrera", carreraCreada,
                    "message", "Carrera creada exitosamente",
                    "id", carreraCreada.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error de validación",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("❌ Error al crear carrera: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error interno del servidor",
                    "message", e.getMessage()
            ));
        }
    }

    // ✏ Actualiza una carrera existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCarrera(@PathVariable Long id, @RequestBody Carrera carrera) {
        try {
            logger.info("✏ Actualizando carrera con ID: {}", id);
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID de carrera inválido"));
            }

            Carrera existingCarrera = carreraService.findById(id);
            if (existingCarrera == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "error", "Carrera no encontrada",
                        "message", "No existe una carrera con el ID especificado",
                        "id", id
                ));
            }

            Map<String, String> validationErrors = validateCarreraData(carrera);
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Errores de validación",
                        "details", validationErrors
                ));
            }

            carrera.setId(id);
            Carrera carreraActualizada = carreraService.actualizarCarrera(carrera);

            return ResponseEntity.ok(Map.of(
                    "carrera", carreraActualizada,
                    "message", "Carrera actualizada exitosamente",
                    "id", id
            ));
        } catch (Exception e) {
            logger.error("❌ Error al actualizar carrera {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error interno del servidor",
                    "message", e.getMessage(),
                    "id", id
            ));
        }
    }

    // Obtiene carreras activas por departamento
    @GetMapping("/departamento/{departamentoId}/activas")
    public ResponseEntity<?> getCarrerasActivasByDepartamento(@PathVariable Long departamentoId) {
        try {
            logger.info("Solicitud para obtener carreras activas del departamento: {}", departamentoId);
            List<Carrera> carreras = carreraService.getCarrerasActivasByDepartamento(departamentoId);
            return ResponseEntity.ok(Map.of(
                    "carreras", carreras,
                    "departamentoId", departamentoId,
                    "count", carreras.size(),
                    "message", "Carreras del departamento obtenidas exitosamente"
            ));
        } catch (Exception e) {
            logger.error("Error al obtener carreras del departamento {}: {}", departamentoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error al obtener carreras del departamento",
                    "message", e.getMessage()
            ));
        }
    }

    // 🗑 Desactiva una carrera
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarCarrera(@PathVariable Long id) {
        try {
            logger.info("🗑 Desactivando carrera con ID: {}", id);
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID de carrera inválido"));
            }

            Carrera existingCarrera = carreraService.findById(id);
            if (existingCarrera == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "error", "Carrera no encontrada",
                        "message", "No existe una carrera con el ID especificado",
                        "id", id
                ));
            }

            carreraService.desactivarCarrera(id);

            return ResponseEntity.ok(Map.of(
                    "message", "Carrera desactivada exitosamente",
                    "id", id,
                    "carrera", existingCarrera.getNombre()
            ));
        } catch (Exception e) {
            logger.error("❌ Error al desactivar carrera {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error interno del servidor",
                    "message", e.getMessage(),
                    "id", id
            ));
        }
    }

    // 🏥 Health check
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            long count = carreraService.getAllCarrerasActivas().size();
            return ResponseEntity.ok(Map.of(
                    "service", "CarreraController",
                    "status", "OK",
                    "timestamp", System.currentTimeMillis(),
                    "carrerasActivas", count
            ));
        } catch (Exception e) {
            logger.error("❌ Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "service", "CarreraController",
                    "status", "ERROR",
                    "timestamp", System.currentTimeMillis(),
                    "error", e.getMessage()
            ));
        }
    }

    // ✅ Validación auxiliar
    private Map<String, String> validateCarreraData(Carrera carrera) {
        Map<String, String> errors = new HashMap<>();

        if (carrera == null) {
            errors.put("carrera", "Los datos de la carrera son requeridos");
            return errors;
        }

        if (carrera.getNombre() == null || carrera.getNombre().trim().isEmpty()) {
            errors.put("nombre", "El nombre de la carrera es requerido");
        } else if (carrera.getNombre().trim().length() < 3) {
            errors.put("nombre", "El nombre debe tener al menos 3 caracteres");
        } else if (carrera.getNombre().trim().length() > 100) {
            errors.put("nombre", "El nombre no puede exceder 100 caracteres");
        }

        if (carrera.getCodigo() == null || carrera.getCodigo().trim().isEmpty()) {
            errors.put("codigo", "El código de la carrera es requerido");
        } else if (carrera.getCodigo().trim().length() > 10) {
            errors.put("codigo", "El código no puede exceder 10 caracteres");
        }

        if (carrera.getDepartamentoId() == null || carrera.getDepartamentoId() <= 0) {
            errors.put("departamentoId", "Debe especificar un departamento válido");
        }

        return errors;
    }
}
