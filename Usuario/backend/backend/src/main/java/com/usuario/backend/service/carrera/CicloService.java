package com.usuario.backend.service.carrera;
import com.usuario.backend.model.entity.Ciclo;
import com.usuario.backend.repository.CicloRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CicloService {

    private static final Logger logger = LoggerFactory.getLogger(CicloService.class);

    @Autowired
    private CicloRepository cicloRepository;

    public List<Ciclo> getAllCiclos() {
        try {
            List<Ciclo> ciclos = cicloRepository.findByOrderByNumero();
            logger.info("Se obtuvieron {} ciclos", ciclos.size());
            return ciclos;
        } catch (Exception e) {
            logger.error("Error al obtener ciclos: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener los ciclos", e);
        }
    }

    public List<Ciclo> getCiclosByCarrera(Long carreraId) {
        try {
            // 🔥 USAR MÉTODO ALTERNATIVO: Obtener todos los ciclos disponibles
            List<Ciclo> ciclos = cicloRepository.findAllCiclos();
            logger.info("Se obtuvieron {} ciclos para carrera {}", ciclos.size(), carreraId);
            return ciclos;
        } catch (Exception e) {
            logger.error("Error al obtener ciclos para carrera {}: {}", carreraId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener ciclos de carrera", e);
        }
    }
}
