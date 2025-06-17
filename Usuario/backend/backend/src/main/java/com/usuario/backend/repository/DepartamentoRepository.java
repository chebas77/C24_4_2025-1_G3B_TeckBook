package com.usuario.backend.repository;

import com.usuario.backend.model.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    
    List<Departamento> findByActivoTrue();
    
    Departamento findByCodigo(String codigo);
}