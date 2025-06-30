package com.usuario.backend.service.aula;

import com.usuario.backend.model.entity.AulaVirtual;
import com.usuario.backend.model.entity.AulaEstudiante;
import com.usuario.backend.repository.AulaVirtualRepository;
import com.usuario.backend.repository.AulaEstudianteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AulaVirtualService {

    private static final Logger logger = LoggerFactory.getLogger(AulaVirtualService.class);

    @Autowired
    private AulaVirtualRepository aulaVirtualRepository;
    
    @Autowired
    private AulaEstudianteRepository aulaEstudianteRepository;

    /**
     * 🔥 MÉTODO PRINCIPAL: Obtiene aulas según el rol del usuario
     */
    public List<AulaVirtual> getAulasByUsuario(Long usuarioId, String rol) {
        try {
            logger.info("Obteniendo aulas para usuario {} con rol {}", usuarioId, rol);
            if ("PROFESOR".equalsIgnoreCase(rol)) {
                // Profesores ven todas sus aulas (activas, inactivas, finalizadas)
                List<AulaVirtual> aulas = aulaVirtualRepository.findByProfesorIdOrderByFechaInicioDesc(usuarioId);
                logger.info("Profesor {} tiene {} aulas", usuarioId, aulas.size());
                return aulas;
            } else if ("ESTUDIANTE".equalsIgnoreCase(rol)) {
                // Estudiantes ven todas las aulas donde están inscritos (sin filtrar por estado de aula)
                List<Long> aulaIds = aulaEstudianteRepository.findAulaIdsByEstudianteId(usuarioId);
                if (aulaIds.isEmpty()) {
                    logger.info("Estudiante {} no está inscrito en ningún aula", usuarioId);
                    return List.of();
                }
                List<AulaVirtual> aulas = aulaVirtualRepository.findAllById(aulaIds);
                logger.info("Estudiante {} está inscrito en {} aulas", usuarioId, aulas.size());
                return aulas;
            } else {
                logger.warn("Rol no reconocido: {}", rol);
                return List.of();
            }
        } catch (Exception e) {
            logger.error("Error al obtener aulas para usuario {}: {}", usuarioId, e.getMessage(), e);
            throw new RuntimeException("Error al obtener las aulas del usuario", e);
        }
    }

    /**
     * 🔥 OBTENER AULA POR ID
     */
    public AulaVirtual findById(Long id) {
        try {
            return aulaVirtualRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Error al buscar aula por ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    /**
     * 🔥 AGREGAR ESTUDIANTE A AULA
     */
    public void agregarEstudianteAAula(Long aulaId, Long estudianteId) {
        try {
            // Verificar que el aula existe
            AulaVirtual aula = findById(aulaId);
            if (aula == null) {
                throw new IllegalArgumentException("El aula con ID " + aulaId + " no existe");
            }

            // Verificar si ya está inscrito como activo
            boolean yaInscrito = aulaEstudianteRepository.existsByAulaIdAndEstudianteIdAndEstado(
                aulaId, estudianteId, AulaEstudiante.EstadoEstudiante.activo);
            if (yaInscrito) {
                throw new IllegalArgumentException("El estudiante ya está inscrito en esta aula");
            }

            // Buscar si existe registro previo como invitado o inactivo
            List<AulaEstudiante> registros = aulaEstudianteRepository.findByAulaIdAndEstado(aulaId, AulaEstudiante.EstadoEstudiante.invitado);
            registros.addAll(aulaEstudianteRepository.findByAulaIdAndEstado(aulaId, AulaEstudiante.EstadoEstudiante.inactivo));
            AulaEstudiante existente = registros.stream()
                .filter(ae -> ae.getEstudianteId().equals(estudianteId))
                .findFirst().orElse(null);

            if (existente != null) {
                existente.setEstado(AulaEstudiante.EstadoEstudiante.activo);
                existente.setFechaUnion(java.time.LocalDateTime.now());
                existente.setFechaSalida(null);
                aulaEstudianteRepository.save(existente);
                logger.info("Estudiante {} reactivado en el aula {}", estudianteId, aulaId);
            } else {
                // Crear nueva inscripción
                AulaEstudiante aulaEstudiante = new AulaEstudiante(aulaId, estudianteId);
                aulaEstudianteRepository.save(aulaEstudiante);
                logger.info("Estudiante {} agregado al aula {} exitosamente", estudianteId, aulaId);
            }
        } catch (Exception e) {
            logger.error("Error al agregar estudiante {} al aula {}: {}", estudianteId, aulaId, e.getMessage());
            throw e;
        }
    }

    /**
     * 🔥 OBTENER ESTUDIANTES DE UN AULA
     */
    public List<AulaEstudiante> getEstudiantesDeAula(Long aulaId) {
        try {
            return aulaEstudianteRepository.findByAulaIdAndEstado(aulaId, AulaEstudiante.EstadoEstudiante.activo);
        } catch (Exception e) {
            logger.error("Error al obtener estudiantes del aula {}: {}", aulaId, e.getMessage());
            return List.of();
        }
    }

    /**
     * 🔥 CONTAR ESTUDIANTES EN AULA
     */
    public long contarEstudiantesEnAula(Long aulaId) {
        try {
            return aulaEstudianteRepository.countByAulaIdAndEstado(aulaId, AulaEstudiante.EstadoEstudiante.activo);
        } catch (Exception e) {
            logger.error("Error al contar estudiantes en aula {}: {}", aulaId, e.getMessage());
            return 0;
        }
    }

    /**
     * 🔥 VERIFICAR SI USUARIO PUEDE ACCEDER AL AULA
     */
    public boolean puedeAccederAAula(Long usuarioId, String rol, Long aulaId) {
        try {
            if ("PROFESOR".equalsIgnoreCase(rol)) {
                // Verificar si es el profesor del aula
                AulaVirtual aula = findById(aulaId);
                return aula != null && usuarioId.equals(aula.getProfesorId());
                
            } else if ("ESTUDIANTE".equalsIgnoreCase(rol)) {
                // Verificar si está inscrito en el aula
                return aulaEstudianteRepository.existsByAulaIdAndEstudianteIdAndEstado(
                    aulaId, usuarioId, AulaEstudiante.EstadoEstudiante.activo);
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error al verificar acceso del usuario {} al aula {}: {}", usuarioId, aulaId, e.getMessage());
            return false;
        }
    }

    /**
     * 🔥 BUSCAR AULAS POR NOMBRE (para el usuario específico)
     */
    public List<AulaVirtual> buscarAulasPorNombre(Long usuarioId, String rol, String nombre) {
        try {
            if ("PROFESOR".equalsIgnoreCase(rol)) {
                return aulaVirtualRepository.findByNombreContainingIgnoreCaseAndProfesorId(nombre, usuarioId);
            } else {
                // Para estudiantes, buscar entre sus aulas
                List<AulaVirtual> aulasDelEstudiante = getAulasByUsuario(usuarioId, rol);
                return aulasDelEstudiante.stream()
                    .filter(aula -> aula.getNombre().toLowerCase().contains(nombre.toLowerCase()) ||
                                   (aula.getTitulo() != null && aula.getTitulo().toLowerCase().contains(nombre.toLowerCase())))
                    .toList();
            }
        } catch (Exception e) {
            logger.error("Error al buscar aulas por nombre para usuario {}: {}", usuarioId, e.getMessage());
            return List.of();
        }
    }

    /**
     * Elimina (desactiva) un participante activo de un aula. Devuelve true si se desactivó, false si no existía.
     */
    public boolean eliminarParticipanteAula(Long aulaId, Long estudianteId) {
        try {
            List<AulaEstudiante> lista = aulaEstudianteRepository.findByAulaIdAndEstado(aulaId, AulaEstudiante.EstadoEstudiante.activo);
            AulaEstudiante participante = lista.stream()
                .filter(ae -> ae.getEstudianteId().equals(estudianteId))
                .findFirst().orElse(null);
            if (participante == null) {
                logger.warn("No se encontró participante activo con id {} en aula {}", estudianteId, aulaId);
                return false;
            }
            participante.setEstado(AulaEstudiante.EstadoEstudiante.inactivo);
            participante.setFechaSalida(java.time.LocalDateTime.now());
            aulaEstudianteRepository.save(participante);
            logger.info("Participante {} desactivado en aula {}", estudianteId, aulaId);
            return true;
        } catch (Exception e) {
            logger.error("Error al eliminar participante {} del aula {}: {}", estudianteId, aulaId, e.getMessage());
            throw e;
        }
    }
}