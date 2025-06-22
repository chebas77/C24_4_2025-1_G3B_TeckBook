package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aula_estudiantes")
public class AulaEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "aula_id", nullable = false)
    private Long aulaId;
    
    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;
    
    @Column(name = "profesor_id")
    private Long profesorId;
    
    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;
    
    @Column(name = "estado")
    private String estado; // ACTIVO, INACTIVO, COMPLETADO
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructores
    public AulaEstudiante() {}

    public AulaEstudiante(Long aulaId, Long estudianteId, Long profesorId) {
        this.aulaId = aulaId;
        this.estudianteId = estudianteId;
        this.profesorId = profesorId;
        this.estado = "ACTIVO";
        this.fechaInscripcion = LocalDateTime.now();
    }

    // Timestamps automáticos
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaInscripcion == null) {
            fechaInscripcion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "ACTIVO";
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

    public Long getAulaId() {
        return aulaId;
    }

    public void setAulaId(Long aulaId) {
        this.aulaId = aulaId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    @Override
    public String toString() {
        return "AulaEstudiante{" +
                "id=" + id +
                ", aulaId=" + aulaId +
                ", estudianteId=" + estudianteId +
                ", profesorId=" + profesorId +
                ", fechaInscripcion=" + fechaInscripcion +
                ", estado='" + estado + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}