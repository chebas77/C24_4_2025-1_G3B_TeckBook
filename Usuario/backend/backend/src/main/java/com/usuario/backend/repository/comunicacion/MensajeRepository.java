package com.usuario.backend.repository.comunicacion;


import com.usuario.backend.model.entity.comunicacion.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    // Mensajes de una conversación (ordenados por fecha)
    List<Mensaje> findByConversacionIdOrderByFechaEnvio(Long conversacionId);
    
    // Mensajes de un remitente
    List<Mensaje> findByRemitenteIdOrderByFechaEnvioDesc(Long remitenteId);
    
    // Mensajes por tipo
    List<Mensaje> findByTipoMensaje(Mensaje.TipoMensaje tipoMensaje);
    
    // Mensajes no leídos de una conversación
    List<Mensaje> findByConversacionIdAndLeidoFalse(Long conversacionId);
    
    // Mensajes no leídos para un usuario específico
    @Query("SELECT m FROM Mensaje m JOIN Conversacion c ON m.conversacionId = c.id " +
           "WHERE (c.usuario1Id = :usuarioId OR c.usuario2Id = :usuarioId) " +
           "AND m.remitenteId != :usuarioId AND m.leido = false " +
           "ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findMensajesNoLeidosParaUsuario(@Param("usuarioId") Long usuarioId);
    
    // Contar mensajes no leídos por conversación
    Long countByConversacionIdAndLeidoFalse(Long conversacionId);
    
    // Contar mensajes no leídos para un usuario
    @Query("SELECT COUNT(m) FROM Mensaje m JOIN Conversacion c ON m.conversacionId = c.id " +
           "WHERE (c.usuario1Id = :usuarioId OR c.usuario2Id = :usuarioId) " +
           "AND m.remitenteId != :usuarioId AND m.leido = false")
    Long countMensajesNoLeidosParaUsuario(@Param("usuarioId") Long usuarioId);
    
    // Último mensaje de una conversación
    @Query("SELECT m FROM Mensaje m WHERE m.conversacionId = :conversacionId " +
           "ORDER BY m.fechaEnvio DESC LIMIT 1")
    Mensaje findUltimoMensajeByConversacion(@Param("conversacionId") Long conversacionId);
}