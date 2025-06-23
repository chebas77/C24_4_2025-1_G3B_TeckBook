package com.usuario.backend.model.entity.comunicacion;


import com.usuario.backend.model.entity.core.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "conversacion_id", nullable = false)
    private Integer conversacionId;
    
    @Column(name = "remitente_id", nullable = false)
    private Integer remitenteId;
    
    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mensaje", nullable = false)
    private TipoMensaje tipoMensaje = TipoMensaje.texto;
    
    @Column(name = "archivo_url")
    private String archivoUrl;
    
    @Column(name = "leido", nullable = false)
    private Boolean leido = false;
    
    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;
    
    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", insertable = false, updatable = false)
    private Conversacion conversacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remitente_id", insertable = false, updatable = false)
    private Usuario remitente;

    // Enum
    public enum TipoMensaje {
        texto, imagen, archivo
    }

    // Constructores
    public Mensaje() {}

    public Mensaje(Integer conversacionId, Integer remitenteId, String contenido) {
        this.conversacionId = conversacionId;
        this.remitenteId = remitenteId;
        this.contenido = contenido;
    }

    public Mensaje(Integer conversacionId, Integer remitenteId, String contenido, TipoMensaje tipoMensaje) {
        this.conversacionId = conversacionId;
        this.remitenteId = remitenteId;
        this.contenido = contenido;
        this.tipoMensaje = tipoMensaje;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaEnvio == null) fechaEnvio = LocalDateTime.now();
        if (leido == null) leido = false;
        if (tipoMensaje == null) tipoMensaje = TipoMensaje.texto;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public void marcarComoLeido() {
        this.leido = true;
        this.fechaLectura = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void marcarComoNoLeido() {
        this.leido = false;
        this.fechaLectura = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean estaLeido() {
        return leido != null && leido;
    }

    public boolean esTexto() {
        return tipoMensaje == TipoMensaje.texto;
    }

    public boolean esImagen() {
        return tipoMensaje == TipoMensaje.imagen;
    }

    public boolean esArchivo() {
        return tipoMensaje == TipoMensaje.archivo;
    }

    public boolean tieneArchivo() {
        return archivoUrl != null && !archivoUrl.trim().isEmpty();
    }

    public String getContenidoResumido(int longitudMaxima) {
        if (contenido == null) return "";
        return contenido.length() <= longitudMaxima ? 
               contenido : 
               contenido.substring(0, longitudMaxima) + "...";
    }

    public boolean fueEnviadoPor(Integer usuarioId) {
        return usuarioId.equals(remitenteId);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getConversacionId() { return conversacionId; }
    public void setConversacionId(Integer conversacionId) { this.conversacionId = conversacionId; }

    public Integer getRemitenteId() { return remitenteId; }
    public void setRemitenteId(Integer remitenteId) { this.remitenteId = remitenteId; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public TipoMensaje getTipoMensaje() { return tipoMensaje; }
    public void setTipoMensaje(TipoMensaje tipoMensaje) { this.tipoMensaje = tipoMensaje; }

    public String getArchivoUrl() { return archivoUrl; }
    public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }

    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }

    public LocalDateTime getFechaLectura() { return fechaLectura; }
    public void setFechaLectura(LocalDateTime fechaLectura) { this.fechaLectura = fechaLectura; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Conversacion getConversacion() { return conversacion; }
    public void setConversacion(Conversacion conversacion) { this.conversacion = conversacion; }

    public Usuario getRemitente() { return remitente; }
    public void setRemitente(Usuario remitente) { this.remitente = remitente; }

    @Override
    public String toString() {
        return "Mensaje{" +
                "id=" + id +
                ", conversacionId=" + conversacionId +
                ", remitenteId=" + remitenteId +
                ", tipoMensaje=" + tipoMensaje +
                ", leido=" + leido +
                ", fechaEnvio=" + fechaEnvio +
                '}';
    }
}