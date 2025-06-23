package com.usuario.backend.model.entity.academic;


import com.usuario.backend.model.entity.core.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seccion_curso_profesor")
public class SeccionCursoProfesor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "seccion_id", nullable = false)
    private Integer seccionId;
    
    @Column(name = "curso_id", nullable = false)
    private Integer cursoId;
    
    @Column(name = "profesor_id", nullable = false)
    private Integer profesorId;
    
    @Column(name = "horario", length = 100)
    private String horario; // Ej: "Lunes 8:00-10:00"
    
    @Column(name = "aula", length = 20)
    private String aula; // Ej: "Lab-A1", "Aula-101"
    
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
    @JoinColumn(name = "curso_id", insertable = false, updatable = false)
    private Curso curso;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", insertable = false, updatable = false)
    private Usuario profesor;

    // Constructores
    public SeccionCursoProfesor() {}

    public SeccionCursoProfesor(Integer seccionId, Integer cursoId, Integer profesorId) {
        this.seccionId = seccionId;
        this.cursoId = cursoId;
        this.profesorId = profesorId;
    }

    public SeccionCursoProfesor(Integer seccionId, Integer cursoId, Integer profesorId, String horario, String aula) {
        this.seccionId = seccionId;
        this.cursoId = cursoId;
        this.profesorId = profesorId;
        this.horario = horario;
        this.aula = aula;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public void desactivarClase() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activarClase() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean estaClaseActiva() {
        return activo != null && activo;
    }

    public String getInformacionCompleta() {
        StringBuilder info = new StringBuilder();
        info.append("Curso: ").append(cursoId);
        info.append(" | Sección: ").append(seccionId);
        info.append(" | Profesor: ").append(profesorId);
        if (horario != null) info.append(" | Horario: ").append(horario);
        if (aula != null) info.append(" | Aula: ").append(aula);
        return info.toString();
    }

    public boolean tieneHorarioDefinido() {
        return horario != null && !horario.trim().isEmpty();
    }

    public boolean tieneAulaAsignada() {
        return aula != null && !aula.trim().isEmpty();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSeccionId() { return seccionId; }
    public void setSeccionId(Integer seccionId) { this.seccionId = seccionId; }

    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }

    public Integer getProfesorId() { return profesorId; }
    public void setProfesorId(Integer profesorId) { this.profesorId = profesorId; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getAula() { return aula; }
    public void setAula(String aula) { this.aula = aula; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Seccion getSeccion() { return seccion; }
    public void setSeccion(Seccion seccion) { this.seccion = seccion; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Usuario getProfesor() { return profesor; }
    public void setProfesor(Usuario profesor) { this.profesor = profesor; }

    @Override
    public String toString() {
        return "SeccionCursoProfesor{" +
                "id=" + id +
                ", seccionId=" + seccionId +
                ", cursoId=" + cursoId +
                ", profesorId=" + profesorId +
                ", horario='" + horario + '\'' +
                ", aula='" + aula + '\'' +
                ", activo=" + activo +
                '}';
    }
}