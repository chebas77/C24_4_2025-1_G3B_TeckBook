package com.usuario.backend.repository.marketplace;


import com.usuario.backend.model.entity.marketplace.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar productos disponibles
    List<Producto> findByDisponibleTrue();
    
    // Productos de un usuario
    List<Producto> findByUsuarioIdAndDisponibleTrueOrderByFechaPublicacionDesc(Long usuarioId);
    
    // Productos por categoría
    List<Producto> findByCategoriaAndDisponibleTrueOrderByFechaPublicacionDesc(String categoria);
    
    // Productos por estado
    List<Producto> findByEstadoProductoAndDisponibleTrueOrderByFechaPublicacionDesc(Producto.EstadoProducto estadoProducto);
    
    // Buscar por nombre (contiene)
    List<Producto> findByNombreContainingIgnoreCaseAndDisponibleTrueOrderByFechaPublicacionDesc(String nombre);
    
    // Buscar por descripción (contiene)
    List<Producto> findByDescripcionContainingIgnoreCaseAndDisponibleTrueOrderByFechaPublicacionDesc(String descripcion);
    
    // Productos por rango de precio
    List<Producto> findByPrecioBetweenAndDisponibleTrueOrderByPrecioAsc(BigDecimal precioMin, BigDecimal precioMax);
    
    // Productos más recientes
    @Query("SELECT p FROM Producto p WHERE p.disponible = true ORDER BY p.fechaPublicacion DESC LIMIT :limite")
    List<Producto> findProductosRecientes(@Param("limite") int limite);
    
    // Productos más baratos por categoría
    @Query("SELECT p FROM Producto p WHERE p.categoria = :categoria AND p.disponible = true ORDER BY p.precio ASC LIMIT :limite")
    List<Producto> findProductosBaratosPorCategoria(@Param("categoria") String categoria, @Param("limite") int limite);
    
    // Búsqueda general (nombre o descripción)
    @Query("SELECT p FROM Producto p WHERE p.disponible = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
           "ORDER BY p.fechaPublicacion DESC")
    List<Producto> buscarProductos(@Param("texto") String texto);
    
    // Contar productos por usuario
    Long countByUsuarioIdAndDisponibleTrue(Long usuarioId);
    
    // Contar productos por categoría
    Long countByCategoriaAndDisponibleTrue(String categoria);
    
    // Categorías disponibles (distintas)
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.disponible = true ORDER BY p.categoria")
    List<String> findCategoriasDisponibles();
}