package com.usuario.backend.service.carrera;

import com.usuario.backend.model.entity.Carrera;
import com.usuario.backend.repository.CarreraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarreraService {

    private static final Logger logger = LoggerFactory.getLogger(CarreraService.class);

    @Autowired
    private CarreraRepository carreraRepository;

    /**
     * 📚 Obtiene todas las carreras activas
     */
    public List<Carrera> getAllCarrerasActivas() {
        try {
            List<Carrera> carreras = carreraRepository.findByActivoTrue();
            logger.info("Se obtuvieron {} carreras activas", carreras.size());
            return carreras;
        } catch (Exception e) {
            logger.error("Error al obtener carreras activas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener las carreras activas", e);
        }
    }

    /**
     * 🔍 Busca una carrera por ID
     */
    public Carrera findById(Long id) {
        try {
            Optional<Carrera> carrera = carreraRepository.findById(id);
            return carrera.orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar carrera por ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 🔍 Busca una carrera por código
     */
    public Carrera findByCodigo(String codigo) {
        try {
            return carreraRepository.findByCodigo(codigo);
        } catch (Exception e) {
            logger.error("Error al buscar carrera por código {}: {}", codigo, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 🏢 Obtiene carreras por departamento
     */
    public List<Carrera> getCarrerasByDepartamento(Long departamentoId) {
        try {
            List<Carrera> carreras = carreraRepository.findByDepartamentoIdAndActivoTrue(departamentoId);
            logger.info("Se obtuvieron {} carreras para el departamento {}", carreras.size(), departamentoId);
            return carreras;
        } catch (Exception e) {
            logger.error("Error al obtener carreras del departamento {}: {}", departamentoId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener carreras del departamento", e);
        }
    }

    /**
     * 🔎 Busca carreras por nombre
     */
    public List<Carrera> findByNombre(String nombre) {
        try {
            List<Carrera> carreras = carreraRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
            logger.info("Se encontraron {} carreras que contienen: {}", carreras.size(), nombre);
            return carreras;
        } catch (Exception e) {
            logger.error("Error al buscar carreras por nombre {}: {}", nombre, e.getMessage(), e);
            throw new RuntimeException("Error al buscar carreras por nombre", e);
        }
    }

    /**
     * ➕ Crea una nueva carrera
     */
    public Carrera crearCarrera(Carrera carrera) {
        try {
            // Validar código duplicado
            if (carrera.getCodigo() != null) {
                Carrera existente = carreraRepository.findByCodigo(carrera.getCodigo());
                if (existente != null) {
                    throw new IllegalArgumentException("Ya existe una carrera con el código: " + carrera.getCodigo());
                }
            }

            // Establecer activo por defecto
            if (carrera.getActivo() == null) {
                carrera.setActivo(true);
            }

            Carrera carreraGuardada = carreraRepository.save(carrera);
            logger.info("Carrera creada: {} (ID: {})", carreraGuardada.getNombre(), carreraGuardada.getId());
            return carreraGuardada;

        } catch (Exception e) {
            logger.error("Error al crear carrera: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear la carrera", e);
        }
    }

    /**
     * ✏️ Actualiza una carrera
     */
    public Carrera actualizarCarrera(Carrera carrera) {
        try {
            Carrera carreraActualizada = carreraRepository.save(carrera);
            logger.info("Carrera actualizada: {} (ID: {})", carreraActualizada.getNombre(), carreraActualizada.getId());
            return carreraActualizada;
        } catch (Exception e) {
            logger.error("Error al actualizar carrera: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar la carrera", e);
        }
    }

    /**
     * 🗑️ Desactiva una carrera
     */
    public void desactivarCarrera(Long id) {
        try {
            Optional<Carrera> carreraOpt = carreraRepository.findById(id);
            if (carreraOpt.isPresent()) {
                Carrera carrera = carreraOpt.get();
                carrera.setActivo(false);
                carreraRepository.save(carrera);
                logger.info("Carrera desactivada: {} (ID: {})", carrera.getNombre(), id);
            } else {
                throw new IllegalArgumentException("No se encontró carrera con ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error al desactivar carrera {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al desactivar la carrera", e);
        }
    }
    public List<Carrera> getCarrerasActivasByDepartamento(Long departamentoId) {
    try {
        List<Carrera> carreras = carreraRepository.findByDepartamentoIdAndActivoTrue(departamentoId);
        logger.info("Se obtuvieron {} carreras activas para departamento {}", 
                   carreras.size(), departamentoId);
        return carreras;
    } catch (Exception e) {
        logger.error("Error al obtener carreras del departamento {}: {}", 
                    departamentoId, e.getMessage(), e);
        throw new RuntimeException("Error al obtener carreras del departamento", e);
    }
}
}