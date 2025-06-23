package com.usuario.backend.controller.core;

import com.usuario.backend.model.entity.core.Carrera;
import com.usuario.backend.service.core.CarreraService;
import jakarta.validation.Valid;
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

    /**
     * 📚 Obtiene todas las carreras activas para formularios públicos
     * Endpoint público - no requiere autenticación
     */
    @GetMapping("/activas")
    public ResponseEntity<?> getCarrerasActivas() {
        try {
            logger.info("📚 Obteniendo carreras activas");
            
            List<Carrera> carreras = carreraService.getAllCarrerasActivas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("count", carreras.size());
            
            if (carreras.isEmpty()) {
                logger.warn("⚠️ No se encontraron carreras activas");
                response.put("message", "No hay carreras disponibles actualmente");
                response.put("isEmpty", true);
            } else {
                logger.info("✅ Se devolvieron {} carreras activas", carreras.size());
                response.put("message", "Carreras obtenidas exitosamente");
                response.put("isEmpty", false);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener carreras activas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener las carreras",
                        "message", e.getMessage(),
                        "carreras", List.of(),
                        "count", 0,
                        "isEmpty", true
                    ));
        }
    }

    /**
     * 🔍 Obtiene una carrera específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarreraById(@PathVariable Long id) {
        try {
            logger.info("🔍 Obteniendo carrera con ID: {}", id);
            
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID de carrera inválido"));
            }
            
            Carrera carrera = carreraService.findById(id);
            if (carrera != null) {
                logger.info("✅ Carrera encontrada: {}", carrera.getNombre());
                return ResponseEntity.ok(Map.of(
                    "carrera", carrera,
                    "message", "Carrera encontrada exitosamente"
                ));
            } else {
                logger.warn("⚠️ Carrera no encontrada con ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "error", "Carrera no encontrada", 
                            "id", id,
                            "message", "No existe una carrera con el ID especificado"
                        ));
            }
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener carrera por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error interno del servidor", 
                        "message", e.getMessage(),
                        "id", id
                    ));
        }
    }

    /**
     * 🏢 Obtiene carreras por departamento
     */
    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<?> getCarrerasByDepartamento(@PathVariable Long departamentoId) {
        try {
            logger.info("🏢 Obteniendo carreras del departamento: {}", departamentoId);
            
            if (departamentoId == null || departamentoId <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID de departamento inválido"));
            }
            
            List<Carrera> carreras = carreraService.getCarrerasByDepartamento(departamentoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("departamentoId", departamentoId);
            response.put("count", carreras.size());
            response.put("isEmpty", carreras.isEmpty());
            
            if (carreras.isEmpty()) {
                response.put("message", "No hay carreras para el departamento especificado");
                logger.info("⚠️ No se encontraron carreras para departamento: {}", departamentoId);
            } else {
                response.put("message", "Carreras del departamento obtenidas exitosamente");
                logger.info("✅ Se encontraron {} carreras para departamento: {}", carreras.size(), departamentoId);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener carreras del departamento {}: {}", departamentoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener carreras del departamento", 
                        "message", e.getMessage(),
                        "departamentoId", departamentoId,
                        "carreras", List.of(),
                        "count", 0
                    ));
        }
    }

    /**
     * 🏢 Obtiene carreras activas por departamento
     */
    @GetMapping("/departamento/{departamentoId}/activas")
    public ResponseEntity<?> getCarrerasActivasByDepartamento(@PathVariable Long departamentoId) {
        try {
            logger.info("Solicitud para obtener carreras activas del departamento: {}", departamentoId);
            
            List<Carrera> carreras = carreraService.getCarrerasActivasByDepartamento(departamentoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("departamentoId", departamentoId);
            response.put("count", carreras.size());
            response.put("message", "Carreras del departamento obtenidas exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener carreras del departamento {}: {}", departamentoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener carreras del departamento", "message", e.getMessage()));
        }
    }

    /**
     * 🔎 Busca carreras por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarCarreras(@RequestParam String nombre) {
        try {
            logger.info("🔎 Búsqueda de carreras por nombre: '{}'", nombre);
            
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                            "error", "Parámetro de búsqueda requerido",
                            "message", "El parámetro 'nombre' no puede estar vacío"
                        ));
            }
            
            if (nombre.trim().length() < 2) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
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
            
            if (carreras.isEmpty()) {
                response.put("message", "No se encontraron carreras con el término especificado");
                logger.info("⚠️ Sin resultados para búsqueda: '{}'", nombre);
            } else {
                response.put("message", "Búsqueda completada exitosamente");
                logger.info("✅ Se encontraron {} carreras para: '{}'", carreras.size(), nombre);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al buscar carreras por nombre '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error en la búsqueda", 
                        "message", e.getMessage(),
                        "searchTerm", nombre,
                        "carreras", List.of(),
                        "count", 0
                    ));
        }
    }

    /**
     * ➕ Crea una nueva carrera (requiere autenticación de admin)
     */
    @PostMapping
    public ResponseEntity<?> crearCarrera(@Valid @RequestBody Carrera carrera) {
        try {
            logger.info("➕ Creando nueva carrera: {}", carrera != null ? carrera.getNombre() : "null");
            
            Carrera carreraCreada = carreraService.crearCarrera(carrera);
            
            Map<String, Object> response = new HashMap<>();
            response.put("carrera", carreraCreada);
            response.put("message", "Carrera creada exitosamente");
            response.put("id", carreraCreada.getId());
            
            logger.info("✅ Carrera creada exitosamente: {} (ID: {})", carreraCreada.getNombre(), carreraCreada.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("❌ Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error", "Error de validación", 
                        "message", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.error("❌ Error al crear carrera: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error interno del servidor", 
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * ✏️ Actualiza una carrera existente (requiere autenticación de admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCarrera(@PathVariable Long id, @Valid @RequestBody Carrera carrera) {
        try {
            logger.info("✏️ Actualizando carrera con ID: {}", id);
            
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID de carrera inválido"));
            }
            
            // Verificar que la carrera existe
            Carrera existingCarrera = carreraService.findById(id);
            if (existingCarrera == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "error", "Carrera no encontrada",
                            "message", "No existe una carrera con el ID especificado",
                            "id", id
                        ));
            }
            
            carrera.setId(id);
            Carrera carreraActualizada = carreraService.actualizarCarrera(carrera);
            
            Map<String, Object> response = new HashMap<>();
            response.put("carrera", carreraActualizada);
            response.put("message", "Carrera actualizada exitosamente");
            response.put("id", id);
            
            logger.info("✅ Carrera actualizada: {} (ID: {})", carreraActualizada.getNombre(), id);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("❌ Error de validación al actualizar carrera {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error", "Error de validación", 
                        "message", e.getMessage(),
                        "id", id
                    ));
        } catch (Exception e) {
            logger.error("❌ Error al actualizar carrera {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error interno del servidor", 
                        "message", e.getMessage(),
                        "id", id
                    ));
        }
    }

    /**
     * 🗑️ Desactiva una carrera (soft delete)
     */ 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarCarrera(@PathVariable Long id) {
        try {
            logger.info("🗑️ Desactivando carrera con ID: {}", id);
            
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "ID de carrera inválido"));
            }
            
            // Verificar que la carrera existe
            Carrera existingCarrera = carreraService.findById(id);
            if (existingCarrera == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "error", "Carrera no encontrada",
                            "message", "No existe una carrera con el ID especificado",
                            "id", id
                        ));
            }
            
            carreraService.desactivarCarrera(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Carrera desactivada exitosamente");
            response.put("id", id);
            response.put("carrera", existingCarrera.getNombre());
            
            logger.info("✅ Carrera desactivada: {} (ID: {})", existingCarrera.getNombre(), id);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("❌ Error al desactivar carrera {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "error", "Error de validación", 
                        "message", e.getMessage(),
                        "id", id
                    ));
        } catch (Exception e) {
            logger.error("❌ Error al desactivar carrera {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error interno del servidor", 
                        "message", e.getMessage(),
                        "id", id
                    ));
        }
    }

    /**
     * 🏥 Endpoint de salud para verificar el servicio
     */
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
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                        "service", "CarreraController",
                        "status", "ERROR",
                        "timestamp", System.currentTimeMillis(),
                        "error", e.getMessage()
                    ));
        }
    }
}