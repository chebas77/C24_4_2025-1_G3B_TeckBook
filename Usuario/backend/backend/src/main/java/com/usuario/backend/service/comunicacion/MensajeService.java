package com.usuario.backend.service.comunicacion;
import com.usuario.backend.model.entity.comunicacion.Mensaje;
import com.usuario.backend.repository.comunicacion.MensajeRepository;
import com.usuario.backend.repository.comunicacion.ConversacionRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;
    
    @Autowired
    private ConversacionRepository conversacionRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ConversacionService conversacionService;

    // CRUD básico
    public List<Mensaje> findAll() {
        return mensajeRepository.findAll();
    }

    public Optional<Mensaje> findById(Long id) {
        return mensajeRepository.findById(id);
    }

    public Mensaje save(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    public void deleteById(Long id) {
        mensajeRepository.deleteById(id);
    }

    // Métodos de búsqueda específicos
    public List<Mensaje> findByConversacion(Long conversacionId) {
        return mensajeRepository.findByConversacionIdOrderByFechaEnvio(conversacionId);
    }

    public List<Mensaje> findByRemitente(Long remitenteId) {
        return mensajeRepository.findByRemitenteIdOrderByFechaEnvioDesc(remitenteId);
    }

    public List<Mensaje> findByTipo(Mensaje.TipoMensaje tipoMensaje) {
        return mensajeRepository.findByTipoMensaje(tipoMensaje);
    }

    public List<Mensaje> findMensajesNoLeidos(Long conversacionId) {
        return mensajeRepository.findByConversacionIdAndLeidoFalse(conversacionId);
    }

    public List<Mensaje> findMensajesNoLeidosParaUsuario(Long usuarioId) {
        return mensajeRepository.findMensajesNoLeidosParaUsuario(usuarioId);
    }

    public Long countMensajesNoLeidos(Long conversacionId) {
        return mensajeRepository.countByConversacionIdAndLeidoFalse(conversacionId);
    }

    public Long countMensajesNoLeidosParaUsuario(Long usuarioId) {
        return mensajeRepository.countMensajesNoLeidosParaUsuario(usuarioId);
    }

    public Optional<Mensaje> findUltimoMensaje(Long conversacionId) {
        return Optional.ofNullable(mensajeRepository.findUltimoMensajeByConversacion(conversacionId));
    }

    // Validaciones de negocio
    public boolean existsConversacion(Long conversacionId) {
        return conversacionRepository.findById(conversacionId).isPresent();
    }

    public boolean existsRemitente(Long remitenteId) {
        return usuarioRepository.findById(remitenteId).isPresent();
    }

    public boolean perteneceAConversacion(Long conversacionId, Long usuarioId) {
        return conversacionService.perteneceAConversacion(conversacionId, usuarioId);
    }

    public Mensaje enviarMensaje(Mensaje mensaje) {
        // Validar conversación
        if (!existsConversacion(mensaje.getConversacionId())) {
            throw new IllegalArgumentException("La conversación especificada no existe");
        }
        
        // Validar remitente
        if (!existsRemitente(mensaje.getRemitenteId())) {
            throw new IllegalArgumentException("El remitente especificado no existe");
        }
        
        // Validar que el remitente pertenece a la conversación
        if (!perteneceAConversacion(mensaje.getConversacionId(), mensaje.getRemitenteId())) {
            throw new IllegalArgumentException("El remitente no pertenece a esta conversación");
        }
        
        // Validar contenido
        if (mensaje.getContenido() == null || mensaje.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }
        
        // Validar tipo y archivo
        if (mensaje.getTipoMensaje() != Mensaje.TipoMensaje.TEXTO && 
            (mensaje.getArchivoUrl() == null || mensaje.getArchivoUrl().trim().isEmpty())) {
            throw new IllegalArgumentException("Los mensajes de imagen/archivo requieren una URL");
        }
        
        // Establecer fecha de envío
        if (mensaje.getFechaEnvio() == null) {
            mensaje.setFechaEnvio(LocalDateTime.now());
        }
        
        // Establecer como no leído
        mensaje.setLeido(false);
        
        // Guardar mensaje
        Mensaje mensajeGuardado = save(mensaje);
        
        // Actualizar última actividad en la conversación
        conversacionService.actualizarUltimoMensaje(
            mensaje.getConversacionId(), 
            mensajeGuardado.getId(), 
            mensajeGuardado.getFechaEnvio()
        );
        
        return mensajeGuardado;
    }

    public void marcarComoLeido(Long mensajeId, Long usuarioId) {
        mensajeRepository.findById(mensajeId).ifPresent(mensaje -> {
            // Validar que el usuario puede marcar este mensaje como leído
            if (!perteneceAConversacion(mensaje.getConversacionId(), usuarioId)) {
                throw new IllegalArgumentException("No tienes permiso para marcar este mensaje como leído");
            }
            
            // No marcar como leído si es el propio remitente
            if (mensaje.getRemitenteId().equals(usuarioId)) {
                return;
            }
            
            mensaje.setLeido(true);
            mensaje.setFechaLectura(LocalDateTime.now());
            mensajeRepository.save(mensaje);
        });
    }

    public void marcarConversacionComoLeida(Long conversacionId, Long usuarioId) {
        // Validar permisos
        if (!perteneceAConversacion(conversacionId, usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para acceder a esta conversación");
        }
        
        List<Mensaje> mensajesNoLeidos = findMensajesNoLeidos(conversacionId);
        for (Mensaje mensaje : mensajesNoLeidos) {
            // Solo marcar como leídos los mensajes que no son del usuario actual
            if (!mensaje.getRemitenteId().equals(usuarioId)) {
                mensaje.setLeido(true);
                mensaje.setFechaLectura(LocalDateTime.now());
                mensajeRepository.save(mensaje);
            }
        }
    }

    // Métodos de utilidad
    public boolean tienePermisosLectura(Long conversacionId, Long usuarioId) {
        return perteneceAConversacion(conversacionId, usuarioId);
    }

    public List<Mensaje> getMensajesConAcceso(Long conversacionId, Long usuarioId) {
        if (!tienePermisosLectura(conversacionId, usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para acceder a esta conversación");
        }
        return findByConversacion(conversacionId);
    }
}