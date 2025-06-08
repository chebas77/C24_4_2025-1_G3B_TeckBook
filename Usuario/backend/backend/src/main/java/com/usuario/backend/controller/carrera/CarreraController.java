// Usuario/backend/backend/src/main/java/com/usuario/backend/controller/carrera/CarreraController.java
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

    /**
     * Obtiene todas las carreras activas para el formulario de registro
     * Endpoint público - no requiere autenticación
     */
    @GetMapping("/activas")
    public ResponseEntity<?> getCarrerasActivas() {
        try {
            logger.info("Solicitud para obtener carreras activas");
            
            List<Carrera> carreras = carreraService.getAllCarrerasActivas();
            
            if (carreras.isEmpty()) {
                logger.warn("No se encontraron carreras activas");
                return ResponseEntity.ok(Map.of(
                    "carreras", carreras,
                    "message", "No hay carreras disponibles actualmente",
                    "count", 0
                ));
            }
            
            // Crear respuesta simplificada para el frontend
            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("count", carreras.size());
            response.put("message", "Carreras obtenidas exitosamente");
            
            logger.info("Se devolvieron {} carreras activas", carreras.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener carreras activas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener las carreras",
                        "message", e.getMessage(),
                        "carreras", List.of()
                    ));
        }
    }

    /**
     * Obtiene una carrera específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarreraById(@PathVariable Long id) {
        try {
            logger.info("Solicitud para obtener carrera con ID: {}", id);
            
            Carrera carrera = carreraService.findById(id);
            if (carrera != null) {
                return ResponseEntity.ok(carrera);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Carrera no encontrada", "id", id));
            }
            
        } catch (Exception e) {
            logger.error("Error al obtener carrera por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener la carrera", "message", e.getMessage()));
        }
    }

    /**
     * Obtiene carreras por departamento
     */
    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<?> getCarrerasByDepartamento(@PathVariable Long departamentoId) {
        try {
            logger.info("Solicitud para obtener carreras del departamento: {}", departamentoId);
            
            List<Carrera> carreras = carreraService.getCarrerasByDepartamento(departamentoId);
            
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
     * Busca carreras por nombre
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarCarreras(@RequestParam String nombre) {
        try {
            logger.info("Búsqueda de carreras por nombre: {}", nombre);
            
            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El parámetro 'nombre' es requerido"));
            }
            
            List<Carrera> carreras = carreraService.findByNombre(nombre.trim());
            
            Map<String, Object> response = new HashMap<>();
            response.put("carreras", carreras);
            response.put("searchTerm", nombre);
            response.put("count", carreras.size());
            response.put("message", "Búsqueda completada");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al buscar carreras por nombre {}: {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al buscar carreras", "message", e.getMessage()));
        }
    }

    /**
     * Crea una nueva carrera (requiere autenticación de admin)
     */
    @PostMapping
    public ResponseEntity<?> crearCarrera(@RequestBody Carrera carrera) {
        try {
            logger.info("Solicitud para crear nueva carrera: {}", carrera.getNombre());
            
            // Validaciones básicas
            if (carrera.getNombre() == null || carrera.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El nombre de la carrera es requerido"));
            }
            
            Carrera carreraCreada = carreraService.crearCarrera(carrera);
            
            Map<String, Object> response = new HashMap<>();
            response.put("carrera", carreraCreada);
            response.put("message", "Carrera creada exitosamente");
            response.put("id", carreraCreada.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al crear carrera: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error de validación", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al crear carrera: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la carrera", "message", e.getMessage()));
        }
    }

    /**
     * Actualiza una carrera existente (requiere autenticación de admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCarrera(@PathVariable Long id, @RequestBody Carrera carrera) {
        try {
            logger.info("Solicitud para actualizar carrera con ID: {}", id);
            
            carrera.setId(id);
            Carrera carreraActualizada = carreraService.actualizarCarrera(carrera);
            
            Map<String, Object> response = new HashMap<>();
            response.put("carrera", carreraActualizada);
            response.put("message", "Carrera actualizada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al actualizar carrera {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error de validación", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al actualizar carrera {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar la carrera", "message", e.getMessage()));
        }
    }

    /**
     * Desactiva una carrera (requiere autenticación de admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarCarrera(@PathVariable Long id) {
        try {
            logger.info("Solicitud para desactivar carrera con ID: {}", id);
            
            carreraService.desactivarCarrera(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Carrera desactivada exitosamente");
            response.put("id", id);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error al desactivar carrera {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error de validación", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al desactivar carrera {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al desactivar la carrera", "message", e.getMessage()));
        }
    }

    /**
     * Endpoint de salud para verificar el servicio
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "CarreraController",
            "status", "OK",
            "timestamp", System.currentTimeMillis(),
            "endpoints", Map.of(
                "activas", "GET /api/carreras/activas",
                "byId", "GET /api/carreras/{id}",
                "byDepartamento", "GET /api/carreras/departamento/{departamentoId}",
                "buscar", "GET /api/carreras/buscar?nombre={nombre}",
                "crear", "POST /api/carreras",
                "actualizar", "PUT /api/carreras/{id}",
                "desactivar", "DELETE /api/carreras/{id}"
            )
        ));
    }
}