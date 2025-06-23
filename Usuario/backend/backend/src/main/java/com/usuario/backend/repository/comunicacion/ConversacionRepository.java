package com.usuario.backend.repository.comunicacion;


import com.usuario.backend.model.entity.comunicacion.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {
    
    // Buscar conversaciones activas
    List<Conversacion> findByActivoTrue();
    
    // Conversaciones de un usuario (como usuario1 o usuario2)
    @Query("SELECT c FROM Conversacion c WHERE (c.usuario1Id = :usuarioId OR c.usuario2Id = :usuarioId) " +
           "AND c.activo = true ORDER BY c.fechaUltimoMensaje DESC")
    List<Conversacion> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Buscar conversación específica entre dos usuarios
    @Query("SELECT c FROM Conversacion c WHERE " +
           "((c.usuario1Id = :usuario1Id AND c.usuario2Id = :usuario2Id) OR " +
           "(c.usuario1Id = :usuario2Id AND c.usuario2Id = :usuario1Id)) " +
           "AND c.activo = true")
    Optional<Conversacion> findByUsuarios(@Param("usuario1Id") Long usuario1Id, @Param("usuario2Id") Long usuario2Id);
    
    // Conversaciones ordenadas por último mensaje
    @Query("SELECT c FROM Conversacion c WHERE (c.usuario1Id = :usuarioId OR c.usuario2Id = :usuarioId) " +
           "AND c.activo = true AND c.fechaUltimoMensaje IS NOT NULL " +
           "ORDER BY c.fechaUltimoMensaje DESC")
    List<Conversacion> findConversacionesRecientes(@Param("usuarioId") Long usuarioId);
    
    // Contar conversaciones activas de un usuario
    @Query("SELECT COUNT(c) FROM Conversacion c WHERE (c.usuario1Id = :usuarioId OR c.usuario2Id = :usuarioId) AND c.activo = true")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
}