package com.usuario.backend.repository.academic;


import com.usuario.backend.model.entity.academic.ProfesorCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesorCarreraRepository extends JpaRepository<ProfesorCarrera, Long> {
    
    // Buscar activos
    List<ProfesorCarrera> findByActivoTrue();
    
    // Carreras donde puede enseñar un profesor
    List<ProfesorCarrera> findByProfesorIdAndActivoTrue(Long profesorId);
    
    // Profesores que pueden enseñar en una carrera
    List<ProfesorCarrera> findByCarreraIdAndActivoTrue(Long carreraId);
    
    // Buscar asignación específica
    Optional<ProfesorCarrera> findByProfesorIdAndCarreraIdAndActivoTrue(Long profesorId, Long carreraId);
}
