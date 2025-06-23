package com.usuario.backend.service.academic;


import com.usuario.backend.model.entity.academic.Seccion;
import com.usuario.backend.repository.academic.SeccionRepository;
import com.usuario.backend.repository.core.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeccionService {

    @Autowired
    private SeccionRepository seccionRepository;
    
    @Autowired
    private CarreraRepository carreraRepository;

    // CRUD básico
    public List<Seccion> findAll() {
        return seccionRepository.findByActivoTrue();
    }

    public Optional<Seccion> findById(Long id) {
        return seccionRepository.findById(id);
    }

    public Seccion save(Seccion seccion) {
        return seccionRepository.save(seccion);
    }

    public void deleteById(Long id) {
        seccionRepository.findById(id).ifPresent(seccion -> {
            seccion.setActivo(false);
            seccionRepository.save(seccion);
        });
    }

    // Métodos de búsqueda específicos
    public List<Seccion> findByCarrera(Long carreraId) {
        return seccionRepository.findByCarreraIdAndActivoTrue(carreraId);
    }

    public List<Seccion> findByCarreraYCiclo(Long carreraId, Integer ciclo) {
        return seccionRepository.findByCarreraIdAndCicloAndActivoTrue(carreraId, ciclo);
    }

    public List<Seccion> findByPeriodo(String periodoAcademico) {
        return seccionRepository.findByPeriodoAcademicoAndActivoTrue(periodoAcademico);
    }

    public Optional<Seccion> findSeccionEspecifica(String letra, Integer ciclo, Long carreraId, String periodoAcademico) {
        return seccionRepository.findByLetraAndCicloAndCarreraIdAndPeriodoAcademico(letra, ciclo, carreraId, periodoAcademico);
    }

    public Long countEstudiantes(Long seccionId) {
        return seccionRepository.countEstudiantesBySeccion(seccionId);
    }

    // Validaciones de negocio
    public boolean existsCarrera(Long carreraId) {
        return carreraRepository.findById(carreraId).isPresent();
    }

    public boolean hasCapacidad(Long seccionId) {
        return seccionRepository.findById(seccionId).map(seccion -> {
            Long estudiantes = countEstudiantes(seccionId);
            return estudiantes < seccion.getCapacidadMaxima();
        }).orElse(false);
    }

    public Seccion createSeccion(Seccion seccion) {
        // Validar carrera
        if (!existsCarrera(seccion.getCarreraId())) {
            throw new IllegalArgumentException("La carrera especificada no existe");
        }
        
        // Validar que no existe la misma sección
        if (findSeccionEspecifica(seccion.getLetra(), seccion.getCiclo(), 
                                 seccion.getCarreraId(), seccion.getPeriodoAcademico()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una sección con estos parámetros");
        }
        
        // Validar ciclo
        if (seccion.getCiclo() < 1 || seccion.getCiclo() > 6) {
            throw new IllegalArgumentException("El ciclo debe estar entre 1 y 6");
        }
        
        // Validar letra
        if (!seccion.getLetra().matches("[A-Z]")) {
            throw new IllegalArgumentException("La letra debe ser una letra mayúscula");
        }
        
        return save(seccion);
    }

    public Seccion updateSeccion(Long id, Seccion seccionActualizada) {
        return seccionRepository.findById(id).map(seccion -> {
            // Validar carrera
            if (!existsCarrera(seccionActualizada.getCarreraId())) {
                throw new IllegalArgumentException("La carrera especificada no existe");
            }
            
            seccion.setLetra(seccionActualizada.getLetra());
            seccion.setCiclo(seccionActualizada.getCiclo());
            seccion.setCarreraId(seccionActualizada.getCarreraId());
            seccion.setPeriodoAcademico(seccionActualizada.getPeriodoAcademico());
            seccion.setCapacidadMaxima(seccionActualizada.getCapacidadMaxima());
            
            return seccionRepository.save(seccion);
        }).orElseThrow(() -> new IllegalArgumentException("Sección no encontrada"));
    }
}