package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
    
    @Column(name = "anuncio_id", nullable = false)
    private Long anuncioId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(name = "comentario_padre_id")
    private Long comentarioPadreId;
    
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaActualizacion;
    
    // Constructor por defecto
    public Comentario() {}
    
    // Constructor con parámetros
    public Comentario(Integer usuarioId, Long anuncioId, String contenido) {
        this.usuarioId = usuarioId;
        this.anuncioId = anuncioId;
        this.contenido = contenido;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Constructor para respuestas
    public Comentario(Integer usuarioId, Long anuncioId, String contenido, Long comentarioPadreId) {
        this(usuarioId, anuncioId, contenido);
        this.comentarioPadreId = comentarioPadreId;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    
    public Long getAnuncioId() { return anuncioId; }
    public void setAnuncioId(Long anuncioId) { this.anuncioId = anuncioId; }
    
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    
    public Long getComentarioPadreId() { return comentarioPadreId; }
    public void setComentarioPadreId(Long comentarioPadreId) { this.comentarioPadreId = comentarioPadreId; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}