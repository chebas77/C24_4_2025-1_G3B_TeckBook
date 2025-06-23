package com.usuario.backend.service.core;

import com.usuario.backend.model.entity.core.Departamento;
import com.usuario.backend.repository.core.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    public List<Departamento> getAllDepartamentosActivos() {
        return departamentoRepository.findByActivoTrue();
    }

    public Departamento findById(Long id) {
        return departamentoRepository.findById(id).orElse(null);
    }

    public Departamento crearDepartamento(Departamento departamento) {
        if (departamentoRepository.existsByNombreAndActivoTrue(departamento.getNombre())) {
            throw new IllegalArgumentException("Ya existe un departamento con el nombre: " + departamento.getNombre());
        }
        
        return departamentoRepository.save(departamento);
    }

    public Departamento actualizarDepartamento(Departamento departamento) {
        Optional<Departamento> existing = departamentoRepository.findById(departamento.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Departamento no encontrado");
        }
        
        return departamentoRepository.save(departamento);
    }

    public void desactivarDepartamento(Long id) {
        Optional<Departamento> departamentoOpt = departamentoRepository.findById(id);
        if (departamentoOpt.isPresent()) {
            Departamento departamento = departamentoOpt.get();
            departamento.setActivo(false);
            departamentoRepository.save(departamento);
        }
    }
}