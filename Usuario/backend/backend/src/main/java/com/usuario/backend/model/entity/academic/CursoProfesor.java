package com.usuario.backend.model.entity.academic;


import com.usuario.backend.model.entity.core.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "curso_profesor")
public class CursoProfesor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "curso_id", nullable = false)
    private Integer cursoId;
    
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
    @JoinColumn(name = "curso_id", insertable = false, updatable = false)
    private Curso curso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", insertable = false, updatable = false)
    private Usuario profesor;

    // Constructores
    public CursoProfesor() {}

    public CursoProfesor(Integer cursoId, Integer profesorId) {
        this.cursoId = cursoId;
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
    public void deshabilitarCurso() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void habilitarCurso() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean puedeEnsenarCurso() {
        return activo != null && activo;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }

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

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Usuario getProfesor() { return profesor; }
    public void setProfesor(Usuario profesor) { this.profesor = profesor; }

    @Override
    public String toString() {
        return "CursoProfesor{" +
                "id=" + id +
                ", cursoId=" + cursoId +
                ", profesorId=" + profesorId +
                ", activo=" + activo +
                '}';
    }
}