package com.usuario.backend.controller.upload;

import com.usuario.backend.model.entity.core.Usuario;
import com.usuario.backend.service.upload.CloudinaryService;
import com.usuario.backend.service.upload.ProfileImageService;
import com.usuario.backend.service.core.UsuarioService;

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
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    // 🔧 ACTUALIZADO: Usar UsuarioService de la arquitectura Core
    @Autowired
    private UsuarioService usuarioService;

    /**
     * 📤 Subir imagen de perfil - ACTUALIZADO PARA CORE
     * Garantiza persistencia inmediata en base de datos con nueva estructura
     */
    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("📤 Solicitud de subida de imagen de perfil recibida");
        
        if (userDetails == null) {
            logger.warn("❌ Intento de subida de imagen sin autenticación");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        
        String username = userDetails.getUsername();
        
        // ✅ VALIDACIONES DE ARCHIVO
        if (file.isEmpty()) {
            logger.warn("❌ Archivo vacío");
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El archivo está vacío"));
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            logger.warn("❌ Tipo de archivo no válido: {}", contentType);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Solo se permiten archivos de imagen"));
        }
        
        if (file.getSize() > 5 * 1024 * 1024) {
            logger.warn("❌ Archivo demasiado grande: {} bytes", file.getSize());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El archivo no debe superar los 5MB"));
        }
        
        try {
            // 🔧 ACTUALIZADO: Usar findByEmail() de la nueva arquitectura
            Usuario usuario = usuarioService.findByEmail(username);
            if (usuario == null) {
                logger.warn("❌ Usuario no encontrado: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
            
            // ✅ VERIFICAR QUE EL USUARIO ESTÉ ACTIVO
            if (!usuario.getActivo()) {
                logger.warn("❌ Usuario desactivado: {}", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Cuenta desactivada"));
            }
            
            logger.info("✅ Usuario encontrado: {} (ID: {})", username, usuario.getId());
            
            // 📤 SUBIR IMAGEN A CLOUDINARY
            String imageUrl = cloudinaryService.uploadImage(file, String.valueOf(usuario.getId()));
            logger.info("✅ Imagen subida a Cloudinary: {}", imageUrl);
            
            // 💾 GUARDAR EN BD Y VERIFICAR
            String previousUrl = usuario.getProfileImageUrl();
            logger.info("🔄 URL anterior: {}", previousUrl);
            
            usuario.setProfileImageUrl(imageUrl);
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
            
            // 🔍 VERIFICACIÓN CRÍTICA
            logger.info("✅ Usuario actualizado, nueva URL: {}", usuarioActualizado.getProfileImageUrl());
            
            // 🔍 VERIFICACIÓN DESDE BD
            Usuario usuarioVerificacion = usuarioService.findByEmail(username);
            logger.info("🔍 Verificación desde BD: {}", usuarioVerificacion.getProfileImageUrl());
            
            if (usuarioVerificacion.getProfileImageUrl() == null || 
                !usuarioVerificacion.getProfileImageUrl().equals(imageUrl)) {
                logger.error("❌ ERROR: La imagen NO se guardó en la base de datos!");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al guardar la imagen en la base de datos"));
            }
            
            logger.info("✅ Imagen guardada exitosamente en BD para: {}", username);
            
            // 📋 RESPUESTA EXITOSA
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Imagen de perfil actualizada correctamente");
            response.put("previousUrl", previousUrl);
            response.put("userId", usuario.getId());
            response.put("userEmail", usuario.getEmail());
            response.put("verified", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al procesar imagen: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al procesar la imagen: " + e.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    ));
        }
    }
    
    /**
     * 📋 Obtener imagen de perfil actual - ACTUALIZADO PARA CORE
     * Siempre consulta la base de datos con nueva estructura
     */
    @GetMapping("/profile-image/current")
    public ResponseEntity<?> getCurrentProfileImage(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        
        String username = userDetails.getUsername();
        logger.debug("📋 Solicitando imagen actual para: {}", username);
        
        try {
            // 🔧 ACTUALIZADO: Usar findByEmail() directamente
            Usuario usuario = usuarioService.findByEmail(username);
            if (usuario == null) {
                logger.warn("❌ Usuario no encontrado: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
            
            String imageUrl = usuario.getProfileImageUrl();
            logger.debug("📋 URL de imagen obtenida desde BD: {}", imageUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", imageUrl != null ? imageUrl : "");
            response.put("hasImage", imageUrl != null && !imageUrl.isEmpty());
            response.put("userId", usuario.getId());
            response.put("userEmail", usuario.getEmail());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al obtener imagen actual para {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al obtener la imagen",
                        "message", e.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    ));
        }
    }
    
    /**
     * 🗑️ Eliminar imagen de perfil - ACTUALIZADO PARA CORE
     * Elimina tanto de Cloudinary como de la base de datos
     */
    @DeleteMapping("/profile-image")
    public ResponseEntity<?> removeProfileImage(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado"));
        }
        
        String username = userDetails.getUsername();
        logger.info("🗑️ Solicitud de eliminación de imagen para: {}", username);
        
        try {
            // 🔧 ACTUALIZADO: Usar findByEmail() de la nueva arquitectura
            Usuario usuario = usuarioService.findByEmail(username);
            if (usuario == null) {
                logger.warn("❌ Usuario no encontrado: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
            
            String currentImageUrl = usuario.getProfileImageUrl();
            
            // Si no hay imagen, no hacer nada
            if (currentImageUrl == null || currentImageUrl.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No hay imagen de perfil para eliminar");
                response.put("imageUrl", "");
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.ok(response);
            }
            
            // 🗑️ ELIMINAR DE CLOUDINARY
            try {
                // Extraer public_id de la URL de Cloudinary para eliminar
                String[] parts = currentImageUrl.split("/");
                if (parts.length > 0) {
                    String fileName = parts[parts.length - 1];
                    String publicId = "tecbook_profiles/" + fileName.split("\\.")[0];
                    cloudinaryService.deleteImage(publicId);
                    logger.info("🗑️ Imagen eliminada de Cloudinary: {}", publicId);
                }
            } catch (Exception e) {
                logger.warn("⚠️ Error al eliminar imagen de Cloudinary (continuando): {}", e.getMessage());
                // Continuar aunque falle Cloudinary
            }
            
            // 💾 ELIMINAR URL DE LA BASE DE DATOS
            usuario.setProfileImageUrl(null);
            usuarioService.actualizarUsuario(usuario);
            
            logger.info("✅ Imagen de perfil eliminada exitosamente para: {}", username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Imagen de perfil eliminada correctamente");
            response.put("imageUrl", "");
            response.put("previousImageUrl", currentImageUrl);
            response.put("userId", usuario.getId());
            response.put("userEmail", usuario.getEmail());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Error al eliminar imagen para {}: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error al eliminar la imagen",
                        "message", e.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    ));
        }
    }
    
    /**
     * 🔍 Health check del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "service", "ImageUploadController",
            "status", "OK",
            "version", "2.0 - Core Architecture",
            "timestamp", System.currentTimeMillis(),
            "endpoints", Map.of(
                "upload", "POST /api/upload/profile-image",
                "current", "GET /api/upload/profile-image/current", 
                "delete", "DELETE /api/upload/profile-image"
            ),
            "features", Map.of(
                "cloudinaryIntegration", true,
                "coreArchitecture", true,
                "userValidation", true,
                "errorHandling", true
            )
        ));
    }
}