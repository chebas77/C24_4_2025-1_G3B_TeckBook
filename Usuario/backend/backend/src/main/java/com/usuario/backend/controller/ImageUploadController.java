package com.usuario.backend.controller;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.service.CloudinaryService;
import com.usuario.backend.service.UsuarioService;
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
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        logger.info("Solicitud de subida de imagen de perfil recibida");
        
        // Verificar autenticación
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
        
        // Validar tamaño del archivo (5MB máx)
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
            
            // Subir la imagen a Cloudinary
            String imageUrl = cloudinaryService.uploadImage(file, String.valueOf(usuario.getId()));
            
            // Actualizar la URL de la imagen en el usuario
            usuario.setProfileImageUrl(imageUrl);
            usuarioService.actualizarUsuario(usuario);
            
            logger.info("Imagen de perfil actualizada para: {}", username);
            
            // Devolver la URL de la imagen
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Imagen de perfil actualizada correctamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al procesar imagen: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar la imagen: " + e.getMessage()));
        }
    }
}