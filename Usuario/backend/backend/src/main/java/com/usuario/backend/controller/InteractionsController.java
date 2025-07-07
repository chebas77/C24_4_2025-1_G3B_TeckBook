package com.usuario.backend.controller;

import com.usuario.backend.model.entity.*;
import com.usuario.backend.service.user.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/interactions")
@CrossOrigin(origins = "*")
public class InteractionsController {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private UsuarioService usuarioService;

    // ========== LIKES ==========
    
    @PostMapping("/like/{anuncioId}")
    @Transactional
    public ResponseEntity<?> toggleLike(@PathVariable Long anuncioId, 
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.findByCorreoInstitucional(userDetails.getUsername());
            
            // Verificar si ya existe el like
            Like existingLike = entityManager
                .createQuery("SELECT l FROM Like l WHERE l.anuncioId = :anuncioId AND l.usuarioId = :usuarioId", Like.class)
                .setParameter("anuncioId", anuncioId)
                .setParameter("usuarioId", usuario.getId().intValue())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

            if (existingLike != null) {
                // Quitar like
                entityManager.remove(existingLike);
                return ResponseEntity.ok(Map.of("liked", false, "message", "Like eliminado"));
            } else {
                // Agregar like
                Like newLike = new Like(usuario.getId().intValue(), anuncioId);
                entityManager.persist(newLike);
                return ResponseEntity.ok(Map.of("liked", true, "message", "Like agregado"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/likes/{anuncioId}")
    public ResponseEntity<?> getLikes(@PathVariable Long anuncioId) {
        try {
            Long count = entityManager
                .createQuery("SELECT COUNT(l) FROM Like l WHERE l.anuncioId = :anuncioId", Long.class)
                .setParameter("anuncioId", anuncioId)
                .getSingleResult();
            
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user-likes")
    public ResponseEntity<?> getUserLikes(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.findByCorreoInstitucional(userDetails.getUsername());
            
            List<Long> likedAnuncios = entityManager
                .createQuery("SELECT l.anuncioId FROM Like l WHERE l.usuarioId = :usuarioId", Long.class)
                .setParameter("usuarioId", usuario.getId().intValue())
                .getResultList();
            
            return ResponseEntity.ok(Map.of("likedAnuncios", likedAnuncios));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== COMENTARIOS ==========
    
    @PostMapping("/comment/{anuncioId}")
    @Transactional
    public ResponseEntity<?> addComment(@PathVariable Long anuncioId,
                                       @RequestBody Map<String, String> request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.findByCorreoInstitucional(userDetails.getUsername());
            String contenido = request.get("contenido");
            
            if (contenido == null || contenido.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Contenido requerido"));
            }

            Comentario comentario = new Comentario(usuario.getId().intValue(), anuncioId, contenido.trim());
            entityManager.persist(comentario);
            
            return ResponseEntity.ok(Map.of("message", "Comentario agregado", "comentarioId", comentario.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/comments/{anuncioId}")
    public ResponseEntity<?> getComments(@PathVariable Long anuncioId) {
        try {
            List<Object[]> results = entityManager
                .createQuery("""
                    SELECT c.id, c.contenido, c.fechaCreacion, c.usuarioId, u.nombre, u.apellidos 
                    FROM Comentario c JOIN Usuario u ON c.usuarioId = u.id 
                    WHERE c.anuncioId = :anuncioId AND c.activo = true 
                    ORDER BY c.fechaCreacion ASC
                    """, Object[].class)
                .setParameter("anuncioId", anuncioId)
                .getResultList();

            List<Map<String, Object>> comentarios = new ArrayList<>();
            for (Object[] result : results) {
                Map<String, Object> comentario = new HashMap<>();
                comentario.put("id", result[0]);
                comentario.put("contenido", result[1]);
                comentario.put("fechaCreacion", result[2]);
                comentario.put("usuarioId", result[3]);
                comentario.put("autor", result[4] + " " + result[5]);
                comentarios.add(comentario);
            }
            
            return ResponseEntity.ok(Map.of("comentarios", comentarios));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== LECTURAS ==========
    
    @PostMapping("/read/{anuncioId}")
    @Transactional
    public ResponseEntity<?> markAsRead(@PathVariable Long anuncioId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.findByCorreoInstitucional(userDetails.getUsername());
            
            // Verificar si ya está marcado como leído
            List<Lectura> existing = entityManager
                .createQuery("SELECT l FROM Lectura l WHERE l.anuncioId = :anuncioId AND l.usuarioId = :usuarioId", Lectura.class)
                .setParameter("anuncioId", anuncioId)
                .setParameter("usuarioId", usuario.getId().intValue())
                .getResultList();

            if (existing.isEmpty()) {
                Lectura lectura = new Lectura(usuario.getId().intValue(), anuncioId);
                entityManager.persist(lectura);
            }
            
            return ResponseEntity.ok(Map.of("message", "Marcado como leído"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== STATS COMBINADAS ==========
    
    @GetMapping("/stats/{anuncioId}")
    public ResponseEntity<?> getAnuncioStats(@PathVariable Long anuncioId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.findByCorreoInstitucional(userDetails.getUsername());
            
            // Contar likes
            Long likesCount = entityManager
                .createQuery("SELECT COUNT(l) FROM Like l WHERE l.anuncioId = :anuncioId", Long.class)
                .setParameter("anuncioId", anuncioId)
                .getSingleResult();
            
            // Contar comentarios
            Long commentsCount = entityManager
                .createQuery("SELECT COUNT(c) FROM Comentario c WHERE c.anuncioId = :anuncioId AND c.activo = true", Long.class)
                .setParameter("anuncioId", anuncioId)
                .getSingleResult();
            
            // Verificar si usuario ya dio like
            boolean userLiked = !entityManager
                .createQuery("SELECT l FROM Like l WHERE l.anuncioId = :anuncioId AND l.usuarioId = :usuarioId", Like.class)
                .setParameter("anuncioId", anuncioId)
                .setParameter("usuarioId", usuario.getId().intValue())
                .getResultList().isEmpty();
            
            // Verificar si usuario ya leyó
            boolean userRead = !entityManager
                .createQuery("SELECT l FROM Lectura l WHERE l.anuncioId = :anuncioId AND l.usuarioId = :usuarioId", Lectura.class)
                .setParameter("anuncioId", anuncioId)
                .setParameter("usuarioId", usuario.getId().intValue())
                .getResultList().isEmpty();

            Map<String, Object> stats = new HashMap<>();
            stats.put("likesCount", likesCount);
            stats.put("commentsCount", commentsCount);
            stats.put("userLiked", userLiked);
            stats.put("userRead", userRead);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}