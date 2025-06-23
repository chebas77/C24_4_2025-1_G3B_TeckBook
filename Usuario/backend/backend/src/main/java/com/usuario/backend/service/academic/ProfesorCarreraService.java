package com.usuario.backend.service.academic;


import com.usuario.backend.model.entity.academic.ProfesorCarrera;
import com.usuario.backend.repository.academic.ProfesorCarreraRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import com.usuario.backend.repository.core.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfesorCarreraService {

    @Autowired
    private ProfesorCarreraRepository profesorCarreraRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CarreraRepository carreraRepository;

    // CRUD básico
    public List<ProfesorCarrera> findAll() {
        return profesorCarreraRepository.findByActivoTrue();
    }

    public Optional<ProfesorCarrera> findById(Long id) {
        return profesorCarreraRepository.findById(id);
    }

    public ProfesorCarrera save(ProfesorCarrera profesorCarrera) {
        return profesorCarreraRepository.save(profesorCarrera);
    }

    public void deleteById(Long id) {
        profesorCarreraRepository.findById(id).ifPresent(profesorCarrera -> {
            profesorCarrera.setActivo(false);
            profesorCarreraRepository.save(profesorCarrera);
        });
    }

    // Métodos de búsqueda específicos
    public List<ProfesorCarrera> findCarrerasByProfesor(Long profesorId) {
        return profesorCarreraRepository.findByProfesorIdAndActivoTrue(profesorId);
    }

    public List<ProfesorCarrera> findProfesoresByCarrera(Long carreraId) {
        return profesorCarreraRepository.findByCarreraIdAndActivoTrue(carreraId);
    }

    public Optional<ProfesorCarrera> findAutorizacion(Long profesorId, Long carreraId) {
        return profesorCarreraRepository.findByProfesorIdAndCarreraIdAndActivoTrue(profesorId, carreraId);
    }

    // Validaciones de negocio
    public boolean existsProfesor(Long profesorId) {
        return usuarioRepository.findById(profesorId).isPresent();
    }

    public boolean existsCarrera(Long carreraId) {
        return carreraRepository.findById(carreraId).isPresent();
    }

    public boolean existsAutorizacion(Long profesorId, Long carreraId) {
        return findAutorizacion(profesorId, carreraId).isPresent();
    }

    public ProfesorCarrera autorizarProfesor(ProfesorCarrera profesorCarrera) {
        // Validar profesor y carrera
        if (!existsProfesor(profesorCarrera.getProfesorId())) {
            throw new IllegalArgumentException("El profesor especificado no existe");
        }
        if (!existsCarrera(profesorCarrera.getCarreraId())) {
            throw new IllegalArgumentException("La carrera especificada no existe");
        }
        
        // Validar que no esté ya autorizado
        if (existsAutorizacion(profesorCarrera.getProfesorId(), profesorCarrera.getCarreraId())) {
            throw new IllegalArgumentException("El profesor ya está autorizado para esta carrera");
        }
        
        return save(profesorCarrera);
    }

    public void revocarAutorizacion(Long profesorId, Long carreraId) {
        findAutorizacion(profesorId, carreraId).ifPresent(autorizacion -> {
            autorizacion.setActivo(false);
            profesorCarreraRepository.save(autorizacion);
        });
    }
}