// Usuario/backend/backend/src/main/java/com/usuario/backend/repository/InvitacionAulaRepository.java
package com.usuario.backend.repository;

import com.usuario.backend.model.entity.InvitacionAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitacionAulaRepository extends JpaRepository<InvitacionAula, Long> {
    
    // Buscar invitaciones por correo del invitado
    List<InvitacionAula> findByCorreoInvitado(String correoInvitado);
    
    // Buscar invitaciones por aula
    List<InvitacionAula> findByAulaVirtualId(Long aulaVirtualId);
    
    // Buscar invitación por código único
    InvitacionAula findByCodigoInvitacion(String codigoInvitacion);
    
    // Buscar invitaciones por correo y estado
    List<InvitacionAula> findByCorreoInvitadoAndEstado(String correoInvitado, String estado);
    
    // Buscar invitaciones pendientes por correo
    @Query("SELECT i FROM InvitacionAula i WHERE i.correoInvitado = ?1 AND i.estado = 'pendiente' AND i.fechaExpiracion > CURRENT_TIMESTAMP")
    List<InvitacionAula> findInvitacionesPendientesByCorreo(String correoInvitado);
    
    // Contar invitaciones por aula y estado
    Long countByAulaVirtualIdAndEstado(Long aulaVirtualId, String estado);
}