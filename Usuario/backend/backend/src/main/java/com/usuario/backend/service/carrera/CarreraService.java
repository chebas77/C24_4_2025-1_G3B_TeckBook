// Usuario/backend/backend/src/main/java/com/usuario/backend/service/carrera/CarreraService.java
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
     * 🔥 FIX: Obtiene todas las carreras activas (usando "activo" no "activa")
     */
    public List<Carrera> getAllCarrerasActivas() {
        try {
            // 🔥 DEBUGGING: Primero contar todas las carreras
            long totalCarreras = carreraRepository.count();
            logger.info("Total de carreras en BD: {}", totalCarreras);
            
            // 🔥 DEBUGGING: Contar carreras activas
            Long carrerasActivas = carreraRepository.countCarrerasActivas();
            logger.info("Total de carreras activas: {}", carrerasActivas);
            
            // Obtener carreras activas
            List<Carrera> carreras = carreraRepository.findByActivoTrue();
            logger.info("Se obtuvieron {} carreras activas de la BD", carreras.size());
            
            // 🔥 DEBUGGING: Si no hay carreras activas, obtener todas para ver qué pasa
            if (carreras.isEmpty()) {
                logger.warn("No se encontraron carreras activas. Obteniendo todas las carreras para debugging...");
                List<Carrera> todasCarreras = carreraRepository.findAllOrderByNombre();
                logger.info("Total de carreras encontradas: {}", todasCarreras.size());
                
                for (Carrera carrera : todasCarreras) {
                    logger.info("Carrera: {} - Activo: {} - ID: {}", 
                               carrera.getNombre(), carrera.getActivo(), carrera.getId());
                }
            }
            
            return carreras;
        } catch (Exception e) {
            logger.error("Error al obtener carreras activas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener las carreras", e);
        }
    }

    /**
     * Busca una carrera por su ID
     */
    public Carrera findById(Long id) {
        try {
            Optional<Carrera> carrera = carreraRepository.findById(id);
            if (carrera.isPresent()) {
                logger.debug("Carrera encontrada: {}", carrera.get().getNombre());
                return carrera.get();
            } else {
                logger.warn("No se encontró carrera con ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error al buscar carrera por ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Busca una carrera por su código
     */
    public Carrera findByCodigo(String codigo) {
        try {
            Carrera carrera = carreraRepository.findByCodigo(codigo);
            if (carrera != null) {
                logger.debug("Carrera encontrada por código {}: {}", codigo, carrera.getNombre());
            } else {
                logger.warn("No se encontró carrera con código: {}", codigo);
            }
            return carrera;
        } catch (Exception e) {
            logger.error("Error al buscar carrera por código {}: {}", codigo, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 🔥 FIX: Obtiene carreras por departamento (usando "activo")
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
     * 🔥 FIX: Busca carreras por nombre (usando "activo")
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
     * Crea una nueva carrera
     */
    public Carrera crearCarrera(Carrera carrera) {
        try {
            // Validar que no existe una carrera con el mismo código
            if (carrera.getCodigo() != null) {
                Carrera existente = carreraRepository.findByCodigo(carrera.getCodigo());
                if (existente != null) {
                    throw new IllegalArgumentException("Ya existe una carrera con el código: " + carrera.getCodigo());
                }
            }

            // 🔥 FIX: Establecer valores por defecto usando "activo"
            if (carrera.getActivo() == null) {
                carrera.setActivo(true);
            }

            Carrera carreraGuardada = carreraRepository.save(carrera);
            logger.info("Carrera creada exitosamente: {} (ID: {})", carreraGuardada.getNombre(), carreraGuardada.getId());
            return carreraGuardada;

        } catch (Exception e) {
            logger.error("Error al crear carrera: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear la carrera", e);
        }
    }

    /**
     * 🔥 FIX: Desactiva una carrera (soft delete usando "activo")
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
}