package com.usuario.backend.repository;

import com.usuario.backend.model.entity.AulaVirtual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaVirtualRepository extends JpaRepository<AulaVirtual, Long> {
    
    // ✅ MÉTODOS BÁSICOS
    List<AulaVirtual> findByProfesorId(Long profesorId);
    List<AulaVirtual> findByEstado(String estado);
    AulaVirtual findByCodigoAcceso(String codigoAcceso);
    
    // ✅ MÉTODOS CON ORDENAMIENTO
    List<AulaVirtual> findByProfesorIdOrderByFechaInicioDesc(Long profesorId);
    List<AulaVirtual> findByEstadoOrderByFechaInicioDesc(String estado);
    
    // ✅ MÉTODOS OPTIMIZADOS PARA EL SERVICIO
    List<AulaVirtual> findByProfesorIdAndEstadoOrderByFechaInicioDesc(Long profesorId, String estado);
    
    @Query("SELECT av FROM AulaVirtual av WHERE av.id IN :aulaIds AND av.estado = :estado ORDER BY av.fechaInicio DESC")
    List<AulaVirtual> findByIdInAndEstadoOrderByFechaInicioDesc(@Param("aulaIds") List<Long> aulaIds, @Param("estado") String estado);
    
    // ✅ MÉTODOS DE VERIFICACIÓN
    boolean existsByCodigoAcceso(String codigoAcceso);
    long countByEstado(String estado);
    
    // ✅ BÚSQUEDAS POR NOMBRE
    List<AulaVirtual> findByNombreContainingIgnoreCaseAndProfesorId(String nombre, Long profesorId);
    List<AulaVirtual> findByNombreContainingIgnoreCaseAndEstado(String nombre, String estado);
    List<AulaVirtual> findByTituloContainingIgnoreCase(String titulo);
    
    // ✅ BÚSQUEDAS POR SECCIÓN
    List<AulaVirtual> findBySeccionId(Long seccionId);
    List<AulaVirtual> findBySeccionIdAndEstado(Long seccionId, String estado);
    
    // ✅ QUERIES PERSONALIZADAS
    @Query("SELECT COUNT(av) FROM AulaVirtual av WHERE av.profesorId = :profesorId AND av.estado = :estado")
    long countByProfesorIdAndEstado(@Param("profesorId") Long profesorId, @Param("estado") String estado);
    
    @Query("SELECT av FROM AulaVirtual av WHERE av.estado = 'activa' ORDER BY av.fechaInicio DESC")
    List<AulaVirtual> findAulasActivas();
    
    // ✅ VERIFICACIONES ESPECÍFICAS
    boolean existsByNombreAndProfesorIdAndEstado(String nombre, Long profesorId, String estado);
}