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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEstudiante estado = EstadoEstudiante.activo;
    
    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion;
    
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    public enum EstadoEstudiante {
        invitado, activo, inactivo
    }

    // Constructores
    public AulaEstudiante() {}

    public AulaEstudiante(Long aulaId, Long estudianteId) {
        this.aulaId = aulaId;
        this.estudianteId = estudianteId;
        this.estado = EstadoEstudiante.activo;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaUnion == null) {
            fechaUnion = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAulaId() { return aulaId; }
    public void setAulaId(Long aulaId) { this.aulaId = aulaId; }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }

    public EstadoEstudiante getEstado() { return estado; }
    public void setEstado(EstadoEstudiante estado) { this.estado = estado; }

    public LocalDateTime getFechaUnion() { return fechaUnion; }
    public void setFechaUnion(LocalDateTime fechaUnion) { this.fechaUnion = fechaUnion; }

    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }
}