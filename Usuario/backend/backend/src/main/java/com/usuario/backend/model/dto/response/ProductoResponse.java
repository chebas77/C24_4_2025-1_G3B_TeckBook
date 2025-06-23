package com.usuario.backend.model.dto.response;

import com.usuario.backend.model.entity.marketplace.Producto.EstadoProducto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoResponse {
    
    private Integer id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer vendedorId;
    private String vendedorNombre;
    private String vendedorTelefono;
    private String categoria;
    private EstadoProducto estadoProducto;
    private Boolean disponible;
    private LocalDateTime fechaPublicacion;

    // Constructores
    public ProductoResponse() {}

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

    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }

    public String getVendedorNombre() { return vendedorNombre; }
    public void setVendedorNombre(String vendedorNombre) { this.vendedorNombre = vendedorNombre; }

    public String getVendedorTelefono() { return vendedorTelefono; }
    public void setVendedorTelefono(String vendedorTelefono) { this.vendedorTelefono = vendedorTelefono; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public EstadoProducto getEstadoProducto() { return estadoProducto; }
    public void setEstadoProducto(EstadoProducto estadoProducto) { this.estadoProducto = estadoProducto; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}