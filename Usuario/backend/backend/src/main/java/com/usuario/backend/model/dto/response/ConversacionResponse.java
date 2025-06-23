package com.usuario.backend.model.dto.response;
import java.time.LocalDateTime;

public class ConversacionResponse {
    
    private Integer id;
    private Integer otroUsuarioId;
    private String otroUsuarioNombre;
    private String otroUsuarioImagenUrl;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private Boolean tieneNoLeidos;
    private Integer cantidadNoLeidos;

    // Constructores
    public ConversacionResponse() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOtroUsuarioId() { return otroUsuarioId; }
    public void setOtroUsuarioId(Integer otroUsuarioId) { this.otroUsuarioId = otroUsuarioId; }

    public String getOtroUsuarioNombre() { return otroUsuarioNombre; }
    public void setOtroUsuarioNombre(String otroUsuarioNombre) { this.otroUsuarioNombre = otroUsuarioNombre; }

    public String getOtroUsuarioImagenUrl() { return otroUsuarioImagenUrl; }
    public void setOtroUsuarioImagenUrl(String otroUsuarioImagenUrl) { this.otroUsuarioImagenUrl = otroUsuarioImagenUrl; }

    public String getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(String ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public LocalDateTime getFechaUltimoMensaje() { return fechaUltimoMensaje; }
    public void setFechaUltimoMensaje(LocalDateTime fechaUltimoMensaje) { this.fechaUltimoMensaje = fechaUltimoMensaje; }

    public Boolean getTieneNoLeidos() { return tieneNoLeidos; }
    public void setTieneNoLeidos(Boolean tieneNoLeidos) { this.tieneNoLeidos = tieneNoLeidos; }

    public Integer getCantidadNoLeidos() { return cantidadNoLeidos; }
    public void setCantidadNoLeidos(Integer cantidadNoLeidos) { this.cantidadNoLeidos = cantidadNoLeidos; }
}
