package com.usuario.backend.controller.upload;
import com.usuario.backend.model.entity.Usuario;
import com.usuario.backend.service.upload.CloudinaryService;
import com.usuario.backend.service.upload.ProfileImageService;
import com.usuario.backend.service.user.UsuarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class ImageUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private ProfileImageService profileImageService;
    
     // 🔧 FIX: Agregar las dependencias que faltaban
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private UsuarioService usuarioService;
    /**
     * REFACTORIZADO: Subir imagen de perfil usando ProfileImageService
     * SOLUCIÓN AL BUG: Garantiza persistencia inmediata en base de datos
     */// En ImageUploadController.java - MODIFICAR el método uploadProfileImage

@PostMapping("/profile-image")
public ResponseEntity<?> uploadProfileImage(
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal UserDetails userDetails) {
    
    logger.info("Solicitud de subida de imagen de perfil recibida");
    
    if (userDetails == null) {
        logger.warn("Intento de subida de imagen sin autenticación");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No autenticado"));
    }
    
    String username = userDetails.getUsername();
    
    // Validar tipo de archivo
    if (file.isEmpty()) {
        logger.warn("Archivo vacío");
        return ResponseEntity.badRequest()
                .body(Map.of("error", "El archivo está vacío"));
    }
    
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
        logger.warn("Tipo de archivo no válido: {}", contentType);
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Solo se permiten archivos de imagen"));
    }
    
    if (file.getSize() > 5 * 1024 * 1024) {
        logger.warn("Archivo demasiado grande: {} bytes", file.getSize());
        return ResponseEntity.badRequest()
                .body(Map.of("error", "El archivo no debe superar los 5MB"));
    }
    
    try {
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioService.findByCorreoInstitucional(username);
        if (usuario == null) {
            logger.warn("Usuario no encontrado: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        
        logger.info("Usuario encontrado: {} (ID: {})", username, usuario.getId());
        
        // Subir la imagen a Cloudinary
        String imageUrl = cloudinaryService.uploadImage(file, String.valueOf(usuario.getId()));
        logger.info("✅ Imagen subida a Cloudinary: {}", imageUrl);
        
        // 🔧 FIX: Guardar en BD y verificar
        String previousUrl = usuario.getProfileImageUrl();
        logger.info("URL anterior: {}", previousUrl);
        
        usuario.setProfileImageUrl(imageUrl);
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
        
        // 🔍 VERIFICACIÓN CRÍTICA
        logger.info("✅ Usuario actualizado, nueva URL: {}", usuarioActualizado.getProfileImageUrl());
        
        // Verificar que se guardó correctamente
        Usuario usuarioVerificacion = usuarioService.findByCorreoInstitucional(username);
        logger.info("🔍 Verificación desde BD: {}", usuarioVerificacion.getProfileImageUrl());
        
        if (usuarioVerificacion.getProfileImageUrl() == null || 
            !usuarioVerificacion.getProfileImageUrl().equals(imageUrl)) {
            logger.error("❌ ERROR: La imagen NO se guardó en la base de datos!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al guardar la imagen en la base de datos"));
        }
        
        logger.info("✅ Imagen guardada exitosamente en BD para: {}", username);
        
        // Devolver la URL de la imagen
        Map<String, Object> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        response.put("message", "Imagen de perfil actualizada correctamente");
        response.put("previousUrl", previousUrl);
        response.put("userId", usuario.getId());
        response.put("verified", true); // Confirmación de que se guardó
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        logger.error("❌ Error al procesar imagen: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al procesar la imagen: " + e.getMessage()));
    }
}
    
    /**
     * NUEVO: Obtener imagen de perfil actual
     * SOLUCIÓN AL BUG: Siempre consulta la base de datos
     */
    @GetMapping("/profile-image/current")
    public ResponseEntity<?> getCurrentProfileImage(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        
        String username = userDetails.getUsername();
        logger.debug("Solicitando imagen actual para: {}", username);
        
        try {
            Map<String, Object> response = profileImageService.getCurrentProfileImage(username);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener imagen actual para {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener la imagen",
                        "message", e.getMessage()
                    ));
        }
    }
    
    /**
     * NUEVO: Eliminar imagen de perfil
     * Elimina tanto de Cloudinary como de la base de datos
     */
    @DeleteMapping("/profile-image")
    public ResponseEntity<?> removeProfileImage(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        
        String username = userDetails.getUsername();
        logger.info("Solicitud de eliminación de imagen para: {}", username);
        
        try {
            Map<String, Object> response = profileImageService.removeProfileImage(username);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar imagen para {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al eliminar la imagen",
                        "message", e.getMessage()
                    ));
        }
    }
    
    /**
     * NUEVO: Endpoint de salud para verificar el servicio
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "ImageUploadController",
            "status", "OK",
            "timestamp", System.currentTimeMillis(),
            "endpoints", Map.of(
                "upload", "POST /api/upload/profile-image",
                "current", "GET /api/upload/profile-image/current", 
                "delete", "DELETE /api/upload/profile-image"
            )
        ));
    }
}