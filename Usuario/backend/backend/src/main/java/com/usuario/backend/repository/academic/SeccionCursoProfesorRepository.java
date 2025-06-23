package com.usuario.backend.repository.academic;

import com.teckbook.backend.model.entity.academic.SeccionCursoProfesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionCursoProfesorRepository extends JpaRepository<SeccionCursoProfesor, Long> {
    
    // Buscar activos
    List<SeccionCursoProfesor> findByActivoTrue();
    
    // Clases de un profesor
    List<SeccionCursoProfesor> findByProfesorIdAndActivoTrue(Long profesorId);
    
    // Cursos de una sección
    List<SeccionCursoProfesor> findBySeccionIdAndActivoTrue(Long seccionId);
    
    // Profesores de un curso específico
    List<SeccionCursoProfesor> findByCursoIdAndActivoTrue(Long cursoId);
    
    // Buscar asignación específica
    Optional<SeccionCursoProfesor> findBySeccionIdAndCursoIdAndProfesorIdAndActivoTrue(
        Long seccionId, Long cursoId, Long profesorId);
    
    // Horarios de un profesor
    @Query("SELECT scp FROM SeccionCursoProfesor scp WHERE scp.profesorId = :profesorId AND scp.activo = true ORDER BY scp.horario")
    List<SeccionCursoProfesor> findHorariosByProfesor(@Param("profesorId") Long profesorId);
    
    // Clases por aula
    List<SeccionCursoProfesor> findByAulaAndActivoTrue(String aula);
}