package com.usuario.backend.model.dto.request;

import com.usuario.backend.model.entity.comunicacion.Anuncio.TipoAnuncio;
import com.usuario.backend.model.entity.comunicacion.Anuncio.PrioridadAnuncio;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class AnuncioRequest {
    
    @NotBlank(message = "El título es requerido")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    private String titulo;
    
    @NotBlank(message = "El contenido es requerido")
    @Size(max = 5000, message = "El contenido no puede exceder 5000 caracteres")
    private String contenido;
    
    private Integer seccionId;
    
    private Integer cursoId;
    
    @NotNull(message = "El tipo de anuncio es requerido")
    private TipoAnuncio tipo;
    
    private PrioridadAnuncio prioridad = PrioridadAnuncio.media;
    
    @Future(message = "La fecha de vencimiento debe ser en el futuro")
    private LocalDateTime fechaVencimiento;

    // Constructores
    public AnuncioRequest() {}

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Integer getSeccionId() { return seccionId; }
    public void setSeccionId(Integer seccionId) { this.seccionId = seccionId; }

    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }

    public TipoAnuncio getTipo() { return tipo; }
    public void setTipo(TipoAnuncio tipo) { this.tipo = tipo; }

    public PrioridadAnuncio getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadAnuncio prioridad) { this.prioridad = prioridad; }

    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDateTime fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}