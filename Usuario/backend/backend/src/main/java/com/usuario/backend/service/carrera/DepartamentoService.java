package com.usuario.backend.service.carrera;
import com.usuario.backend.model.entity.Departamento;
import com.usuario.backend.repository.DepartamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    private static final Logger logger = LoggerFactory.getLogger(DepartamentoService.class);

    @Autowired
    private DepartamentoRepository departamentoRepository;

    public List<Departamento> getAllDepartamentosActivos() {
        try {
            List<Departamento> departamentos = departamentoRepository.findByActivoTrue();
            logger.info("Se obtuvieron {} departamentos activos", departamentos.size());
            return departamentos;
        } catch (Exception e) {
            logger.error("Error al obtener departamentos activos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los departamentos activos", e);
        }
    }

    public Departamento findById(Long id) {
        try {
            Optional<Departamento> departamento = departamentoRepository.findById(id);
            return departamento.orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar departamento por ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    public Departamento findByCodigo(String codigo) {
        try {
            return departamentoRepository.findByCodigo(codigo);
        } catch (Exception e) {
            logger.error("Error al buscar departamento por código {}: {}", codigo, e.getMessage(), e);
            return null;
        }
    }
}