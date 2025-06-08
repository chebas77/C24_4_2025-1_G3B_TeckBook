// Usuario/backend/backend/src/main/java/com/usuario/backend/model/entity/Carrera.java
package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "codigo", unique = true)
    private String codigo;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "duracion_ciclos")
    private Integer duracionCiclos;
    
    @Column(name = "departamento_id")
    private Long departamentoId;
    
    @Column(name = "modalidad")
    private String modalidad;
    
    // 🔥 FIX: Cambiar de "activa" a "activo" para coincidir con tu BD
    @Column(name = "activo")
    private Boolean activo = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructores
    public Carrera() {}

    public Carrera(String nombre, String codigo, String descripcion, Integer duracionCiclos, Long departamentoId) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.duracionCiclos = duracionCiclos;
        this.departamentoId = departamentoId;
        this.activo = true;
    }

    // 🔥 TIMESTAMPS AUTOMÁTICOS
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracionCiclos() {
        return duracionCiclos;
    }

    public void setDuracionCiclos(Integer duracionCiclos) {
        this.duracionCiclos = duracionCiclos;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    // 🔥 FIX: Cambiar método para usar "activo"
    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
        return "Carrera{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", duracionCiclos=" + duracionCiclos +
                ", departamentoId=" + departamentoId +
                ", modalidad='" + modalidad + '\'' +
                ", activo=" + activo +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}