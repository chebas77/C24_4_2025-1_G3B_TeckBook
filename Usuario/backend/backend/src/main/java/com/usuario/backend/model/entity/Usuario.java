package com.usuario.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "apellidos", nullable = false)
    private String apellidos;
    
    @Column(name = "correo_institucional", nullable = false, unique = true)
    private String correoInstitucional;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private RolUsuario rol;
    
    @Column(name = "ciclo_actual")
    private Integer cicloActual;
    
    @Column(name = "seccion_id")
    private Long seccionId;
    
    @Column(name = "carrera_id")
    private Long carreraId;
    
    @Column(name = "departamento_id")
    private Long departamentoId;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🔧 FIX: Enum con deserialización flexible
    public enum RolUsuario {
        ESTUDIANTE("estudiante"),
        PROFESOR("profesor"), 
        ADMIN("admin");
        
        private final String valor;
        
        RolUsuario(String valor) {
            this.valor = valor;
        }
        
        @JsonValue
        public String getValor() {
            return valor;
        }
        
        // 🎯 FIX PRINCIPAL: Permite deserializar tanto "estudiante" como "ESTUDIANTE"
        @JsonCreator
        public static RolUsuario fromString(String value) {
            if (value == null) return ESTUDIANTE;
            
            // Intentar por valor (estudiante, profesor, admin)
            for (RolUsuario rol : RolUsuario.values()) {
                if (rol.valor.equalsIgnoreCase(value)) {
                    return rol;
                }
            }
            
            // Intentar por nombre del enum (ESTUDIANTE, PROFESOR, ADMIN)
            for (RolUsuario rol : RolUsuario.values()) {
                if (rol.name().equalsIgnoreCase(value)) {
                    return rol;
                }
            }
            
            // Default a estudiante si no encuentra
            return ESTUDIANTE;
        }
    }

    // Constructor por defecto
    public Usuario() {}

    // 🔧 Timestamps automáticos
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========== GETTERS Y SETTERS ==========
    
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    // 🔧 MÉTODOS LEGACY para compatibilidad con código existente
    public String getRolString() {
        return rol != null ? rol.getValor() : "estudiante";
    }
    
    public void setRolString(String rolString) {
        this.rol = RolUsuario.fromString(rolString);
    }

    public Integer getCicloActual() {
        return cicloActual;
    }

    public void setCicloActual(Integer cicloActual) {
        this.cicloActual = cicloActual;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public Long getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(Long carreraId) {
        this.carreraId = carreraId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Verifica si el perfil está completo para acceder al sistema
     */
    public boolean isPerfilCompleto() {
        return nombre != null && !nombre.trim().isEmpty() &&
               apellidos != null && !apellidos.trim().isEmpty() &&
               correoInstitucional != null && !correoInstitucional.trim().isEmpty() &&
               carreraId != null &&
               cicloActual != null &&
               departamentoId != null;
    }

    /**
     * Verifica si faltan datos críticos después de OAuth2
     */
    public boolean requiereCompletarDatos() {
        return carreraId == null || cicloActual == null || departamentoId == null || seccionId == null || createdAt == null;
    }

    /**
     * Obtiene el nombre completo
     */
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", correoInstitucional='" + correoInstitucional + '\'' +
                ", rol=" + rol +
                ", cicloActual=" + cicloActual +
                ", carreraId=" + carreraId +
                ", departamentoId=" + departamentoId +
                ", seccionId=" + seccionId +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", telefono='" + telefono + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}