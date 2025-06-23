package com.usuario.backend.service.academic;



import com.usuario.backend.model.entity.academic.UsuarioSeccion;
import com.usuario.backend.repository.academic.UsuarioSeccionRepository;
import com.usuario.backend.repository.academic.SeccionRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioSeccionService {

    @Autowired
    private UsuarioSeccionRepository usuarioSeccionRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SeccionRepository seccionRepository;
    
    @Autowired
    private SeccionService seccionService;

    // CRUD básico
    public List<UsuarioSeccion> findAll() {
        return usuarioSeccionRepository.findByActivoTrue();
    }

    public Optional<UsuarioSeccion> findById(Long id) {
        return usuarioSeccionRepository.findById(id);
    }

    public UsuarioSeccion save(UsuarioSeccion usuarioSeccion) {
        return usuarioSeccionRepository.save(usuarioSeccion);
    }

    public void deleteById(Long id) {
        usuarioSeccionRepository.findById(id).ifPresent(usuarioSeccion -> {
            usuarioSeccion.setActivo(false);
            usuarioSeccionRepository.save(usuarioSeccion);
        });
    }

    // Métodos de búsqueda específicos
    public List<UsuarioSeccion> findEstudiantesBySeccion(Long seccionId) {
        return usuarioSeccionRepository.findBySeccionIdAndActivoTrue(seccionId);
    }

    public List<UsuarioSeccion> findSeccionesByEstudiante(Long usuarioId) {
        return usuarioSeccionRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    public Optional<UsuarioSeccion> findAsignacion(Long usuarioId, Long seccionId) {
        return usuarioSeccionRepository.findByUsuarioIdAndSeccionIdAndActivoTrue(usuarioId, seccionId);
    }

    public Long countEstudiantesBySeccion(Long seccionId) {
        return usuarioSeccionRepository.countBySeccionIdAndActivoTrue(seccionId);
    }

    // Validaciones de negocio
    public boolean existsUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId).isPresent();
    }

    public boolean existsSeccion(Long seccionId) {
        return seccionRepository.findById(seccionId).isPresent();
    }

    public boolean existsAsignacion(Long usuarioId, Long seccionId) {
        return findAsignacion(usuarioId, seccionId).isPresent();
    }

    public UsuarioSeccion matricularEstudiante(UsuarioSeccion usuarioSeccion) {
        // Validar usuario y sección
        if (!existsUsuario(usuarioSeccion.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario especificado no existe");
        }
        if (!existsSeccion(usuarioSeccion.getSeccionId())) {
            throw new IllegalArgumentException("La sección especificada no existe");
        }
        
        // Validar que no esté ya matriculado
        if (existsAsignacion(usuarioSeccion.getUsuarioId(), usuarioSeccion.getSeccionId())) {
            throw new IllegalArgumentException("El estudiante ya está matriculado en esta sección");
        }
        
        // Validar capacidad de la sección
        if (!seccionService.hasCapacidad(usuarioSeccion.getSeccionId())) {
            throw new IllegalArgumentException("La sección ha alcanzado su capacidad máxima");
        }
        
        return save(usuarioSeccion);
    }

    public void desmatricularEstudiante(Long usuarioId, Long seccionId) {
        findAsignacion(usuarioId, seccionId).ifPresent(asignacion -> {
            asignacion.setActivo(false);
            usuarioSeccionRepository.save(asignacion);
        });
    }
}