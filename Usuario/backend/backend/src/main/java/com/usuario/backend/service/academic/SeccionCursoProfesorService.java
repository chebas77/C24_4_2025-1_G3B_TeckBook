package com.usuario.backend.service.academic;
import com.usuario.backend.model.entity.academic.SeccionCursoProfesor;
import com.usuario.backend.repository.academic.SeccionCursoProfesorRepository;
import com.usuario.backend.repository.academic.SeccionRepository;
import com.usuario.backend.repository.academic.CursoRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeccionCursoProfesorService {

    @Autowired
    private SeccionCursoProfesorRepository seccionCursoProfesorRepository;
    
    @Autowired
    private SeccionRepository seccionRepository;
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // CRUD básico
    public List<SeccionCursoProfesor> findAll() {
        return seccionCursoProfesorRepository.findByActivoTrue();
    }

    public Optional<SeccionCursoProfesor> findById(Long id) {
        return seccionCursoProfesorRepository.findById(id);
    }

    public SeccionCursoProfesor save(SeccionCursoProfesor seccionCursoProfesor) {
        return seccionCursoProfesorRepository.save(seccionCursoProfesor);
    }

    public void deleteById(Long id) {
        seccionCursoProfesorRepository.findById(id).ifPresent(seccionCursoProfesor -> {
            seccionCursoProfesor.setActivo(false);
            seccionCursoProfesorRepository.save(seccionCursoProfesor);
        });
    }

    // Métodos de búsqueda específicos
    public List<SeccionCursoProfesor> findClasesByProfesor(Long profesorId) {
        return seccionCursoProfesorRepository.findByProfesorIdAndActivoTrue(profesorId);
    }

    public List<SeccionCursoProfesor> findCursosBySeccion(Long seccionId) {
        return seccionCursoProfesorRepository.findBySeccionIdAndActivoTrue(seccionId);
    }

    public List<SeccionCursoProfesor> findProfesoresByCurso(Long cursoId) {
        return seccionCursoProfesorRepository.findByCursoIdAndActivoTrue(cursoId);
    }

    public Optional<SeccionCursoProfesor> findAsignacionEspecifica(Long seccionId, Long cursoId, Long profesorId) {
        return seccionCursoProfesorRepository.findBySeccionIdAndCursoIdAndProfesorIdAndActivoTrue(seccionId, cursoId, profesorId);
    }

    public List<SeccionCursoProfesor> findHorariosByProfesor(Long profesorId) {
        return seccionCursoProfesorRepository.findHorariosByProfesor(profesorId);
    }

    public List<SeccionCursoProfesor> findClasesByAula(String aula) {
        return seccionCursoProfesorRepository.findByAulaAndActivoTrue(aula);
    }

    // Validaciones de negocio
    public boolean existsSeccion(Long seccionId) {
        return seccionRepository.findById(seccionId).isPresent();
    }

    public boolean existsCurso(Long cursoId) {
        return cursoRepository.findById(cursoId).isPresent();
    }

    public boolean existsProfesor(Long profesorId) {
        return usuarioRepository.findById(profesorId).isPresent();
    }

    public boolean existsAsignacion(Long seccionId, Long cursoId, Long profesorId) {
        return findAsignacionEspecifica(seccionId, cursoId, profesorId).isPresent();
    }

    public boolean hasConflictoHorario(String horario, Long profesorId, Long aulaId) {
        // Verificar conflicto de profesor
        List<SeccionCursoProfesor> clasesProfesor = findHorariosByProfesor(profesorId);
        for (SeccionCursoProfesor clase : clasesProfesor) {
            if (clase.getHorario() != null && clase.getHorario().equals(horario)) {
                return true; // Conflicto de horario para el profesor
            }
        }
        
        // Verificar conflicto de aula (si se proporciona)
        if (aulaId != null) {
            List<SeccionCursoProfesor> clasesAula = findClasesByAula(aulaId.toString());
            for (SeccionCursoProfesor clase : clasesAula) {
                if (clase.getHorario() != null && clase.getHorario().equals(horario)) {
                    return true; // Conflicto de aula
                }
            }
        }
        
        return false;
    }

    public SeccionCursoProfesor crearAsignacion(SeccionCursoProfesor seccionCursoProfesor) {
        // Validar entidades
        if (!existsSeccion(seccionCursoProfesor.getSeccionId())) {
            throw new IllegalArgumentException("La sección especificada no existe");
        }
        if (!existsCurso(seccionCursoProfesor.getCursoId())) {
            throw new IllegalArgumentException("El curso especificado no existe");
        }
        if (!existsProfesor(seccionCursoProfesor.getProfesorId())) {
            throw new IllegalArgumentException("El profesor especificado no existe");
        }
        
        // Validar que no existe la asignación
        if (existsAsignacion(seccionCursoProfesor.getSeccionId(), 
                           seccionCursoProfesor.getCursoId(), 
                           seccionCursoProfesor.getProfesorId())) {
            throw new IllegalArgumentException("La asignación ya existe");
        }
        
        // Validar conflictos de horario
        if (seccionCursoProfesor.getHorario() != null) {
            Long aulaId = null;
            if (seccionCursoProfesor.getAula() != null) {
                try {
                    aulaId = Long.parseLong(seccionCursoProfesor.getAula());
                } catch (NumberFormatException e) {
                    // Aula no es numérica, usar nombre como string
                }
            }
            
            if (hasConflictoHorario(seccionCursoProfesor.getHorario(), 
                                  seccionCursoProfesor.getProfesorId(), aulaId)) {
                throw new IllegalArgumentException("Conflicto de horario detectado");
            }
        }
        
        return save(seccionCursoProfesor);
    }

    public SeccionCursoProfesor updateAsignacion(Long id, SeccionCursoProfesor asignacionActualizada) {
        return seccionCursoProfesorRepository.findById(id).map(asignacion -> {
            // Validar conflictos de horario si se actualiza
            if (asignacionActualizada.getHorario() != null && 
                !asignacionActualizada.getHorario().equals(asignacion.getHorario())) {
                
                Long aulaId = null;
                if (asignacionActualizada.getAula() != null) {
                    try {
                        aulaId = Long.parseLong(asignacionActualizada.getAula());
                    } catch (NumberFormatException e) {
                        // Aula no es numérica
                    }
                }
                
                if (hasConflictoHorario(asignacionActualizada.getHorario(), 
                                      asignacion.getProfesorId(), aulaId)) {
                    throw new IllegalArgumentException("Conflicto de horario detectado");
                }
            }
            
            asignacion.setHorario(asignacionActualizada.getHorario());
            asignacion.setAula(asignacionActualizada.getAula());
            
            return seccionCursoProfesorRepository.save(asignacion);
        }).orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada"));
    }

    public void removerAsignacion(Long seccionId, Long cursoId, Long profesorId) {
        findAsignacionEspecifica(seccionId, cursoId, profesorId).ifPresent(asignacion -> {
            asignacion.setActivo(false);
            seccionCursoProfesorRepository.save(asignacion);
        });
    }
}