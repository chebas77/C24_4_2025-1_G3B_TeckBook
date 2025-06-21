package com.usuario.backend.repository;

import com.usuario.backend.model.entity.AulaVirtual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaVirtualRepository extends JpaRepository<AulaVirtual, Long> {
    
    // ✅ MÉTODOS BÁSICOS EXISTENTES
    List<AulaVirtual> findByProfesorId(Long profesorId);
    List<AulaVirtual> findByEstado(String estado);
    AulaVirtual findByCodigoAcceso(String codigoAcceso);
    
    // 🆕 MÉTODOS NECESARIOS PARA EL SERVICE
    
    /**
     * Obtener aulas de un profesor ordenadas por fecha (más recientes primero)
     */
    List<AulaVirtual> findByProfesorIdOrderByFechaInicioDesc(Long profesorId);
    
    /**
     * Obtener aulas por estado ordenadas por fecha
     */
    List<AulaVirtual> findByEstadoOrderByFechaInicioDesc(String estado);
    
    /**
     * Verificar si existe un código de acceso (para generar códigos únicos)
     */
    boolean existsByCodigoAcceso(String codigoAcceso);
    
    /**
     * Contar aulas por estado
     */
    long countByEstado(String estado);
    
    /**
     * Buscar aulas por nombre (case insensitive) y profesor
     */
    List<AulaVirtual> findByNombreContainingIgnoreCaseAndProfesorId(String nombre, Long profesorId);
    
    /**
     * Buscar aulas por nombre (case insensitive) y estado
     */
    List<AulaVirtual> findByNombreContainingIgnoreCaseAndEstado(String nombre, String estado);
    
    // 🎓 QUERIES PARA ESTUDIANTES (asumiendo tabla intermedia aula_estudiantes)
    
    /**
     * Obtener aulas donde está inscrito un estudiante
     * Requiere tabla intermedia: aula_estudiantes (aula_id, estudiante_id)
     */
    @Query("""
        SELECT DISTINCT av FROM AulaVirtual av 
        INNER JOIN AulaEstudiante ae ON av.id = ae.aulaId 
        WHERE ae.estudianteId = :estudianteId 
        AND av.estado = 'activa'
        ORDER BY av.fechaInicio DESC
    """)
    List<AulaVirtual> findAulasByEstudianteId(@Param("estudianteId") Long estudianteId);
    
    /**
     * Verificar si un estudiante está inscrito en un aula específica
     */
    @Query("""
        SELECT COUNT(ae) > 0 FROM AulaEstudiante ae 
        WHERE ae.estudianteId = :estudianteId 
        AND ae.aulaId = :aulaId
    """)
    boolean existsEstudianteInAula(@Param("estudianteId") Long estudianteId, @Param("aulaId") Long aulaId);
    
    /**
     * Contar total de estudiantes únicos de un profesor
     */
    @Query("""
        SELECT COUNT(DISTINCT ae.estudianteId) FROM AulaEstudiante ae 
        INNER JOIN AulaVirtual av ON ae.aulaId = av.id 
        WHERE av.profesorId = :profesorId 
        AND av.estado = 'activa'
    """)
    int countEstudiantesByProfesor(@Param("profesorId") Long profesorId);
    
    // 📊 QUERIES ADICIONALES ÚTILES
    
    /**
     * Obtener aulas activas de una carrera específica
     */
    @Query("""
        SELECT av FROM AulaVirtual av 
        WHERE av.carreraId = :carreraId 
        AND av.estado = 'activa'
        ORDER BY av.fechaInicio DESC
    """)
    List<AulaVirtual> findByCarreraIdAndEstadoActiva(@Param("carreraId") Long carreraId);
    
    /**
     * Buscar aulas por ciclo y estado
     */
    List<AulaVirtual> findByCicloAndEstadoOrderByFechaInicioDesc(String ciclo, String estado);
    
    /**
     * Obtener estadísticas de aulas por profesor
     */
    @Query("""
        SELECT COUNT(av) FROM AulaVirtual av 
        WHERE av.profesorId = :profesorId 
        AND av.estado = :estado
    """)
    long countByProfesorIdAndEstado(@Param("profesorId") Long profesorId, @Param("estado") String estado);
    
    /**
     * Buscar aulas por múltiples criterios
     */
    @Query("""
        SELECT av FROM AulaVirtual av 
        WHERE (:profesorId IS NULL OR av.profesorId = :profesorId)
        AND (:estado IS NULL OR av.estado = :estado)
        AND (:carreraId IS NULL OR av.carreraId = :carreraId)
        AND (:ciclo IS NULL OR av.ciclo = :ciclo)
        ORDER BY av.fechaInicio DESC
    """)
    List<AulaVirtual> findByMultipleCriteria(
        @Param("profesorId") Long profesorId,
        @Param("estado") String estado,
        @Param("carreraId") Long carreraId,
        @Param("ciclo") String ciclo
    );
    
    // 🔍 QUERIES DE VALIDACIÓN
    
    /**
     * Verificar si un profesor ya tiene un aula con el mismo nombre
     */
    boolean existsByNombreAndProfesorIdAndEstado(String nombre, Long profesorId, String estado);
    
    /**
     * Obtener aulas que terminan pronto (para notificaciones)
     */
    @Query("""
        SELECT av FROM AulaVirtual av 
        WHERE av.fechaFin <= CURRENT_DATE + :dias 
        AND av.estado = 'activa'
        ORDER BY av.fechaFin ASC
    """)
    List<AulaVirtual> findAulasTerminandoPronto(@Param("dias") int dias);
}