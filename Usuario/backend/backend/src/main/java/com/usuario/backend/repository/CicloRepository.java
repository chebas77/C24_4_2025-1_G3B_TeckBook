package com.usuario.backend.repository;

import com.usuario.backend.model.entity.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CicloRepository extends JpaRepository<Ciclo, Long> {
    
    List<Ciclo> findByOrderByNumero();
    
    @Query("SELECT c FROM Ciclo c WHERE c.numero <= " +
           "(SELECT COALESCE(car.duracionCiclos, 6) FROM Carrera car WHERE car.id = :carreraId) " +
           "ORDER BY c.numero")
    List<Ciclo> findCiclosByCarrera(@Param("carreraId") Long carreraId);
    
    // 🔥 MÉTODO ALTERNATIVO: Obtener todos los ciclos sin filtro
    @Query("SELECT c FROM Ciclo c ORDER BY c.numero")
    List<Ciclo> findAllCiclos();
}