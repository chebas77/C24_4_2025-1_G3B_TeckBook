package com.usuario.backend.repository.academic;

import com.usuario.backend.model.entity.academic.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {
    
    // Buscar secciones activas
    List<Seccion> findByActivoTrue();
    
    // Buscar por carrera
    List<Seccion> findByCarreraIdAndActivoTrue(Long carreraId);
    
    // Buscar por carrera y ciclo
    List<Seccion> findByCarreraIdAndCicloAndActivoTrue(Long carreraId, Integer ciclo);
    
    // Buscar por período académico
    List<Seccion> findByPeriodoAcademicoAndActivoTrue(String periodoAcademico);
    
    // Buscar sección específica (letra + ciclo + carrera + período)
    Optional<Seccion> findByLetraAndCicloAndCarreraIdAndPeriodoAcademico(
        String letra, Integer ciclo, Long carreraId, String periodoAcademico);
    
    // Contar estudiantes por sección
    @Query("SELECT COUNT(us) FROM UsuarioSeccion us WHERE us.seccionId = :seccionId AND us.activo = true")
    Long countEstudiantesBySeccion(@Param("seccionId") Long seccionId);
}