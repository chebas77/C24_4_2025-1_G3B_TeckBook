// Usuario/backend/backend/src/main/java/com/usuario/backend/service/aula/AulaVirtualService.java
package com.usuario.backend.service.aula;

import com.usuario.backend.model.entity.AulaVirtual;
import com.usuario.backend.repository.AulaVirtualRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AulaVirtualService {

    private static final Logger logger = LoggerFactory.getLogger(AulaVirtualService.class);

    @Autowired
    private AulaVirtualRepository aulaRepository;

    public List<AulaVirtual> getAllAulas() {
        return aulaRepository.findAll();
    }

    public List<AulaVirtual> getAulasActivas() {
        return aulaRepository.findByEstado("activa");
    }

    public List<AulaVirtual> getAulasByProfesor(Long profesorId) {
        return aulaRepository.findByProfesorId(profesorId);
    }

    public AulaVirtual crearAula(AulaVirtual aula) {
        try {
            logger.info("📝 Creando aula: {} para profesor ID: {}", aula.getNombre(), aula.getProfesorId());
            
            // Validar que el profesor ID esté asignado
            if (aula.getProfesorId() == null) {
                throw new IllegalArgumentException("El profesor ID es requerido para crear un aula");
            }
            
            // Generar código de acceso único si no se proporciona
            if (aula.getCodigoAcceso() == null || aula.getCodigoAcceso().isEmpty()) {
                aula.setCodigoAcceso(generateCodigoAcceso());
            }
            
            // Establecer estado por defecto
            if (aula.getEstado() == null || aula.getEstado().isEmpty()) {
                aula.setEstado("activa");
            }
            
            logger.info("🔧 Datos del aula antes de guardar: profesor_id={}, codigo={}, estado={}", 
                       aula.getProfesorId(), aula.getCodigoAcceso(), aula.getEstado());
            
            AulaVirtual aulaGuardada = aulaRepository.save(aula);
            logger.info("✅ Aula guardada exitosamente: ID={}, profesor_id={}", 
                       aulaGuardada.getId(), aulaGuardada.getProfesorId());
            
            return aulaGuardada;
            
        } catch (Exception e) {
            logger.error("❌ Error al crear aula: {}", e.getMessage(), e);
            throw e;
        }
    }

    public AulaVirtual findById(Long id) {
        return aulaRepository.findById(id).orElse(null);
    }

    public AulaVirtual findByCodigoAcceso(String codigo) {
        return aulaRepository.findByCodigoAcceso(codigo);
    }

    public AulaVirtual actualizarAula(AulaVirtual aula) {
        return aulaRepository.save(aula);
    }

    public void eliminarAula(Long id) {
        AulaVirtual aula = findById(id);
        if (aula != null) {
            aula.setEstado("inactiva");
            aulaRepository.save(aula);
        }
    }

    private String generateCodigoAcceso() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}