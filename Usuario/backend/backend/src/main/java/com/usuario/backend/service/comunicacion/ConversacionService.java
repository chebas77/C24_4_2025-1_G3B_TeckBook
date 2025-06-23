package com.usuario.backend.service.comunicacion;


import com.usuario.backend.model.entity.comunicacion.Conversacion;
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
public class ConversacionService {

    @Autowired
    private ConversacionRepository conversacionRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // CRUD básico
    public List<Conversacion> findAll() {
        return conversacionRepository.findByActivoTrue();
    }

    public Optional<Conversacion> findById(Long id) {
        return conversacionRepository.findById(id);
    }

    public Conversacion save(Conversacion conversacion) {
        return conversacionRepository.save(conversacion);
    }

    public void deleteById(Long id) {
        conversacionRepository.findById(id).ifPresent(conversacion -> {
            conversacion.setActivo(false);
            conversacionRepository.save(conversacion);
        });
    }

    // Métodos de búsqueda específicos
    public List<Conversacion> findByUsuario(Long usuarioId) {
        return conversacionRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Conversacion> findConversacionEntreUsuarios(Long usuario1Id, Long usuario2Id) {
        return conversacionRepository.findByUsuarios(usuario1Id, usuario2Id);
    }

    public List<Conversacion> findConversacionesRecientes(Long usuarioId) {
        return conversacionRepository.findConversacionesRecientes(usuarioId);
    }

    public Long countConversaciones(Long usuarioId) {
        return conversacionRepository.countByUsuarioId(usuarioId);
    }

    // Validaciones de negocio
    public boolean existsUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId).isPresent();
    }

    public boolean existsConversacion(Long usuario1Id, Long usuario2Id) {
        return findConversacionEntreUsuarios(usuario1Id, usuario2Id).isPresent();
    }

    public Conversacion createConversacion(Long usuario1Id, Long usuario2Id) {
        // Validar usuarios
        if (!existsUsuario(usuario1Id)) {
            throw new IllegalArgumentException("El usuario 1 especificado no existe");
        }
        if (!existsUsuario(usuario2Id)) {
            throw new IllegalArgumentException("El usuario 2 especificado no existe");
        }
        
        // Validar que no sean el mismo usuario
        if (usuario1Id.equals(usuario2Id)) {
            throw new IllegalArgumentException("No se puede crear una conversación consigo mismo");
        }
        
        // Verificar si ya existe la conversación
        Optional<Conversacion> existing = findConversacionEntreUsuarios(usuario1Id, usuario2Id);
        if (existing.isPresent()) {
            // Reactivar si estaba inactiva
            Conversacion conversacion = existing.get();
            if (!conversacion.getActivo()) {
                conversacion.setActivo(true);
                return save(conversacion);
            }
            return conversacion; // Ya existe y está activa
        }
        
        // Crear nueva conversación
        Conversacion nuevaConversacion = new Conversacion();
        nuevaConversacion.setUsuario1Id(usuario1Id);
        nuevaConversacion.setUsuario2Id(usuario2Id);
        nuevaConversacion.setFechaInicio(LocalDateTime.now());
        nuevaConversacion.setActivo(true);
        
        return save(nuevaConversacion);
    }

    public Conversacion getOrCreateConversacion(Long usuario1Id, Long usuario2Id) {
        return findConversacionEntreUsuarios(usuario1Id, usuario2Id)
                .orElseGet(() -> createConversacion(usuario1Id, usuario2Id));
    }

    public void actualizarUltimoMensaje(Long conversacionId, Long mensajeId, LocalDateTime fechaMensaje) {
        conversacionRepository.findById(conversacionId).ifPresent(conversacion -> {
            conversacion.setUltimoMensajeId(mensajeId);
            conversacion.setFechaUltimoMensaje(fechaMensaje);
            conversacionRepository.save(conversacion);
        });
    }

    public void archivarConversacion(Long conversacionId) {
        conversacionRepository.findById(conversacionId).ifPresent(conversacion -> {
            conversacion.setActivo(false);
            conversacionRepository.save(conversacion);
        });
    }

    // Métodos de utilidad
    public Long getOtroUsuario(Long conversacionId, Long usuarioActualId) {
        return conversacionRepository.findById(conversacionId).map(conversacion -> {
            if (conversacion.getUsuario1Id().equals(usuarioActualId)) {
                return conversacion.getUsuario2Id();
            } else if (conversacion.getUsuario2Id().equals(usuarioActualId)) {
                return conversacion.getUsuario1Id();
            } else {
                throw new IllegalArgumentException("El usuario no pertenece a esta conversación");
            }
        }).orElseThrow(() -> new IllegalArgumentException("Conversación no encontrada"));
    }

    public boolean perteneceAConversacion(Long conversacionId, Long usuarioId) {
        return conversacionRepository.findById(conversacionId).map(conversacion -> 
            conversacion.getUsuario1Id().equals(usuarioId) || 
            conversacion.getUsuario2Id().equals(usuarioId)
        ).orElse(false);
    }
}
