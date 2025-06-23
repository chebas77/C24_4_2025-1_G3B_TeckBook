package com.usuario.backend.model.entity.marketplace;

import com.usuario.backend.model.entity.core.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId; // ID del alumno que publica
    
    @Column(name = "categoria", length = 50)
    private String categoria;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_producto", nullable = false)
    private EstadoProducto estadoProducto = EstadoProducto.usado;
    
    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;
    
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion = LocalDateTime.now();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario vendedor;

    // Enum para estado del producto
    public enum EstadoProducto {
        nuevo, usado, regular
    }

    // Constructores
    public Producto() {}

    public Producto(String nombre, String descripcion, BigDecimal precio, Integer usuarioId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.usuarioId = usuarioId;
    }

    public Producto(String nombre, String descripcion, BigDecimal precio, Integer usuarioId, String categoria, EstadoProducto estadoProducto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.usuarioId = usuarioId;
        this.categoria = categoria;
        this.estadoProducto = estadoProducto;
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaPublicacion == null) fechaPublicacion = LocalDateTime.now();
        if (disponible == null) disponible = true;
        if (estadoProducto == null) estadoProducto = EstadoProducto.usado;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Métodos de utilidad
    public void marcarComoVendido() {
        this.disponible = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void marcarComoDisponible() {
        this.disponible = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean estaDisponible() {
        return disponible != null && disponible;
    }

    public boolean esProductoNuevo() {
        return estadoProducto == EstadoProducto.nuevo;
    }

    public boolean esProductoUsado() {
        return estadoProducto == EstadoProducto.usado;
    }

    public boolean esProductoRegular() {
        return estadoProducto == EstadoProducto.regular;
    }

    public boolean tieneImagen() {
        return imagenUrl != null && !imagenUrl.trim().isEmpty();
    }

    public String getEstadoTexto() {
        switch (estadoProducto) {
            case nuevo: return "Nuevo";
            case usado: return "Usado";
            case regular: return "Regular";
            default: return "Sin especificar";
        }
    }

    public String getDescripcionResumida(int longitudMaxima) {
        if (descripcion == null) return "";
        return descripcion.length() <= longitudMaxima ? 
               descripcion : 
               descripcion.substring(0, longitudMaxima) + "...";
    }

    public boolean perteneceAUsuario(Integer usuarioId) {
        return this.usuarioId.equals(usuarioId);
    }

    public boolean esPrecioNegociable(BigDecimal limitePrecio) {
        return precio.compareTo(limitePrecio) >= 0;
    }

    public String getInformacionCompleta() {
        StringBuilder info = new StringBuilder();
        info.append(nombre);
        info.append(" - S/").append(precio);
        info.append(" (").append(getEstadoTexto()).append(")");
        if (categoria != null) {
            info.append(" - ").append(categoria);
        }
        if (!estaDisponible()) {
            info.append(" [VENDIDO]");
        }
        return info.toString();
    }

    // Categorías comunes predefinidas
    public static class Categorias {
        public static final String LIBROS = "Libros";
        public static final String ELECTRONICOS = "Electrónicos";
        public static final String COMPONENTES = "Componentes";
        public static final String INSTRUMENTOS = "Instrumentos";
        public static final String HERRAMIENTAS = "Herramientas";
        public static final String SOFTWARE = "Software";
        public static final String APUNTES = "Apuntes";
        public static final String SEGURIDAD = "Seguridad";
        public static final String COMPUTADORAS = "Computadoras";
    }

    public boolean esDeCategoriaLibros() {
        return Categorias.LIBROS.equals(categoria);
    }

    public boolean esDeCategoriaElectronicos() {
        return Categorias.ELECTRONICOS.equals(categoria);
    }

    public boolean esDeCategoriaComponentes() {
        return Categorias.COMPONENTES.equals(categoria);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public EstadoProducto getEstadoProducto() { return estadoProducto; }
    public void setEstadoProducto(EstadoProducto estadoProducto) { this.estadoProducto = estadoProducto; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Usuario getVendedor() { return vendedor; }
    public void setVendedor(Usuario vendedor) { this.vendedor = vendedor; }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", categoria='" + categoria + '\'' +
                ", estadoProducto=" + estadoProducto +
                ", disponible=" + disponible +
                ", fechaPublicacion=" + fechaPublicacion +
                '}';
    }
}
