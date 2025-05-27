package com.usuario.backend.service;

import com.usuario.backend.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ProfileImageService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProfileImageService.class);
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    /**
     * Sube una imagen a Cloudinary y la guarda inmediatamente en la base de datos
     * SOLUCIÓN AL BUG: Garantiza persistencia inmediata
     */
    public Map<String, Object> uploadAndSaveProfileImage(MultipartFile file, String userEmail) {
        logger.info("Iniciando subida de imagen de perfil para usuario: {}", userEmail);
        
        try {
            // 1. Buscar usuario
            Usuario usuario = usuarioService.findByCorreoInstitucional(userEmail);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado: " + userEmail);
            }
            
            // 2. Subir imagen a Cloudinary
            String imageUrl = cloudinaryService.uploadImage(file, String.valueOf(usuario.getId()));
            logger.info("Imagen subida a Cloudinary exitosamente: {}", imageUrl);
            
            // 3. CRÍTICO: Guardar URL en base de datos INMEDIATAMENTE
            String previousImageUrl = usuario.getProfileImageUrl();
            usuario.setProfileImageUrl(imageUrl);
            
            // Usar actualizarUsuario para garantizar persistencia
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            
            // 4. Verificar que se guardó correctamente
            if (usuarioActualizado.getProfileImageUrl() == null || 
                !usuarioActualizado.getProfileImageUrl().equals(imageUrl)) {
                throw new RuntimeException("Error: La imagen no se guardó correctamente en la base de datos");
            }
            
            logger.info("URL de imagen guardada exitosamente en BD para usuario: {}", userEmail);
            
            // 5. Preparar respuesta completa
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Imagen de perfil actualizada correctamente");
            response.put("userId", usuario.getId());
            response.put("timestamp", System.currentTimeMillis()); // Para cache busting
            response.put("previousImageUrl", previousImageUrl); // Para posible rollback
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error al procesar imagen de perfil para {}: {}", userEmail, e.getMessage(), e);
            throw new RuntimeException("Error al procesar la imagen: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la URL actual de la imagen de perfil desde la base de datos
     * SOLUCIÓN AL BUG: Siempre consulta la BD, no depende de cache
     */
    public Map<String, Object> getCurrentProfileImage(String userEmail) {
        logger.debug("Obteniendo imagen de perfil actual para: {}", userEmail);
        
        try {
            Usuario usuario = usuarioService.findByCorreoInstitucional(userEmail);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado: " + userEmail);
            }
            
            String imageUrl = usuario.getProfileImageUrl();
            logger.debug("URL de imagen obtenida desde BD: {}", imageUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", imageUrl != null ? imageUrl : "");
            response.put("hasImage", imageUrl != null && !imageUrl.isEmpty());
            response.put("userId", usuario.getId());
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error al obtener imagen de perfil para {}: {}", userEmail, e.getMessage());
            throw new RuntimeException("Error al obtener la imagen: " + e.getMessage());
        }
    }
    
    /**
     * Elimina la imagen de perfil tanto de Cloudinary como de la base de datos
     */
    public Map<String, Object> removeProfileImage(String userEmail) {
        logger.info("Eliminando imagen de perfil para usuario: {}", userEmail);
        
        try {
            Usuario usuario = usuarioService.findByCorreoInstitucional(userEmail);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado: " + userEmail);
            }
            
            String currentImageUrl = usuario.getProfileImageUrl();
            
            // Si no hay imagen, no hacer nada
            if (currentImageUrl == null || currentImageUrl.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No hay imagen de perfil para eliminar");
                response.put("imageUrl", "");
                return response;
            }
            
            // Extraer public_id de la URL de Cloudinary para eliminar
            try {
                // Formato típico: https://res.cloudinary.com/cloud/image/upload/v123456/folder/public_id.jpg
                String[] parts = currentImageUrl.split("/");
                if (parts.length > 0) {
                    String fileName = parts[parts.length - 1];
                    String publicId = "tecbook_profiles/" + fileName.split("\\.")[0];
                    cloudinaryService.deleteImage(publicId);
                    logger.info("Imagen eliminada de Cloudinary: {}", publicId);
                }
            } catch (Exception e) {
                logger.warn("Error al eliminar imagen de Cloudinary (continuando): {}", e.getMessage());
                // Continuar aunque falle Cloudinary
            }
            
            // Eliminar URL de la base de datos
            usuario.setProfileImageUrl(null);
            usuarioService.actualizarUsuario(usuario);
            
            logger.info("Imagen de perfil eliminada exitosamente para: {}", userEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Imagen de perfil eliminada correctamente");
            response.put("imageUrl", "");
            response.put("previousImageUrl", currentImageUrl);
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error al eliminar imagen de perfil para {}: {}", userEmail, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
    }
    
    /**
     * Valida si un archivo es una imagen válida
     */
    public void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }
        
        // Validar tamaño (5MB máximo)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo no debe superar los 5MB");
        }
        
        // Validar tipos específicos
        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        boolean isValidType = false;
        for (String type : allowedTypes) {
            if (type.equals(contentType)) {
                isValidType = true;
                break;
            }
        }
        
        if (!isValidType) {
            throw new IllegalArgumentException("Formato no válido. Use JPG, PNG, GIF o WebP");
        }
        
        logger.debug("Archivo de imagen validado correctamente: {} ({})", file.getOriginalFilename(), contentType);
    }
}