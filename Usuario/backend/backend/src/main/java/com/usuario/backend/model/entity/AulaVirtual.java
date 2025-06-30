package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aulas_virtuales") // O el nombre real de tu tabla
public class AulaVirtual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "titulo")
    private String titulo;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "codigo_acceso", unique = true, nullable = false)
    private String codigoAcceso;
    
    @Column(name = "profesor_id", nullable = false)
    private Long profesorId;
    
    @Column(name = "seccion_id")
    private Long seccionId;
    
    @Column(name = "estado")
    private String estado; // 'activa', 'inactiva', 'finalizada'
    
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructores
    public AulaVirtual() {}

    public AulaVirtual(String nombre, String titulo, String descripcion, Long profesorId) {
        this.nombre = nombre;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.profesorId = profesorId;
        this.estado = "activa";
    }

    // Timestamps automáticos
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) {
            estado = "activa";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigoAcceso() {
        return codigoAcceso;
    }

    public void setCodigoAcceso(String codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Métodos útiles
    public boolean isActiva() {
        return "activa".equals(estado);
    }

    @Override
    public String toString() {
        return "AulaVirtual{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", codigoAcceso='" + codigoAcceso + '\'' +
                ", profesorId=" + profesorId +
                ", seccionId=" + seccionId +
                ", estado='" + estado + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}