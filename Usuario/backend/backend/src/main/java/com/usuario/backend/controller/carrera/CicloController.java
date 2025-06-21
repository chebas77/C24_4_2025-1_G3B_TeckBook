package com.usuario.backend.controller.carrera;
import com.usuario.backend.model.entity.Ciclo;
import com.usuario.backend.service.carrera.CicloService;
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
@RequestMapping("/api/ciclos")
@CrossOrigin(origins = "*")
public class CicloController {

    private static final Logger logger = LoggerFactory.getLogger(CicloController.class);

    @Autowired
    private CicloService cicloService;

    /**
     * 📚 Obtiene todos los ciclos disponibles
     */
    @GetMapping("/todos")
    public ResponseEntity<?> getAllCiclos() {
        try {
            logger.info("Solicitud para obtener todos los ciclos");
            
            List<Ciclo> ciclos = cicloService.getAllCiclos();
            
            Map<String, Object> response = new HashMap<>();
            response.put("ciclos", ciclos);
            response.put("count", ciclos.size());
            response.put("message", "Ciclos obtenidos exitosamente");
            
            logger.info("Se devolvieron {} ciclos", ciclos.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener ciclos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener los ciclos",
                        "message", e.getMessage(),
                        "ciclos", List.of()
                    ));
        }
    }

    /**
     * 📚 Obtiene ciclos filtrados por carrera
     */
    @GetMapping("/carrera/{carreraId}")
    public ResponseEntity<?> getCiclosByCarrera(@PathVariable Long carreraId) {
        try {
            logger.info("Solicitud para obtener ciclos de carrera: {}", carreraId);
            
            List<Ciclo> ciclos = cicloService.getCiclosByCarrera(carreraId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("ciclos", ciclos);
            response.put("carreraId", carreraId);
            response.put("count", ciclos.size());
            response.put("message", "Ciclos de carrera obtenidos exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener ciclos de carrera {}: {}", carreraId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener ciclos de carrera", "message", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "CicloController",
            "status", "OK",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
