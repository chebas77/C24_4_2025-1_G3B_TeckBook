package com.usuario.backend.repository.academic;

import com.usuario.backend.model.entity.academic.SeccionProfesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionProfesorRepository extends JpaRepository<SeccionProfesor, Long> {
    
    // Buscar activos
    List<SeccionProfesor> findByActivoTrue();
    
    // Secciones de un profesor
    List<SeccionProfesor> findByProfesorIdAndActivoTrue(Long profesorId);
    
    // Profesores de una sección
    List<SeccionProfesor> findBySeccionIdAndActivoTrue(Long seccionId);
    
    // Buscar asignación específica
    Optional<SeccionProfesor> findByProfesorIdAndSeccionIdAndActivoTrue(Long profesorId, Long seccionId);
}