package com.usuario.backend.model.entity.academic;

import com.usuario.backend.model.entity.core.Usuario;
import com.usuario.backend.model.entity.core.Carrera;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "profesor_carrera")
public class ProfesorCarrera {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "profesor_id", nullable = false)
    private Integer profesorId;
    
    @Column(name = "carrera_id", nullable = false)
    private Integer carreraId;
    
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
    @JoinColumn(name = "profesor_id", insertable = false, updatable = false)
    private Usuario profesor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", insertable = false, updatable = false)
    private Carrera carrera;

    // Constructores
    public ProfesorCarrera() {}

    public ProfesorCarrera(Integer profesorId, Integer carreraId) {
        this.profesorId = profesorId;
        this.carreraId = carreraId;
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
    public void deshabilitarAsignacion() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void habilitarAsignacion() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean puedeEnsenarEnCarrera() {
        return activo != null && activo;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProfesorId() { return profesorId; }
    public void setProfesorId(Integer profesorId) { this.profesorId = profesorId; }

    public Integer getCarreraId() { return carreraId; }
    public void setCarreraId(Integer carreraId) { this.carreraId = carreraId; }

    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Usuario getProfesor() { return profesor; }
    public void setProfesor(Usuario profesor) { this.profesor = profesor; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    @Override
    public String toString() {
        return "ProfesorCarrera{" +
                "id=" + id +
                ", profesorId=" + profesorId +
                ", carreraId=" + carreraId +
                ", activo=" + activo +
                '}';
    }
}