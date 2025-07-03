// Usuario/backend/backend/src/main/java/com/usuario/backend/service/aula/InvitacionAulaService.java
package com.usuario.backend.service.aula;

import com.usuario.backend.model.entity.InvitacionAula;
import com.usuario.backend.model.entity.AulaVirtual;
import com.usuario.backend.model.entity.Usuario;
import com.usuario.backend.repository.InvitacionAulaRepository;
import com.usuario.backend.service.user.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvitacionAulaService {

    private static final Logger logger = LoggerFactory.getLogger(InvitacionAulaService.class);

    @Autowired
    private InvitacionAulaRepository invitacionRepository;
    
    @Autowired
    private AulaVirtualService aulaService;
    
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Envía una invitación a un usuario para unirse a un aula
     */
    public InvitacionAula enviarInvitacion(Long aulaId, String correoInvitado, String mensaje, Long invitadoPorId) {
        logger.info("📧 Enviando invitación para aula {} a {}", aulaId, correoInvitado);
        
        // Verificar que el aula existe
        AulaVirtual aula = aulaService.findById(aulaId);
        if (aula == null) {
            throw new RuntimeException("Aula no encontrada con ID: " + aulaId);
        }

        // Verificar que el invitado tiene un correo válido de Tecsup
        if (!correoInvitado.endsWith("@tecsup.edu.pe")) {
            throw new RuntimeException("Solo se pueden invitar correos institucionales (@tecsup.edu.pe)");
        }

        // Verificar si ya existe una invitación pendiente
        List<InvitacionAula> invitacionesExistentes = invitacionRepository
            .findInvitacionesPendientesByCorreo(correoInvitado);
        
        for (InvitacionAula inv : invitacionesExistentes) {
            if (inv.getAulaVirtualId().equals(aulaId)) {
                throw new RuntimeException("Ya existe una invitación pendiente para este usuario en esta aula");
            }
        }

        // Crear nueva invitación
        InvitacionAula invitacion = new InvitacionAula(aulaId, invitadoPorId, correoInvitado, mensaje);
        invitacion.setCodigoInvitacion(generateCodigoInvitacion());
        
        InvitacionAula invitacionGuardada = invitacionRepository.save(invitacion);
        
        logger.info("✅ Invitación enviada exitosamente: ID {}", invitacionGuardada.getId());
        
        return invitacionGuardada;
    }

    /**
     * Acepta una invitación usando el código único y agrega al usuario al aula
     */
    public InvitacionAula aceptarInvitacion(String codigoInvitacion, String correoUsuario) {
        logger.info("✅ Procesando aceptación de invitación: {}", codigoInvitacion);
        
        InvitacionAula invitacion = invitacionRepository.findByCodigoInvitacion(codigoInvitacion);
        
        if (invitacion == null) {
            throw new RuntimeException("Código de invitación no válido");
        }

        if (!invitacion.getCorreoInvitado().equals(correoUsuario)) {
            throw new RuntimeException("Esta invitación no corresponde a tu correo electrónico");
        }

        if (!invitacion.isPendiente()) {
            String estado = invitacion.getEstado();
            if ("aceptada".equals(estado)) {
                // Si ya fue aceptada, simplemente retorna la invitación
                return invitacion;
            } else if ("expirada".equals(estado) || invitacion.isExpirada()) {
                throw new RuntimeException("Esta invitación ha expirado");
            } else if ("rechazada".equals(estado)) {
                throw new RuntimeException("Esta invitación fue rechazada anteriormente");
            } else {
                throw new RuntimeException("Esta invitación ya no está disponible");
            }
        }

        // Buscar usuario por correo
        Usuario usuario = usuarioService.findByCorreoInstitucional(correoUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado para el correo: " + correoUsuario);
        }
        if (!"ESTUDIANTE".equals(usuario.getRol().toString())) {
            throw new RuntimeException("Solo los usuarios con rol ESTUDIANTE pueden aceptar invitaciones a aulas");
        }

        // Intentar agregar usuario al aula, pero si ya está inscrito, no lanzar excepción
        boolean yaInscrito = false;
        try {
            aulaService.agregarEstudianteAAula(invitacion.getAulaVirtualId(), usuario.getId());
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("ya está inscrito")) {
                yaInscrito = true;
                logger.info("El estudiante ya estaba inscrito en el aula, se marca invitación como aceptada");
            } else {
                logger.error("Error al agregar estudiante al aula al aceptar invitación: {}", e.getMessage());
                throw new RuntimeException("No se pudo agregar al aula: " + e.getMessage());
            }
        }

        // Aceptar invitación
        invitacion.setEstado("aceptada");
        invitacion.setFechaRespuesta(LocalDateTime.now());
        
        InvitacionAula invitacionAceptada = invitacionRepository.save(invitacion);
        
        logger.info("✅ Invitación aceptada exitosamente para: {} y usuario unido al aula (idempotente={})", correoUsuario, yaInscrito);
        
        return invitacionAceptada;
    }

    /**
     * Rechaza una invitación
     */
    public InvitacionAula rechazarInvitacion(String codigoInvitacion, String correoUsuario) {
        logger.info("❌ Procesando rechazo de invitación: {}", codigoInvitacion);
        
        InvitacionAula invitacion = invitacionRepository.findByCodigoInvitacion(codigoInvitacion);
        
        if (invitacion == null) {
            throw new RuntimeException("Código de invitación no válido");
        }

        if (!invitacion.getCorreoInvitado().equals(correoUsuario)) {
            throw new RuntimeException("Esta invitación no corresponde a tu correo electrónico");
        }

        if (!invitacion.isPendiente()) {
            throw new RuntimeException("Esta invitación ya no está disponible para ser rechazada");
        }

        // Rechazar invitación
        invitacion.setEstado("rechazada");
        invitacion.setFechaRespuesta(LocalDateTime.now());
        
        InvitacionAula invitacionRechazada = invitacionRepository.save(invitacion);
        
        logger.info("❌ Invitación rechazada por: {}", correoUsuario);
        
        return invitacionRechazada;
    }

    /**
     * Obtiene todas las invitaciones de un usuario por correo
     */
    public List<InvitacionAula> getInvitacionesPorCorreo(String correoInvitado) {
        logger.debug("📋 Obteniendo invitaciones para: {}", correoInvitado);
        return invitacionRepository.findByCorreoInvitado(correoInvitado);
    }

    /**
     * Obtiene las invitaciones pendientes de un usuario
     */
    public List<InvitacionAula> getInvitacionesPendientes(String correoInvitado) {
        logger.debug("⏳ Obteniendo invitaciones pendientes para: {}", correoInvitado);
        return invitacionRepository.findInvitacionesPendientesByCorreo(correoInvitado);
    }

    /**
     * Obtiene todas las invitaciones de un aula específica
     */
    public List<InvitacionAula> getInvitacionesDeAula(Long aulaId) {
        logger.debug("📋 Obteniendo invitaciones del aula: {}", aulaId);
        return invitacionRepository.findByAulaVirtualId(aulaId);
    }

    /**
     * Obtiene estadísticas de invitaciones para un aula
     */
    public InvitacionStats getEstadisticasAula(Long aulaId) {
        List<InvitacionAula> todasLasInvitaciones = invitacionRepository.findByAulaVirtualId(aulaId);
        
        InvitacionStats stats = new InvitacionStats();
        stats.setTotal(todasLasInvitaciones.size());
        
        for (InvitacionAula inv : todasLasInvitaciones) {
            switch (inv.getEstado()) {
                case "pendiente": 
                    if (inv.isPendiente()) stats.incrementarPendientes();
                    else stats.incrementarExpiradas();
                    break;
                case "aceptada": stats.incrementarAceptadas(); break;
                case "rechazada": stats.incrementarRechazadas(); break;
                case "expirada": stats.incrementarExpiradas(); break;
            }
        }
        
        return stats;
    }

    /**
     * Marca invitaciones expiradas automáticamente
     */
    @Transactional
    public int marcarInvitacionesExpiradas() {
        List<InvitacionAula> invitacionesPendientes = invitacionRepository.findByCorreoInvitadoAndEstado("", "pendiente");
        int marcadas = 0;
        
        for (InvitacionAula inv : invitacionesPendientes) {
            if (inv.isExpirada()) {
                inv.setEstado("expirada");
                invitacionRepository.save(inv);
                marcadas++;
            }
        }
        
        if (marcadas > 0) {
            logger.info("🕒 Se marcaron {} invitaciones como expiradas", marcadas);
        }
        
        return marcadas;
    }

    /**
     * Genera un código único para la invitación
     */
    private String generateCodigoInvitacion() {
        return UUID.randomUUID().toString();
    }

    /**
     * Clase interna para estadísticas de invitaciones
     */
    public static class InvitacionStats {
        private int total = 0;
        private int pendientes = 0;
        private int aceptadas = 0;
        private int rechazadas = 0;
        private int expiradas = 0;

        // Getters y setters
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public int getPendientes() { return pendientes; }
        public void incrementarPendientes() { this.pendientes++; }
        
        public int getAceptadas() { return aceptadas; }
        public void incrementarAceptadas() { this.aceptadas++; }
        
        public int getRechazadas() { return rechazadas; }
        public void incrementarRechazadas() { this.rechazadas++; }
        
        public int getExpiradas() { return expiradas; }
        public void incrementarExpiradas() { this.expiradas++; }
        
        public double getTasaAceptacion() {
            return total > 0 ? (double) aceptadas / total * 100 : 0;
        }
    }
}