package com.usuario.backend.repository.academic;

import com.usuario.backend.model.entity.academic.CursoProfesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoProfesorRepository extends JpaRepository<CursoProfesor, Long> {
    
    // Buscar activos
    List<CursoProfesor> findByActivoTrue();
    
    // Cursos que dicta un profesor
    List<CursoProfesor> findByProfesorIdAndActivoTrue(Long profesorId);
    
    // Profesores que dictan un curso
    List<CursoProfesor> findByCursoIdAndActivoTrue(Long cursoId);
    
    // Buscar asignación específica
    Optional<CursoProfesor> findByProfesorIdAndCursoIdAndActivoTrue(Long profesorId, Long cursoId);
}