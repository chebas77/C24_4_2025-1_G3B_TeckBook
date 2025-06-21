package com.usuario.backend.repository;

import com.usuario.backend.model.entity.AulaVirtual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaVirtualRepository extends JpaRepository<AulaVirtual, Long> {
    
    List<AulaVirtual> findByProfesorId(Long profesorId);
    
    List<AulaVirtual> findByEstado(String estado);
    
    AulaVirtual findByCodigoAcceso(String codigoAcceso);
}