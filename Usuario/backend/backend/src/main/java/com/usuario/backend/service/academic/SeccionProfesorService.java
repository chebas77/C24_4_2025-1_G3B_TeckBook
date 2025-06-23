package com.usuario.backend.service.academic;


import com.usuario.backend.model.entity.academic.SeccionProfesor;
import com.usuario.backend.repository.academic.SeccionProfesorRepository;
import com.usuario.backend.repository.academic.SeccionRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeccionProfesorService {

    @Autowired
    private SeccionProfesorRepository seccionProfesorRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SeccionRepository seccionRepository;

    // CRUD básico
    public List<SeccionProfesor> findAll() {
        return seccionProfesorRepository.findByActivoTrue();
    }

    public Optional<SeccionProfesor> findById(Long id) {
        return seccionProfesorRepository.findById(id);
    }

    public SeccionProfesor save(SeccionProfesor seccionProfesor) {
        return seccionProfesorRepository.save(seccionProfesor);
    }

    public void deleteById(Long id) {
        seccionProfesorRepository.findById(id).ifPresent(seccionProfesor -> {
            seccionProfesor.setActivo(false);
            seccionProfesorRepository.save(seccionProfesor);
        });
    }

    // Métodos de búsqueda específicos
    public List<SeccionProfesor> findSeccionesByProfesor(Long profesorId) {
        return seccionProfesorRepository.findByProfesorIdAndActivoTrue(profesorId);
    }

    public List<SeccionProfesor> findProfesoresBySeccion(Long seccionId) {
        return seccionProfesorRepository.findBySeccionIdAndActivoTrue(seccionId);
    }

    public Optional<SeccionProfesor> findAsignacion(Long profesorId, Long seccionId) {
        return seccionProfesorRepository.findByProfesorIdAndSeccionIdAndActivoTrue(profesorId, seccionId);
    }

    // Validaciones de negocio
    public boolean existsProfesor(Long profesorId) {
        return usuarioRepository.findById(profesorId).isPresent();
    }

    public boolean existsSeccion(Long seccionId) {
        return seccionRepository.findById(seccionId).isPresent();
    }

    public boolean existsAsignacion(Long profesorId, Long seccionId) {
        return findAsignacion(profesorId, seccionId).isPresent();
    }

    public SeccionProfesor asignarProfesor(SeccionProfesor seccionProfesor) {
        // Validar profesor y sección
        if (!existsProfesor(seccionProfesor.getProfesorId())) {
            throw new IllegalArgumentException("El profesor especificado no existe");
        }
        if (!existsSeccion(seccionProfesor.getSeccionId())) {
            throw new IllegalArgumentException("La sección especificada no existe");
        }
        
        // Validar que no esté ya asignado
        if (existsAsignacion(seccionProfesor.getProfesorId(), seccionProfesor.getSeccionId())) {
            throw new IllegalArgumentException("El profesor ya está asignado a esta sección");
        }
        
        return save(seccionProfesor);
    }

    public void removerAsignacion(Long profesorId, Long seccionId) {
        findAsignacion(profesorId, seccionId).ifPresent(asignacion -> {
            asignacion.setActivo(false);
            seccionProfesorRepository.save(asignacion);
        });
    }
}