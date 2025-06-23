package com.usuario.backend.repository.comunicacion;

import com.usuario.backend.model.entity.comunicacion.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    
    // Buscar anuncios activos
    List<Anuncio> findByActivoTrue();
    
    // Anuncios por tipo
    List<Anuncio> findByTipoAndActivoTrue(Anuncio.TipoAnuncio tipo);
    
    // Anuncios por prioridad
    List<Anuncio> findByPrioridadAndActivoTrue(Anuncio.Prioridad prioridad);
    
    // Anuncios de un usuario (profesor)
    List<Anuncio> findByUsuarioIdAndActivoTrueOrderByFechaPublicacionDesc(Long usuarioId);
    
    // Anuncios de una sección específica
    List<Anuncio> findBySeccionIdAndActivoTrueOrderByFechaPublicacionDesc(Long seccionId);
    
    // Anuncios de un curso específico
    List<Anuncio> findByCursoIdAndActivoTrueOrderByFechaPublicacionDesc(Long cursoId);
    
    // Anuncios generales (más recientes primero)
    List<Anuncio> findByTipoAndActivoTrueOrderByFechaPublicacionDesc(Anuncio.TipoAnuncio tipo);
    
    // Anuncios por prioridad alta
    @Query("SELECT a FROM Anuncio a WHERE a.prioridad = 'ALTA' AND a.activo = true " +
           "AND (a.fechaVencimiento IS NULL OR a.fechaVencimiento > :fecha) " +
           "ORDER BY a.fechaPublicacion DESC")
    List<Anuncio> findAnunciosUrgentes(@Param("fecha") LocalDateTime fecha);
    
    // Anuncios vigentes (no vencidos)
    @Query("SELECT a FROM Anuncio a WHERE a.activo = true " +
           "AND (a.fechaVencimiento IS NULL OR a.fechaVencimiento > :fecha) " +
           "ORDER BY a.fechaPublicacion DESC")
    List<Anuncio> findAnunciosVigentes(@Param("fecha") LocalDateTime fecha);
}