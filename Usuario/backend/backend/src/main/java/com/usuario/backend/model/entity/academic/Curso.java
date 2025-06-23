package com.usuario.backend.model.entity.academic;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "curso")
public class Curso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "ciclo", nullable = false)
    private Integer ciclo;
    
    @Column(name = "creditos")
    private Integer creditos = 3;
    
    @Column(name = "horas_teoricas")
    private Integer horasTeoricas = 2;
    
    @Column(name = "horas_practicas")
    private Integer horasPracticas = 2;
    
    @Column(name = "departamento_id", nullable = false)
    private Integer departamentoId;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructores
    public Curso() {}

    public Curso(String nombre, String codigo, Integer ciclo, Integer departamentoId) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.ciclo = ciclo;
        this.departamentoId = departamentoId;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (activo == null) activo = true;
        if (creditos == null) creditos = 3;
        if (horasTeoricas == null) horasTeoricas = 2;
        if (horasPracticas == null) horasPracticas = 2;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombre + " (" + codigo + ")";
    }

    public Integer getTotalHoras() {
        return (horasTeoricas != null ? horasTeoricas : 0) + 
               (horasPracticas != null ? horasPracticas : 0);
    }

    public boolean esCursoPractico() {
        return horasPracticas != null && horasPracticas > horasTeoricas;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCiclo() { return ciclo; }
    public void setCiclo(Integer ciclo) { this.ciclo = ciclo; }

    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }

    public Integer getHorasTeoricas() { return horasTeoricas; }
    public void setHorasTeoricas(Integer horasTeoricas) { this.horasTeoricas = horasTeoricas; }

    public Integer getHorasPracticas() { return horasPracticas; }
    public void setHorasPracticas(Integer horasPracticas) { this.horasPracticas = horasPracticas; }

    public Integer getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(Integer departamentoId) { this.departamentoId = departamentoId; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Curso{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", ciclo=" + ciclo +
                ", creditos=" + creditos +
                ", horasTeoricas=" + horasTeoricas +
                ", horasPracticas=" + horasPracticas +
                ", departamentoId=" + departamentoId +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
