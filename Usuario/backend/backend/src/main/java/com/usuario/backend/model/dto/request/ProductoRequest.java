package com.usuario.backend.model.dto.request;
import com.usuario.backend.model.entity.marketplace.Producto.EstadoProducto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public class ProductoRequest {
    
    @NotBlank(message = "El nombre del producto es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es requerido")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "99999.99", message = "El precio no puede exceder 99,999.99")
    private BigDecimal precio;
    
    private String imagenUrl;
    
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;
    
    @NotNull(message = "El estado del producto es requerido")
    private EstadoProducto estadoProducto;

    // Constructores
    public ProductoRequest() {}

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public EstadoProducto getEstadoProducto() { return estadoProducto; }
    public void setEstadoProducto(EstadoProducto estadoProducto) { this.estadoProducto = estadoProducto; }
}
