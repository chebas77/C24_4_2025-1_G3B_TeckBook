package com.usuario.backend.service.carrera;

import com.usuario.backend.model.entity.Seccion;
import com.usuario.backend.repository.SeccionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeccionService {

    private static final Logger logger = LoggerFactory.getLogger(SeccionService.class);

    @Autowired
    private SeccionRepository seccionRepository;

    public List<Seccion> getSeccionesByCarreraAndCiclo(Long carreraId, Long cicloId) {
        try {
            List<Seccion> secciones = seccionRepository.findByCarreraIdAndCiclo(carreraId, cicloId.intValue());
            logger.info("Se obtuvieron {} secciones para carrera {} y ciclo {}", 
                       secciones.size(), carreraId, cicloId);
            return secciones;
        } catch (Exception e) {
            logger.error("Error al obtener secciones para carrera {} y ciclo {}: {}", 
                        carreraId, cicloId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener secciones", e);
        }
    }

    public List<Seccion> getSeccionesByCarrera(Long carreraId) {
        try {
            List<Seccion> secciones = seccionRepository.findByCarreraIdOrderByCicloAndNombre(carreraId);
            logger.info("Se obtuvieron {} secciones para carrera {}", secciones.size(), carreraId);
            return secciones;
        } catch (Exception e) {
            logger.error("Error al obtener secciones para carrera {}: {}", carreraId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener secciones de carrera", e);
        }
    }
}