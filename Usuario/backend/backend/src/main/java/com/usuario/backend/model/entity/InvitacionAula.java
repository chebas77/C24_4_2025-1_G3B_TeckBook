package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitaciones_aula")
public class InvitacionAula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aula_virtual_id", nullable = false)
    private Long aulaVirtualId;

    @Column(name = "invitado_por_id", nullable = false)
    private Long invitadoPorId;

    @Column(name = "correo_invitado", nullable = false)
    private String correoInvitado;

    @Column(name = "codigo_invitacion", unique = true, nullable = false)
    private String codigoInvitacion;

    @Column(name = "estado")
    private String estado = "pendiente"; // pendiente, aceptada, rechazada, expirada

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "fecha_invitacion")
    private LocalDateTime fechaInvitacion;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    // Constructores
    public InvitacionAula() {}

    public InvitacionAula(Long aulaVirtualId, Long invitadoPorId, String correoInvitado, String mensaje) {
        this.aulaVirtualId = aulaVirtualId;
        this.invitadoPorId = invitadoPorId;
        this.correoInvitado = correoInvitado;
        this.mensaje = mensaje;
        this.fechaInvitacion = LocalDateTime.now();
        this.fechaExpiracion = LocalDateTime.now().plusDays(7); // Expira en 7 días
    }

    // Métodos de negocio
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public boolean isPendiente() {
        return "pendiente".equals(estado) && !isExpirada();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAulaVirtualId() {
        return aulaVirtualId;
    }

    public void setAulaVirtualId(Long aulaVirtualId) {
        this.aulaVirtualId = aulaVirtualId;
    }

    public Long getInvitadoPorId() {
        return invitadoPorId;
    }

    public void setInvitadoPorId(Long invitadoPorId) {
        this.invitadoPorId = invitadoPorId;
    }

    public String getCorreoInvitado() {
        return correoInvitado;
    }

    public void setCorreoInvitado(String correoInvitado) {
        this.correoInvitado = correoInvitado;
    }

    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }

    public void setCodigoInvitacion(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaInvitacion() {
        return fechaInvitacion;
    }

    public void setFechaInvitacion(LocalDateTime fechaInvitacion) {
        this.fechaInvitacion = fechaInvitacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}