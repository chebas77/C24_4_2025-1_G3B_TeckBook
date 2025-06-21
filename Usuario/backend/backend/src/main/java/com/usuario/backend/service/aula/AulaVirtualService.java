
package com.usuario.backend.service.aula;

import com.usuario.backend.model.entity.AulaVirtual;
import com.usuario.backend.repository.AulaVirtualRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AulaVirtualService {

    private static final Logger logger = LoggerFactory.getLogger(AulaVirtualService.class);

    @Autowired
    private AulaVirtualRepository aulaVirtualRepository;

    /**
     * 🎓 OBTENER AULAS POR PROFESOR (Solo las que él creó)
     */
    public List<AulaVirtual> getAulasByProfesor(Long profesorId) {
        try {
            List<AulaVirtual> aulas = aulaVirtualRepository.findByProfesorIdOrderByFechaInicioDesc(profesorId);
            logger.info("Profesor {} tiene {} aulas creadas", profesorId, aulas.size());
            return aulas;
        } catch (Exception e) {
            logger.error("Error al obtener aulas del profesor {}: {}", profesorId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener aulas del profesor", e);
        }
    }

    /**
     * 👨‍🎓 OBTENER AULAS POR ESTUDIANTE (Solo donde está inscrito)
     */
    public List<AulaVirtual> getAulasByEstudiante(Long estudianteId) {
        try {
            List<AulaVirtual> aulas = aulaVirtualRepository.findAulasByEstudianteId(estudianteId);
            logger.info("Estudiante {} está inscrito en {} aulas", estudianteId, aulas.size());
            return aulas;
        } catch (Exception e) {
            logger.error("Error al obtener aulas del estudiante {}: {}", estudianteId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener aulas del estudiante", e);
        }
    }

    /**
     * 🔍 VERIFICAR SI ESTUDIANTE ESTÁ INSCRITO EN AULA
     */
    public boolean isEstudianteInscritoEnAula(Long estudianteId, Long aulaId) {
        try {
            return aulaVirtualRepository.existsEstudianteInAula(estudianteId, aulaId);
        } catch (Exception e) {
            logger.error("Error al verificar inscripción del estudiante {} en aula {}: {}", 
                        estudianteId, aulaId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 📊 OBTENER TOTAL DE ESTUDIANTES POR PROFESOR
     */
    public int getTotalEstudiantesByProfesor(Long profesorId) {
        try {
            return aulaVirtualRepository.countEstudiantesByProfesor(profesorId);
        } catch (Exception e) {
            logger.error("Error al contar estudiantes del profesor {}: {}", profesorId, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * ➕ CREAR AULA
     */
    public AulaVirtual crearAula(AulaVirtual aula) {
        try {
            // 🔧 GENERAR CÓDIGO DE ACCESO SI NO EXISTE
            if (aula.getCodigoAcceso() == null || aula.getCodigoAcceso().isEmpty()) {
                aula.setCodigoAcceso(generateCodigoAcceso());
            }

            // 🔧 ESTABLECER ESTADO POR DEFECTO
            if (aula.getEstado() == null || aula.getEstado().isEmpty()) {
                aula.setEstado("activa");
            }

            AulaVirtual aulaGuardada = aulaVirtualRepository.save(aula);
            logger.info("Aula creada: {} (ID: {}, Código: {})", 
                       aulaGuardada.getNombre(), aulaGuardada.getId(), aulaGuardada.getCodigoAcceso());
            return aulaGuardada;

        } catch (Exception e) {
            logger.error("Error al crear aula '{}': {}", aula.getNombre(), e.getMessage(), e);
            throw new RuntimeException("Error al crear el aula", e);
        }
    }

    /**
     * 🔄 ACTUALIZAR AULA
     */
    public AulaVirtual actualizarAula(AulaVirtual aula) {
        try {
            AulaVirtual aulaActualizada = aulaVirtualRepository.save(aula);
            logger.info("Aula actualizada: {} (ID: {})", aulaActualizada.getNombre(), aulaActualizada.getId());
            return aulaActualizada;
        } catch (Exception e) {
            logger.error("Error al actualizar aula ID {}: {}", aula.getId(), e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el aula", e);
        }
    }

    /**
     * 🗑️ ELIMINAR AULA (Soft delete)
     */
    public void eliminarAula(Long aulaId) {
        try {
            Optional<AulaVirtual> aulaOpt = aulaVirtualRepository.findById(aulaId);
            if (aulaOpt.isPresent()) {
                AulaVirtual aula = aulaOpt.get();
                aula.setEstado("eliminada");
                aulaVirtualRepository.save(aula);
                logger.info("Aula eliminada (soft delete): {} (ID: {})", aula.getNombre(), aulaId);
            } else {
                throw new IllegalArgumentException("Aula no encontrada con ID: " + aulaId);
            }
        } catch (Exception e) {
            logger.error("Error al eliminar aula ID {}: {}", aulaId, e.getMessage(), e);
            throw new RuntimeException("Error al eliminar el aula", e);
        }
    }

    /**
     * 🔍 BUSCAR AULA POR ID
     */
    public AulaVirtual findById(Long id) {
        try {
            Optional<AulaVirtual> aula = aulaVirtualRepository.findById(id);
            return aula.orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar aula por ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 🔍 BUSCAR AULA POR CÓDIGO DE ACCESO
     */
    public AulaVirtual findByCodigoAcceso(String codigoAcceso) {
        try {
            return aulaVirtualRepository.findByCodigoAcceso(codigoAcceso);
        } catch (Exception e) {
            logger.error("Error al buscar aula por código {}: {}", codigoAcceso, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 📋 OBTENER TODAS LAS AULAS (Solo para admin)
     */
    public List<AulaVirtual> getAllAulas() {
        try {
            List<AulaVirtual> aulas = aulaVirtualRepository.findByEstadoOrderByFechaInicioDesc("activa");
            logger.info("Se obtuvieron {} aulas activas en total", aulas.size());
            return aulas;
        } catch (Exception e) {
            logger.error("Error al obtener todas las aulas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener las aulas", e);
        }
    }

    /**
     * 🔧 GENERAR CÓDIGO DE ACCESO ÚNICO
     */
    private String generateCodigoAcceso() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        
        // 🔄 GENERAR HASTA ENCONTRAR UNO ÚNICO
        boolean exists = true;
        while (exists) {
            codigo.setLength(0);
            for (int i = 0; i < 8; i++) {
                codigo.append(chars.charAt((int) (Math.random() * chars.length())));
            }
            
            // 🔍 VERIFICAR QUE NO EXISTA
            exists = aulaVirtualRepository.existsByCodigoAcceso(codigo.toString());
        }
        
        return codigo.toString();
    }

    /**
     * 🔍 BUSCAR AULAS POR NOMBRE (Para búsquedas)
     */
    public List<AulaVirtual> searchAulasByNombre(String nombre, Long profesorId) {
        try {
            if (profesorId != null) {
                // Buscar solo en las aulas del profesor
                return aulaVirtualRepository.findByNombreContainingIgnoreCaseAndProfesorId(nombre, profesorId);
            } else {
                // Buscar en todas las aulas activas
                return aulaVirtualRepository.findByNombreContainingIgnoreCaseAndEstado(nombre, "activa");
            }
        } catch (Exception e) {
            logger.error("Error al buscar aulas por nombre '{}': {}", nombre, e.getMessage(), e);
            throw new RuntimeException("Error al buscar aulas", e);
        }
    }

    /**
     * 📈 OBTENER ESTADÍSTICAS GENERALES
     */
    public Map<String, Object> getEstadisticasGenerales() {
        try {
            long totalAulas = aulaVirtualRepository.count();
            long aulasActivas = aulaVirtualRepository.countByEstado("activa");
            long aulasInactivas = aulaVirtualRepository.countByEstado("inactiva");
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("totalAulas", totalAulas);
            estadisticas.put("aulasActivas", aulasActivas);
            estadisticas.put("aulasInactivas", aulasInactivas);
            estadisticas.put("timestamp", System.currentTimeMillis());
            
            return estadisticas;
            
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas generales: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener estadísticas", e);
        }
    }
}