package com.usuario.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);
    
    @Autowired
    private Cloudinary cloudinary;
    
    public String uploadImage(MultipartFile file, String userId) {
    try {
        // Generar un ID único para la imagen
        String publicId = "tecbook_profiles/" + userId + "_" + UUID.randomUUID().toString().substring(0, 8);
        
        logger.info("Subiendo imagen para el usuario: {}", userId);
        
        // Definir transformación como string
        String transformation = "c_fill,w_300,h_300,g_face";
        
        // Configurar opciones básicas con transformación como string
        Map<String, Object> options = ObjectUtils.asMap(
            "public_id", publicId,
            "overwrite", true,
            "transformation", transformation
        );
        
        // Subir imagen a Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        
        // Obtener la URL segura de la imagen
        String secureUrl = (String) uploadResult.get("secure_url");
        
        logger.info("Imagen subida exitosamente: {}", secureUrl);
        
        return secureUrl;
        
    } catch (IOException e) {
        logger.error("Error al subir imagen: {}", e.getMessage(), e);
        throw new RuntimeException("Error al subir imagen a Cloudinary", e);
    }
}
    
    public void deleteImage(String publicId) {
        try {
            // Eliminar imagen de Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Imagen eliminada: {}", publicId);
        } catch (IOException e) {
            logger.error("Error al eliminar imagen: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar imagen de Cloudinary", e);
        }
    }
}