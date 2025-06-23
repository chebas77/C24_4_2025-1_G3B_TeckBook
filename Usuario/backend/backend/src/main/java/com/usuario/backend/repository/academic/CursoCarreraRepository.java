package com.usuario.backend.repository.academic;


import com.usuario.backend.model.entity.academic.CursoCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoCarreraRepository extends JpaRepository<CursoCarrera, Long> {
    
    // Buscar activos
    List<CursoCarrera> findByActivoTrue();
    
    // Cursos de una carrera
    List<CursoCarrera> findByCarreraIdAndActivoTrue(Long carreraId);
    
    // Carreras que tienen un curso
    List<CursoCarrera> findByCursoIdAndActivoTrue(Long cursoId);
    
    // Cursos obligatorios de una carrera
    List<CursoCarrera> findByCarreraIdAndEsObligatorioTrueAndActivoTrue(Long carreraId);
    
    // Cursos por carrera y ciclo sugerido
    List<CursoCarrera> findByCarreraIdAndCicloSugeridoAndActivoTrue(Long carreraId, Integer cicloSugerido);
    
    // Verificar si existe relación específica
    @Query("SELECT cc FROM CursoCarrera cc WHERE cc.cursoId = :cursoId AND cc.carreraId = :carreraId AND cc.activo = true")
    CursoCarrera findByCursoIdAndCarreraIdAndActivoTrue(@Param("cursoId") Long cursoId, @Param("carreraId") Long carreraId);
}
