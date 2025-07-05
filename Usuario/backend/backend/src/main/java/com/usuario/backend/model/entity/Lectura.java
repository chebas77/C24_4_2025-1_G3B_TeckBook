package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "anuncio_id"}))
public class Lectura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
    
    @Column(name = "anuncio_id", nullable = false)
    private Long anuncioId;
    
    @Column(name = "fecha_lectura")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaLectura;
    
    // Constructor por defecto
    public Lectura() {}
    
    // Constructor con parámetros
    public Lectura(Integer usuarioId, Long anuncioId) {
        this.usuarioId = usuarioId;
        this.anuncioId = anuncioId;
        this.fechaLectura = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    
    public Long getAnuncioId() { return anuncioId; }
    public void setAnuncioId(Long anuncioId) { this.anuncioId = anuncioId; }
    
    public LocalDateTime getFechaLectura() { return fechaLectura; }
    public void setFechaLectura(LocalDateTime fechaLectura) { this.fechaLectura = fechaLectura; }
}