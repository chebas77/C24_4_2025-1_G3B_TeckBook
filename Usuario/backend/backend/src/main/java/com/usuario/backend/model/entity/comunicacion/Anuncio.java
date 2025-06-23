package com.usuario.backend.model.entity.comunicacion;

import com.usuario.backend.model.entity.core.Usuario;
import com.usuario.backend.model.entity.academic.Seccion;
import com.usuario.backend.model.entity.academic.Curso;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anuncios")
public class Anuncio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;
    
    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId; // ID del profesor que publica
    
    @Column(name = "seccion_id")
    private Integer seccionId; // Opcional: anuncio específico para una sección
    
    @Column(name = "curso_id")
    private Integer cursoId; // Opcional: anuncio específico para un curso
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoAnuncio tipo = TipoAnuncio.general;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false)
    private PrioridadAnuncio prioridad = PrioridadAnuncio.media;
    
    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion = LocalDateTime.now();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario autor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id", insertable = false, updatable = false)
    private Seccion seccion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", insertable = false, updatable = false)
    private Curso curso;

    // Enums
    public enum TipoAnuncio {
        general, curso, seccion
    }

    public enum PrioridadAnuncio {
        baja, media, alta
    }

    // Constructores
    public Anuncio() {}

    public Anuncio(String titulo, String contenido, Integer usuarioId) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.usuarioId = usuarioId;
    }

    public Anuncio(String titulo, String contenido, Integer usuarioId, TipoAnuncio tipo, PrioridadAnuncio prioridad) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.prioridad = prioridad;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaPublicacion == null) fechaPublicacion = LocalDateTime.now();
        if (activo == null) activo = true;
        if (tipo == null) tipo = TipoAnuncio.general;
        if (prioridad == null) prioridad = PrioridadAnuncio.media;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public boolean estaVigente() {
        return activo && (fechaVencimiento == null || fechaVencimiento.isAfter(LocalDateTime.now()));
    }

    public boolean estaVencido() {
        return fechaVencimiento != null && fechaVencimiento.isBefore(LocalDateTime.now());
    }

    public void archivar() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactivar() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean esAnuncioGeneral() {
        return tipo == TipoAnuncio.general;
    }

    public boolean esAnuncioDeCurso() {
        return tipo == TipoAnuncio.curso && cursoId != null;
    }

    public boolean esAnuncioDeSeccion() {
        return tipo == TipoAnuncio.seccion && seccionId != null;
    }

    public boolean esAltaPrioridad() {
        return prioridad == PrioridadAnuncio.alta;
    }

    public String getResumen(int longitudMaxima) {
        if (contenido == null) return "";
        return contenido.length() <= longitudMaxima ? 
               contenido : 
               contenido.substring(0, longitudMaxima) + "...";
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

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

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public Seccion getSeccion() { return seccion; }
    public void setSeccion(Seccion seccion) { this.seccion = seccion; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    @Override
    public String toString() {
        return "Anuncio{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tipo=" + tipo +
                ", prioridad=" + prioridad +
                ", activo=" + activo +
                ", fechaPublicacion=" + fechaPublicacion +
                '}';
    }
}