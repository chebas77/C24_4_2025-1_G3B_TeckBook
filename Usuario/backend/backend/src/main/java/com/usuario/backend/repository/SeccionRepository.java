package com.usuario.backend.repository;

import com.usuario.backend.model.entity.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {
    
    List<Seccion> findByCarreraIdAndCiclo(Long carreraId, Integer ciclo);
    
    List<Seccion> findByCarreraId(Long carreraId);
    
    @Query("SELECT s FROM Seccion s WHERE s.carreraId = :carreraId ORDER BY s.ciclo, s.nombre")
    List<Seccion> findByCarreraIdOrderByCicloAndNombre(@Param("carreraId") Long carreraId);
}
