package com.usuario.backend.controller.core;

import com.usuario.backend.model.entity.core.Departamento;
import com.usuario.backend.service.core.DepartamentoService;
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
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentoController {

    private static final Logger logger = LoggerFactory.getLogger(DepartamentoController.class);

    @Autowired
    private DepartamentoService departamentoService;

    /**
     * 🏢 Obtiene todos los departamentos activos para el filtro cascada
     * Endpoint público para crear aulas
     */
    @GetMapping("/activos")
    public ResponseEntity<?> getDepartamentosActivos() {
        try {
            logger.info("Solicitud para obtener departamentos activos");
            
            List<Departamento> departamentos = departamentoService.getAllDepartamentosActivos();
            
            Map<String, Object> response = new HashMap<>();
            response.put("departamentos", departamentos);
            response.put("count", departamentos.size());
            response.put("message", "Departamentos obtenidos exitosamente");
            
            logger.info("Se devolvieron {} departamentos activos", departamentos.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener departamentos activos: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener los departamentos",
                        "message", e.getMessage(),
                        "departamentos", List.of()
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
                        .body(Map.of("error", "Departamento no encontrado", "id", id));
            }
        } catch (Exception e) {
            logger.error("Error al obtener departamento por ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el departamento", "message", e.getMessage()));
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