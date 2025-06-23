package com.usuario.backend.repository.core;

import com.usuario.backend.model.entity.core.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    
    // Para /activas
    List<Carrera> findByActivoTrue();
    
    // Para /departamento/{departamentoId}
    List<Carrera> findByDepartamentoId(Long departamentoId);
    
    // Para /departamento/{departamentoId}/activas
    List<Carrera> findByDepartamentoIdAndActivoTrue(Long departamentoId);
    
    // Para /buscar?nombre=
    @Query("SELECT c FROM Carrera c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND c.activo = true")
    List<Carrera> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    // Para validaciones
    boolean existsByNombreAndActivoTrue(String nombre);
    boolean existsByCodigoAndActivoTrue(String codigo);
}