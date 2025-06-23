package com.usuario.backend.service.core;

import com.usuario.backend.model.entity.core.Carrera;
import com.usuario.backend.repository.core.CarreraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarreraService {

    private static final Logger logger = LoggerFactory.getLogger(CarreraService.class);

    @Autowired
    private CarreraRepository carreraRepository;

    /**
     * Para GET /activas
     */
    public List<Carrera> getAllCarrerasActivas() {
        return carreraRepository.findByActivoTrue();
    }

    /**
     * Para GET /{id}
     */
    public Carrera findById(Long id) {
        return carreraRepository.findById(id).orElse(null);
    }

    /**
     * Para GET /departamento/{departamentoId}
     */
    public List<Carrera> getCarrerasByDepartamento(Long departamentoId) {
        return carreraRepository.findByDepartamentoId(departamentoId);
    }

    /**
     * Para GET /departamento/{departamentoId}/activas
     */
    public List<Carrera> getCarrerasActivasByDepartamento(Long departamentoId) {
        return carreraRepository.findByDepartamentoIdAndActivoTrue(departamentoId);
    }

    /**
     * Para GET /buscar?nombre=
     */
    public List<Carrera> findByNombre(String nombre) {
        return carreraRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    /**
     * Para POST /
     */
    public Carrera crearCarrera(Carrera carrera) {
        // Validar nombre único
        if (carreraRepository.existsByNombreAndActivoTrue(carrera.getNombre())) {
            throw new IllegalArgumentException("Ya existe una carrera con el nombre: " + carrera.getNombre());
        }
        
        // Validar código único si se proporciona
        if (carrera.getCodigo() != null && !carrera.getCodigo().trim().isEmpty()) {
            if (carreraRepository.existsByCodigoAndActivoTrue(carrera.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una carrera con el código: " + carrera.getCodigo());
            }
        }
        
        return carreraRepository.save(carrera);
    }

    /**
     * Para PUT /{id}
     */
    public Carrera actualizarCarrera(Carrera carrera) {
        Optional<Carrera> existing = carreraRepository.findById(carrera.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Carrera no encontrada");
        }
        
        return carreraRepository.save(carrera);
    }

    /**
     * Para DELETE /{id}
     */
    public void desactivarCarrera(Long id) {
        Optional<Carrera> carreraOpt = carreraRepository.findById(id);
        if (carreraOpt.isPresent()) {
            Carrera carrera = carreraOpt.get();
            carrera.setActivo(false);
            carreraRepository.save(carrera);
        }
    }
}