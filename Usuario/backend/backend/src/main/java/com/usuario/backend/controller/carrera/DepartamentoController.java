package com.usuario.backend.controller.carrera;
import com.usuario.backend.model.entity.Departamento;
import com.usuario.backend.service.carrera.DepartamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentoController {

    private static final Logger logger = LoggerFactory.getLogger(DepartamentoController.class);

    @Autowired
    private DepartamentoService departamentoService;

    /**
     * 🏢 Obtiene todos los departamentos activos
     * Endpoint público para formularios
     */
    @GetMapping("/activos")
    public ResponseEntity<?> getDepartamentosActivos() {
        try {
            logger.info("🏢 Obteniendo departamentos activos");
            
            List<Departamento> departamentos = departamentoService.getAllDepartamentosActivos();
            
            return ResponseEntity.ok(Map.of(
                "departamentos", departamentos,
                "count", departamentos.size(),
                "message", "Departamentos obtenidos exitosamente"
            ));
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener departamentos activos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener los departamentos",
                        "message", e.getMessage(),
                        "departamentos", List.of(),
                        "count", 0
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartamentoById(@PathVariable Long id) {
        try {
            Departamento departamento = departamentoService.findById(id);
            if (departamento != null) {
                return ResponseEntity.ok(departamento);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Departamento no encontrado"));
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener departamento {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el departamento"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "DepartamentoController",
            "status", "OK",
            "timestamp", System.currentTimeMillis()
        ));
    }
}