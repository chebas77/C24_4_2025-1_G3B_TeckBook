package com.usuario.backend.controller.aula;

import com.usuario.backend.model.entity.InvitacionAula;
import com.usuario.backend.model.entity.Usuario;
import com.usuario.backend.service.aula.InvitacionAulaService;
import com.usuario.backend.service.user.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/invitaciones")
@CrossOrigin(origins = "*")
public class InvitacionAulaController {

    private static final Logger logger = LoggerFactory.getLogger(InvitacionAulaController.class);

    @Autowired
    private InvitacionAulaService invitacionService;
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarInvitacion(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String email = userDetails.getUsername();
            Usuario usuario = usuarioService.findByCorreoInstitucional(email);
            
            if (usuario == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
            }

            Long aulaId = Long.valueOf(request.get("aulaId").toString());
            String correoInvitado = request.get("correoInvitado").toString();
            String mensaje = request.getOrDefault("mensaje", "").toString();

            InvitacionAula invitacion = invitacionService.enviarInvitacion(
                aulaId, correoInvitado, mensaje, usuario.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invitación enviada exitosamente");
            response.put("invitacion", Map.of(
                "id", invitacion.getId(),
                "correoInvitado", invitacion.getCorreoInvitado(),
                "codigoInvitacion", invitacion.getCodigoInvitacion(),
                "fechaInvitacion", invitacion.getFechaInvitacion(),
                "fechaExpiracion", invitacion.getFechaExpiracion()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al enviar invitación: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/aceptar/{codigoInvitacion}")
    public ResponseEntity<?> aceptarInvitacion(
            @PathVariable String codigoInvitacion,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String email = userDetails.getUsername();
            
            InvitacionAula invitacion = invitacionService.aceptarInvitacion(codigoInvitacion, email);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invitación aceptada exitosamente");
            response.put("aulaId", invitacion.getAulaVirtualId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al aceptar invitación: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mis-invitaciones")
    public ResponseEntity<?> getMisInvitaciones(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String email = userDetails.getUsername();
            List<InvitacionAula> invitaciones = invitacionService.getInvitacionesPorCorreo(email);

            return ResponseEntity.ok(Map.of(
                "invitaciones", invitaciones,
                "total", invitaciones.size()
            ));

        } catch (Exception e) {
            logger.error("Error al obtener invitaciones: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> getInvitacionesPendientes(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
            }

            String email = userDetails.getUsername();
            List<InvitacionAula> invitaciones = invitacionService.getInvitacionesPendientes(email);

            return ResponseEntity.ok(Map.of(
                "invitaciones", invitaciones,
                "total", invitaciones.size()
            ));

        } catch (Exception e) {
            logger.error("Error al obtener invitaciones pendientes: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    @GetMapping("/aula/{aulaId}")
    public ResponseEntity<?> getInvitacionesDeAula(@PathVariable Long aulaId) {
        try {
            List<InvitacionAula> invitaciones = invitacionService.getInvitacionesDeAula(aulaId);

            return ResponseEntity.ok(Map.of(
                "invitaciones", invitaciones,
                "total", invitaciones.size()
            ));

        } catch (Exception e) {
            logger.error("Error al obtener invitaciones del aula: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }
}