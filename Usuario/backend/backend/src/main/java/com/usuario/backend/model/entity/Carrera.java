package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;
    
    @Column(name = "departamento_id", nullable = false)
    private Long departamentoId;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // 🔧 CAMPOS OPCIONALES (no están en tu BD actual pero pueden ser útiles)
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "duracion_ciclos")
    private Integer duracionCiclos;
    
    @Column(name = "modalidad", length = 50)
    private String modalidad;
    
    // 🔧 TIMESTAMPS - NO ESTÁN EN TU BD ACTUAL
    // Comentados para evitar errores de columnas inexistentes
    /*
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    */

    // ========== CONSTRUCTORES ==========
    
    public Carrera() {}

    public Carrera(String nombre, String codigo, Long departamentoId) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.departamentoId = departamentoId;
        this.activo = true;
    }

    public Carrera(String nombre, String codigo, Long departamentoId, String descripcion, Integer duracionCiclos) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.departamentoId = departamentoId;
        this.descripcion = descripcion;
        this.duracionCiclos = duracionCiclos;
        this.activo = true;
    }

    
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
        this.nombre = nombre != null ? nombre.trim() : null;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo != null ? codigo.trim().toUpperCase() : null;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo != null ? activo : true;
    }

    // 🔧 CAMPOS OPCIONALES
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : null;
    }

    public Integer getDuracionCiclos() {
        return duracionCiclos;
    }

    public void setDuracionCiclos(Integer duracionCiclos) {
        this.duracionCiclos = duracionCiclos;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad != null ? modalidad.trim() : null;
    }


    public boolean isActiva() {
        return activo != null && activo;
    }

    /**
     * 📝 Obtiene el nombre completo con código
     */
    public String getNombreCompleto() {
        if (codigo != null && !codigo.isEmpty()) {
            return nombre + " (" + codigo + ")";
        }
        return nombre;
    }

    /**
     * 🔍 Verifica si los datos básicos están completos
     */
    public boolean isDatosCompletos() {
        return nombre != null && !nombre.trim().isEmpty() &&
               codigo != null && !codigo.trim().isEmpty() &&
               departamentoId != null && departamentoId > 0;
    }

    // ========== MÉTODOS OBJECT ==========
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Carrera carrera = (Carrera) obj;
        
        if (id != null) {
            return id.equals(carrera.id);
        }
        
        // Si no hay ID, comparar por código único
        return codigo != null && codigo.equals(carrera.codigo);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return codigo != null ? codigo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Carrera{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", departamentoId=" + departamentoId +
                ", activo=" + activo +
                (descripcion != null ? ", descripcion='" + descripcion + '\'' : "") +
                (duracionCiclos != null ? ", duracionCiclos=" + duracionCiclos : "") +
                (modalidad != null ? ", modalidad='" + modalidad + '\'' : "") +
                '}';
    }
}