package com.usuario.backend.repository.academic;

import com.usuario.backend.model.entity.academic.UsuarioSeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioSeccionRepository extends JpaRepository<UsuarioSeccion, Long> {
    
    // Buscar activos
    List<UsuarioSeccion> findByActivoTrue();
    
    // Estudiantes de una sección
    List<UsuarioSeccion> findBySeccionIdAndActivoTrue(Long seccionId);
    
    // Secciones de un estudiante
    List<UsuarioSeccion> findByUsuarioIdAndActivoTrue(Long usuarioId);
    
    // Buscar asignación específica
    Optional<UsuarioSeccion> findByUsuarioIdAndSeccionIdAndActivoTrue(Long usuarioId, Long seccionId);
    
    // Contar estudiantes por sección
    Long countBySeccionIdAndActivoTrue(Long seccionId);
}