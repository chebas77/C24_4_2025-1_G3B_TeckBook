package com.usuario.backend.model.entity.academic;

import com.usuario.backend.model.entity.core.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seccion_profesor")
public class SeccionProfesor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "seccion_id", nullable = false)
    private Integer seccionId;
    
    @Column(name = "profesor_id", nullable = false)
    private Integer profesorId;
    
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion = LocalDateTime.now();
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id", insertable = false, updatable = false)
    private Seccion seccion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", insertable = false, updatable = false)
    private Usuario profesor;

    // Constructores
    public SeccionProfesor() {}

    public SeccionProfesor(Integer seccionId, Integer profesorId) {
        this.seccionId = seccionId;
        this.profesorId = profesorId;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaAsignacion == null) fechaAsignacion = LocalDateTime.now();
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public void desasignarDeSeccion() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void reasignarASeccion() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean estaActivamenteAsignado() {
        return activo != null && activo;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSeccionId() { return seccionId; }
    public void setSeccionId(Integer seccionId) { this.seccionId = seccionId; }

    public Integer getProfesorId() { return profesorId; }
    public void setProfesorId(Integer profesorId) { this.profesorId = profesorId; }

    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Seccion getSeccion() { return seccion; }
    public void setSeccion(Seccion seccion) { this.seccion = seccion; }

    public Usuario getProfesor() { return profesor; }
    public void setProfesor(Usuario profesor) { this.profesor = profesor; }

    @Override
    public String toString() {
        return "SeccionProfesor{" +
                "id=" + id +
                ", seccionId=" + seccionId +
                ", profesorId=" + profesorId +
                ", activo=" + activo +
                '}';
    }
}
