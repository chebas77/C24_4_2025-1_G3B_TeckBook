package com.usuario.backend.model.dto.request;


import com.usuario.backend.model.entity.comunicacion.Mensaje.TipoMensaje;
import jakarta.validation.constraints.*;

public class MensajeRequest {
    
    @NotNull(message = "El destinatario es requerido")
    private Integer destinatarioId;
    
    @NotBlank(message = "El contenido del mensaje es requerido")
    @Size(max = 2000, message = "El contenido no puede exceder 2000 caracteres")
    private String contenido;
    
    private TipoMensaje tipoMensaje = TipoMensaje.texto;
    
    private String archivoUrl;

    // Constructores
    public MensajeRequest() {}

    // Getters y Setters
    public Integer getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(Integer destinatarioId) { this.destinatarioId = destinatarioId; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public TipoMensaje getTipoMensaje() { return tipoMensaje; }
    public void setTipoMensaje(TipoMensaje tipoMensaje) { this.tipoMensaje = tipoMensaje; }

    public String getArchivoUrl() { return archivoUrl; }
    public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }
}
