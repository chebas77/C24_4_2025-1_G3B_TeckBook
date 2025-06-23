package com.usuario.backend.model.entity.academic;


import com.usuario.backend.model.entity.core.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_seccion")
public class UsuarioSeccion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;
    
    @Column(name = "seccion_id", nullable = false)
    private Integer seccionId;
    
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
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id", insertable = false, updatable = false)
    private Seccion seccion;

    // Constructores
    public UsuarioSeccion() {}

    public UsuarioSeccion(Integer usuarioId, Integer seccionId) {
        this.usuarioId = usuarioId;
        this.seccionId = seccionId;
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

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getSeccionId() { return seccionId; }
    public void setSeccionId(Integer seccionId) { this.seccionId = seccionId; }

    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Seccion getSeccion() { return seccion; }
    public void setSeccion(Seccion seccion) { this.seccion = seccion; }

    @Override
    public String toString() {
        return "UsuarioSeccion{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", seccionId=" + seccionId +
                ", activo=" + activo +
                '}';
    }
}