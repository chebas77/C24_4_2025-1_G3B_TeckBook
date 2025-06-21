package com.usuario.backend.controller.carrera;

import com.usuario.backend.model.entity.Seccion;
import com.usuario.backend.service.carrera.SeccionService;
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
@RequestMapping("/api/secciones")
@CrossOrigin(origins = "*")
public class SeccionController {

    private static final Logger logger = LoggerFactory.getLogger(SeccionController.class);

    @Autowired
    private SeccionService seccionService;

    /**
     * 🏫 Obtiene secciones filtradas por carrera y ciclo
     * Este es el endpoint principal para la cascada
     */
    @GetMapping("/carrera/{carreraId}/ciclo/{cicloId}")
    public ResponseEntity<?> getSeccionesByCarreraAndCiclo(
            @PathVariable Long carreraId, 
            @PathVariable Long cicloId) {
        try {
            logger.info("Solicitud para obtener secciones: carrera={}, ciclo={}", carreraId, cicloId);
            
            List<Seccion> secciones = seccionService.getSeccionesByCarreraAndCiclo(carreraId, cicloId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("secciones", secciones);
            response.put("carreraId", carreraId);
            response.put("cicloId", cicloId);
            response.put("count", secciones.size());
            response.put("message", "Secciones obtenidas exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener secciones para carrera {} y ciclo {}: {}", 
                        carreraId, cicloId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener secciones", "message", e.getMessage()));
        }
    }

    /**
     * 🏫 Obtiene todas las secciones de una carrera
     */
    @GetMapping("/carrera/{carreraId}")
    public ResponseEntity<?> getSeccionesByCarrera(@PathVariable Long carreraId) {
        try {
            logger.info("Solicitud para obtener secciones de carrera: {}", carreraId);
            
            List<Seccion> secciones = seccionService.getSeccionesByCarrera(carreraId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("secciones", secciones);
            response.put("carreraId", carreraId);
            response.put("count", secciones.size());
            response.put("message", "Secciones de carrera obtenidas exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener secciones de carrera {}: {}", carreraId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener secciones de carrera", "message", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "SeccionController",
            "status", "OK",
            "timestamp", System.currentTimeMillis()
        ));
    }
}