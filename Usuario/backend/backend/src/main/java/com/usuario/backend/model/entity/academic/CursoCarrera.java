package com.usuario.backend.model.entity.academic;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.usuario.backend.model.entity.core.Carrera;
@Entity
@Table(name = "curso_carrera")
public class CursoCarrera {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "curso_id", nullable = false)
    private Integer cursoId;
    
    @Column(name = "carrera_id", nullable = false)
    private Integer carreraId;
    
    @Column(name = "ciclo_sugerido")
    private Integer cicloSugerido;
    
    @Column(name = "es_obligatorio", nullable = false)
    private Boolean esObligatorio = true;
    
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
    @JoinColumn(name = "carrera_id", insertable = false, updatable = false)
    private Carrera carrera;

    // Constructores
    public CursoCarrera() {}

    public CursoCarrera(Integer cursoId, Integer carreraId, Integer cicloSugerido, Boolean esObligatorio) {
        this.cursoId = cursoId;
        this.carreraId = carreraId;
        this.cicloSugerido = cicloSugerido;
        this.esObligatorio = esObligatorio;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (activo == null) activo = true;
        if (esObligatorio == null) esObligatorio = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public boolean esCursoElectivo() {
        return !esObligatorio;
    }

    public String getTipoCurso() {
        return esObligatorio ? "Obligatorio" : "Electivo";
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }

    public Integer getCarreraId() { return carreraId; }
    public void setCarreraId(Integer carreraId) { this.carreraId = carreraId; }

    public Integer getCicloSugerido() { return cicloSugerido; }
    public void setCicloSugerido(Integer cicloSugerido) { this.cicloSugerido = cicloSugerido; }

    public Boolean getEsObligatorio() { return esObligatorio; }
    public void setEsObligatorio(Boolean esObligatorio) { this.esObligatorio = esObligatorio; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    @Override
    public String toString() {
        return "CursoCarrera{" +
                "id=" + id +
                ", cursoId=" + cursoId +
                ", carreraId=" + carreraId +
                ", cicloSugerido=" + cicloSugerido +
                ", esObligatorio=" + esObligatorio +
                '}';
    }
}
