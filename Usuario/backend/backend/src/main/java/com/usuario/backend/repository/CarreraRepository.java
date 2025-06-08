// Usuario/backend/backend/src/main/java/com/usuario/backend/repository/CarreraRepository.java
package com.usuario.backend.repository;

import com.usuario.backend.model.entity.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    
    // 🔥 FIX: Cambiar de "activa" a "activo"
    List<Carrera> findByActivoTrue();
    
    // Buscar carrera por código
    Carrera findByCodigo(String codigo);
    
    // 🔥 FIX: Obtener carreras por departamento usando "activo"
    List<Carrera> findByDepartamentoIdAndActivoTrue(Long departamentoId);
    
    // 🔥 FIX: Buscar carreras por nombre usando "activo"
    @Query("SELECT c FROM Carrera c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND c.activo = true")
    List<Carrera> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    // 🔥 QUERY ADICIONAL: Obtener todas las carreras (para debugging)
    @Query("SELECT c FROM Carrera c ORDER BY c.nombre")
    List<Carrera> findAllOrderByNombre();
    
    // 🔥 QUERY ADICIONAL: Contar carreras activas
    @Query("SELECT COUNT(c) FROM Carrera c WHERE c.activo = true")
    Long countCarrerasActivas();
}