// =================================================================
// Seccion.java - Entidad de secciones (formato XC##Y)
// =================================================================
package com.usuario.backend.model.entity.academic;

import com.usuario.backend.model.entity.core.Carrera;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "secciones")
public class Seccion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "letra", nullable = false, length = 1)
    private String letra; // A, B, C, D
    
    @Column(name = "ciclo", nullable = false)
    private Integer ciclo;
    
    @Column(name = "carrera_id", nullable = false)
    private Integer carreraId;
    
    @Column(name = "periodo_academico", length = 20)
    private String periodoAcademico; // Ej: 2025-1
    
    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima = 30;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", insertable = false, updatable = false)
    private Carrera carrera;

    // Constructores
    public Seccion() {}

    public Seccion(String letra, Integer ciclo, Integer carreraId, String periodoAcademico) {
        this.letra = letra;
        this.ciclo = ciclo;
        this.carreraId = carreraId;
        this.periodoAcademico = periodoAcademico;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (activo == null) activo = true;
        if (capacidadMaxima == null) capacidadMaxima = 30;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    /**
     * Genera el código completo de la sección en formato (X)C##(Y)
     * Ejemplo: 1C24A, 3C5B, 6C14A
     */
    public String getCodigoCompleto() {
        if (carrera != null && carrera.getCodigo() != null) {
            return ciclo + carrera.getCodigo() + letra;
        }
        // Fallback si no se ha cargado la relación
        return ciclo + "C??" + letra;
    }

    /**
     * Genera código con el código de carrera proporcionado
     */
    public String getCodigoCompleto(String codigoCarrera) {
        return ciclo + codigoCarrera + letra;
    }

    public String getNombreCompleto() {
        return "Sección " + letra + " - Ciclo " + ciclo;
    }

    public String getNombreCompletoConCarrera() {
        if (carrera != null) {
            return getCodigoCompleto() + " - " + carrera.getNombre();
        }
        return getNombreCompleto();
    }

    public boolean puedeAceptarMasEstudiantes(int cantidadActual) {
        return cantidadActual < capacidadMaxima;
    }

    public boolean estaActiva() {
        return activo != null && activo;
    }

    public void activar() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void desactivar() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean esPrimerCiclo() {
        return ciclo != null && ciclo == 1;
    }

    public boolean esUltimoCiclo() {
        return ciclo != null && ciclo == 6; // Asumiendo 6 ciclos máximo
    }

    public boolean perteneceAPeriodo(String periodo) {
        return periodoAcademico != null && periodoAcademico.equals(periodo);
    }

    public String getInformacionResumida() {
        StringBuilder info = new StringBuilder();
        info.append(getCodigoCompleto());
        info.append(" (").append(capacidadMaxima).append(" estudiantes máx.)");
        if (periodoAcademico != null) {
            info.append(" - ").append(periodoAcademico);
        }
        if (!estaActiva()) {
            info.append(" [INACTIVA]");
        }
        return info.toString();
    }

    // Métodos para validación de letra de sección
    public static boolean esLetraValida(String letra) {
        return letra != null && letra.matches("[A-D]");
    }

    public boolean esSeccionA() {
        return "A".equals(letra);
    }

    public boolean esSeccionB() {
        return "B".equals(letra);
    }

    public boolean esSeccionC() {
        return "C".equals(letra);
    }

    public boolean esSeccionD() {
        return "D".equals(letra);
    }

    // Getters y Setters
    public Integer getId() { 
        return id; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }

    public String getLetra() { 
        return letra; 
    }
    
    public void setLetra(String letra) { 
        this.letra = letra; 
    }

    public Integer getCiclo() { 
        return ciclo; 
    }
    
    public void setCiclo(Integer ciclo) { 
        this.ciclo = ciclo; 
    }

    public Integer getCarreraId() { 
        return carreraId; 
    }
    
    public void setCarreraId(Integer carreraId) { 
        this.carreraId = carreraId; 
    }

    public String getPeriodoAcademico() { 
        return periodoAcademico; 
    }
    
    public void setPeriodoAcademico(String periodoAcademico) { 
        this.periodoAcademico = periodoAcademico; 
    }

    public Integer getCapacidadMaxima() { 
        return capacidadMaxima; 
    }
    
    public void setCapacidadMaxima(Integer capacidadMaxima) { 
        this.capacidadMaxima = capacidadMaxima; 
    }

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

    public Carrera getCarrera() { 
        return carrera; 
    }
    
    public void setCarrera(Carrera carrera) { 
        this.carrera = carrera; 
    }

    @Override
    public String toString() {
        return "Seccion{" +
                "id=" + id +
                ", letra='" + letra + '\'' +
                ", ciclo=" + ciclo +
                ", carreraId=" + carreraId +
                ", periodoAcademico='" + periodoAcademico + '\'' +
                ", capacidadMaxima=" + capacidadMaxima +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Seccion seccion = (Seccion) obj;
        
        if (id != null) {
            return id.equals(seccion.id);
        }
        
        // Si no hay ID, comparar por campos únicos
        return letra.equals(seccion.letra) && 
               ciclo.equals(seccion.ciclo) && 
               carreraId.equals(seccion.carreraId) &&
               (periodoAcademico != null ? periodoAcademico.equals(seccion.periodoAcademico) : seccion.periodoAcademico == null);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        
        int result = letra != null ? letra.hashCode() : 0;
        result = 31 * result + (ciclo != null ? ciclo.hashCode() : 0);
        result = 31 * result + (carreraId != null ? carreraId.hashCode() : 0);
        result = 31 * result + (periodoAcademico != null ? periodoAcademico.hashCode() : 0);
        return result;
    }
}