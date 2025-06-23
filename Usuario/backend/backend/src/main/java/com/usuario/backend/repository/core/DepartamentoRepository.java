package com.usuario.backend.repository.core;

import com.usuario.backend.model.entity.core.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    
    List<Departamento> findByActivoTrue();
    
    boolean existsByNombreAndActivoTrue(String nombre);
}