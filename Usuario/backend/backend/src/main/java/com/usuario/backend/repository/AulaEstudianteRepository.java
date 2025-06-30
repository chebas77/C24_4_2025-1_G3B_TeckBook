package com.usuario.backend.repository;

import com.usuario.backend.model.entity.AulaEstudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaEstudianteRepository extends JpaRepository<AulaEstudiante, Long> {
    
    // ✅ BUSCAR AULAS DE UN ESTUDIANTE
    @Query("SELECT ae.aulaId FROM AulaEstudiante ae WHERE ae.estudianteId = :estudianteId AND ae.estado = 'activo'")
    List<Long> findAulaIdsByEstudianteId(@Param("estudianteId") Long estudianteId);
    
    // ✅ BUSCAR ESTUDIANTES DE UN AULA
    List<AulaEstudiante> findByAulaIdAndEstado(Long aulaId, AulaEstudiante.EstadoEstudiante estado);
    
    // ✅ VERIFICAR SI ESTUDIANTE ESTÁ EN AULA
    boolean existsByAulaIdAndEstudianteIdAndEstado(Long aulaId, Long estudianteId, AulaEstudiante.EstadoEstudiante estado);
    
    // ✅ CONTAR ESTUDIANTES EN AULA
    long countByAulaIdAndEstado(Long aulaId, AulaEstudiante.EstadoEstudiante estado);
}