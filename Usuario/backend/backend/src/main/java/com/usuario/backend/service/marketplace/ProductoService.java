package com.usuario.backend.service.marketplace;
import com.usuario.backend.model.entity.marketplace.Producto;
import com.usuario.backend.repository.marketplace.ProductoRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // CRUD básico
    public List<Producto> findAll() {
        return productoRepository.findByDisponibleTrue();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setDisponible(false);
            productoRepository.save(producto);
        });
    }

    // Métodos de búsqueda específicos
    public List<Producto> findByUsuario(Long usuarioId) {
        return productoRepository.findByUsuarioIdAndDisponibleTrueOrderByFechaPublicacionDesc(usuarioId);
    }

    public List<Producto> findByCategoria(String categoria) {
        return productoRepository.findByCategoriaAndDisponibleTrueOrderByFechaPublicacionDesc(categoria);
    }

    public List<Producto> findByEstado(Producto.EstadoProducto estado) {
        return productoRepository.findByEstadoProductoAndDisponibleTrueOrderByFechaPublicacionDesc(estado);
    }

    public List<Producto> searchByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndDisponibleTrueOrderByFechaPublicacionDesc(nombre);
    }

    public List<Producto> searchByDescripcion(String descripcion) {
        return productoRepository.findByDescripcionContainingIgnoreCaseAndDisponibleTrueOrderByFechaPublicacionDesc(descripcion);
    }

    public List<Producto> findByRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.findByPrecioBetweenAndDisponibleTrueOrderByPrecioAsc(precioMin, precioMax);
    }

    public List<Producto> findProductosRecientes(int limite) {
        return productoRepository.findProductosRecientes(limite);
    }

    public List<Producto> findProductosBaratos(String categoria, int limite) {
        return productoRepository.findProductosBaratosPorCategoria(categoria, limite);
    }

    public List<Producto> buscarProductos(String texto) {
        return productoRepository.buscarProductos(texto);
    }

    public Long countByUsuario(Long usuarioId) {
        return productoRepository.countByUsuarioIdAndDisponibleTrue(usuarioId);
    }

    public Long countByCategoria(String categoria) {
        return productoRepository.countByCategoriaAndDisponibleTrue(categoria);
    }

    public List<String> getCategorias() {
        return productoRepository.findCategoriasDisponibles();
    }

    // Validaciones de negocio
    public boolean existsUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId).isPresent();
    }

    public boolean esVendedor(Long productoId, Long usuarioId) {
        return productoRepository.findById(productoId)
                .map(producto -> producto.getUsuarioId().equals(usuarioId))
                .orElse(false);
    }

    public boolean isValidCategoria(String categoria) {
        // Categorías permitidas en el marketplace estudiantil
        List<String> categoriasPermitidas = List.of(
            "Libros", "Electrónicos", "Instrumentos", "Componentes", 
            "Herramientas", "Computadoras", "Software", "Apuntes", 
            "Seguridad", "Laboratorio", "Materiales"
        );
        return categoriasPermitidas.contains(categoria);
    }

    public boolean isValidPrecio(BigDecimal precio) {
        return precio != null && 
               precio.compareTo(BigDecimal.ZERO) > 0 && 
               precio.compareTo(new BigDecimal("10000")) <= 0; // Máximo S/. 10,000
    }

    public Producto createProducto(Producto producto) {
        // Validar usuario vendedor
        if (!existsUsuario(producto.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario especificado no existe");
        }
        
        // Validar campos obligatorios
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        
        if (producto.getDescripcion() == null || producto.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del producto es obligatoria");
        }
        
        // Validar precio
        if (!isValidPrecio(producto.getPrecio())) {
            throw new IllegalArgumentException("El precio debe estar entre S/. 0.01 y S/. 10,000");
        }
        
        // Validar categoría
        if (!isValidCategoria(producto.getCategoria())) {
            throw new IllegalArgumentException("Categoría no válida");
        }
        
        // Validar estado del producto
        if (producto.getEstadoProducto() == null) {
            producto.setEstadoProducto(Producto.EstadoProducto.USADO); // Por defecto
        }
        
        // Establecer fecha de publicación
        if (producto.getFechaPublicacion() == null) {
            producto.setFechaPublicacion(LocalDateTime.now());
        }
        
        // Establecer como disponible
        producto.setDisponible(true);
        
        // Limpiar y formatear datos
        producto.setNombre(producto.getNombre().trim());
        producto.setDescripcion(producto.getDescripcion().trim());
        
        return save(producto);
    }

    public Producto updateProducto(Long id, Producto productoActualizado, Long usuarioId) {
        return productoRepository.findById(id).map(producto -> {
            // Validar que solo el vendedor puede editar
            if (!esVendedor(id, usuarioId)) {
                throw new IllegalArgumentException("Solo el vendedor puede editar este producto");
            }
            
            // Validar precio
            if (!isValidPrecio(productoActualizado.getPrecio())) {
                throw new IllegalArgumentException("El precio debe estar entre S/. 0.01 y S/. 10,000");
            }
            
            // Validar categoría
            if (!isValidCategoria(productoActualizado.getCategoria())) {
                throw new IllegalArgumentException("Categoría no válida");
            }
            
            // Actualizar campos permitidos
            producto.setNombre(productoActualizado.getNombre().trim());
            producto.setDescripcion(productoActualizado.getDescripcion().trim());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setCategoria(productoActualizado.getCategoria());
            producto.setEstadoProducto(productoActualizado.getEstadoProducto());
            producto.setImagenUrl(productoActualizado.getImagenUrl());
            
            return productoRepository.save(producto);
        }).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    public void marcarComoVendido(Long id, Long usuarioId) {
        productoRepository.findById(id).ifPresent(producto -> {
            // Validar que solo el vendedor puede marcar como vendido
            if (!esVendedor(id, usuarioId)) {
                throw new IllegalArgumentException("Solo el vendedor puede marcar este producto como vendido");
            }
            
            producto.setDisponible(false);
            productoRepository.save(producto);
        });
    }

    public void reactivarProducto(Long id, Long usuarioId) {
        productoRepository.findById(id).ifPresent(producto -> {
            // Validar que solo el vendedor puede reactivar
            if (!esVendedor(id, usuarioId)) {
                throw new IllegalArgumentException("Solo el vendedor puede reactivar este producto");
            }
            
            producto.setDisponible(true);
            productoRepository.save(producto);
        });
    }

    // Métodos de búsqueda avanzada
    public List<Producto> findProductosConFiltros(String categoria, Producto.EstadoProducto estado, 
                                                  BigDecimal precioMin, BigDecimal precioMax, String busqueda) {
        
        List<Producto> productos = findAll();
        
        // Filtrar por categoría
        if (categoria != null && !categoria.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getCategoria().equals(categoria))
                    .toList();
        }
        
        // Filtrar por estado
        if (estado != null) {
            productos = productos.stream()
                    .filter(p -> p.getEstadoProducto() == estado)
                    .toList();
        }
        
        // Filtrar por rango de precio
        if (precioMin != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio().compareTo(precioMin) >= 0)
                    .toList();
        }
        
        if (precioMax != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio().compareTo(precioMax) <= 0)
                    .toList();
        }
        
        // Filtrar por búsqueda de texto
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            String searchTerm = busqueda.toLowerCase().trim();
            productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(searchTerm) ||
                               p.getDescripcion().toLowerCase().contains(searchTerm))
                    .toList();
        }
        
        return productos;
    }

    public List<Producto> getRecomendaciones(Long usuarioId, int limite) {
        // Obtener categorías de productos que ha publicado el usuario
        List<Producto> productosUsuario = findByUsuario(usuarioId);
        
        if (productosUsuario.isEmpty()) {
            // Si no tiene productos, mostrar los más recientes
            return findProductosRecientes(limite);
        }
        
        // Obtener la categoría más común del usuario
        String categoriaFavorita = productosUsuario.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Producto::getCategoria, 
                    java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("Libros");
        
        // Recomendar productos de la misma categoría (excluyendo los del usuario)
        List<Producto> recomendaciones = findByCategoria(categoriaFavorita).stream()
                .filter(p -> !p.getUsuarioId().equals(usuarioId))
                .limit(limite)
                .toList();
        
        // Si no hay suficientes, completar con productos recientes
        if (recomendaciones.size() < limite) {
            List<Producto> recientes = findProductosRecientes(limite - recomendaciones.size()).stream()
                    .filter(p -> !p.getUsuarioId().equals(usuarioId))
                    .filter(p -> !recomendaciones.contains(p))
                    .toList();
            
            java.util.List<Producto> resultado = new java.util.ArrayList<>(recomendaciones);
            resultado.addAll(recientes);
            return resultado;
        }
        
        return recomendaciones;
    }

    // Métodos de estadísticas
    public java.util.Map<String, Object> getEstadisticasUsuario(Long usuarioId) {
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
        
        estadisticas.put("totalProductos", countByUsuario(usuarioId));
        estadisticas.put("productosActivos", findByUsuario(usuarioId).size());
        
        List<Producto> productos = findByUsuario(usuarioId);
        if (!productos.isEmpty()) {
            BigDecimal precioPromedio = productos.stream()
                    .map(Producto::getPrecio)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(new BigDecimal(productos.size()), 2, java.math.RoundingMode.HALF_UP);
            
            estadisticas.put("precioPromedio", precioPromedio);
            
            // Categoría más común
            String categoriaMasComun = productos.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                        Producto::getCategoria, 
                        java.util.stream.Collectors.counting()))
                    .entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey)
                    .orElse("N/A");
            
            estadisticas.put("categoriaMasComun", categoriaMasComun);
        } else {
            estadisticas.put("precioPromedio", BigDecimal.ZERO);
            estadisticas.put("categoriaMasComun", "N/A");
        }
        
        return estadisticas;
    }

    public java.util.Map<String, Long> getEstadisticasCategorias() {
        return getCategorias().stream()
                .collect(java.util.stream.Collectors.toMap(
                    categoria -> categoria,
                    categoria -> countByCategoria(categoria)
                ));
    }
}