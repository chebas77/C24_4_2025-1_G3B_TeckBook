package com.usuario.backend.model.entity.comunicacion;

import com.usuario.backend.model.entity.core.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversaciones")
public class Conversacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "usuario1_id", nullable = false)
    private Integer usuario1Id;
    
    @Column(name = "usuario2_id", nullable = false)
    private Integer usuario2Id;
    
    @Column(name = "ultimo_mensaje_id")
    private Integer ultimoMensajeId;
    
    @Column(name = "fecha_ultimo_mensaje")
    private LocalDateTime fechaUltimoMensaje;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio = LocalDateTime.now();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario1_id", insertable = false, updatable = false)
    private Usuario usuario1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario2_id", insertable = false, updatable = false)
    private Usuario usuario2;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ultimo_mensaje_id", insertable = false, updatable = false)
    private Mensaje ultimoMensaje;
    
    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mensaje> mensajes;

    // Constructores
    public Conversacion() {}

    public Conversacion(Integer usuario1Id, Integer usuario2Id) {
        // Asegurar orden consistente para evitar duplicados
        if (usuario1Id < usuario2Id) {
            this.usuario1Id = usuario1Id;
            this.usuario2Id = usuario2Id;
        } else {
            this.usuario1Id = usuario2Id;
            this.usuario2Id = usuario1Id;
        }
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaInicio == null) fechaInicio = LocalDateTime.now();
        if (activo == null) activo = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public void actualizarUltimoMensaje(Integer mensajeId, LocalDateTime fechaMensaje) {
        this.ultimoMensajeId = mensajeId;
        this.fechaUltimoMensaje = fechaMensaje;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean participaUsuario(Integer usuarioId) {
        return usuarioId.equals(usuario1Id) || usuarioId.equals(usuario2Id);
    }

    public Integer getOtroUsuario(Integer usuarioId) {
        if (usuarioId.equals(usuario1Id)) {
            return usuario2Id;
        } else if (usuarioId.equals(usuario2Id)) {
            return usuario1Id;
        }
        return null;
    }

    public void archivar() {
        this.activo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactivar() {
        this.activo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean estaActiva() {
        return activo != null && activo;
    }

    public int getCantidadMensajes() {
        return mensajes != null ? mensajes.size() : 0;
    }

    public boolean tieneActividad() {
        return fechaUltimoMensaje != null;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuario1Id() { return usuario1Id; }
    public void setUsuario1Id(Integer usuario1Id) { this.usuario1Id = usuario1Id; }

    public Integer getUsuario2Id() { return usuario2Id; }
    public void setUsuario2Id(Integer usuario2Id) { this.usuario2Id = usuario2Id; }

    public Integer getUltimoMensajeId() { return ultimoMensajeId; }
    public void setUltimoMensajeId(Integer ultimoMensajeId) { this.ultimoMensajeId = ultimoMensajeId; }

    public LocalDateTime getFechaUltimoMensaje() { return fechaUltimoMensaje; }
    public void setFechaUltimoMensaje(LocalDateTime fechaUltimoMensaje) { this.fechaUltimoMensaje = fechaUltimoMensaje; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Usuario getUsuario1() { return usuario1; }
    public void setUsuario1(Usuario usuario1) { this.usuario1 = usuario1; }

    public Usuario getUsuario2() { return usuario2; }
    public void setUsuario2(Usuario usuario2) { this.usuario2 = usuario2; }

    public Mensaje getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(Mensaje ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }

    @Override
    public String toString() {
        return "Conversacion{" +
                "id=" + id +
                ", usuario1Id=" + usuario1Id +
                ", usuario2Id=" + usuario2Id +
                ", activo=" + activo +
                ", fechaUltimoMensaje=" + fechaUltimoMensaje +
                '}';
    }
}
