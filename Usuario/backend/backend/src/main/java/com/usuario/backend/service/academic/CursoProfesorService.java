package com.usuario.backend.service.academic;

import com.usuario.backend.model.entity.academic.CursoProfesor;
import com.usuario.backend.repository.academic.CursoProfesorRepository;
import com.usuario.backend.repository.academic.CursoRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CursoProfesorService {

    @Autowired
    private CursoProfesorRepository cursoProfesorRepository;
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // CRUD básico
    public List<CursoProfesor> findAll() {
        return cursoProfesorRepository.findByActivoTrue();
    }

    public Optional<CursoProfesor> findById(Long id) {
        return cursoProfesorRepository.findById(id);
    }

    public CursoProfesor save(CursoProfesor cursoProfesor) {
        return cursoProfesorRepository.save(cursoProfesor);
    }

    public void deleteById(Long id) {
        cursoProfesorRepository.findById(id).ifPresent(cursoProfesor -> {
            cursoProfesor.setActivo(false);
            cursoProfesorRepository.save(cursoProfesor);
        });
    }

    // Métodos de búsqueda específicos
    public List<CursoProfesor> findCursosByProfesor(Long profesorId) {
        return cursoProfesorRepository.findByProfesorIdAndActivoTrue(profesorId);
    }

    public List<CursoProfesor> findProfesoresByCurso(Long cursoId) {
        return cursoProfesorRepository.findByCursoIdAndActivoTrue(cursoId);
    }

    public Optional<CursoProfesor> findAsignacion(Long profesorId, Long cursoId) {
        return cursoProfesorRepository.findByProfesorIdAndCursoIdAndActivoTrue(profesorId, cursoId);
    }

    // Validaciones de negocio
    public boolean existsProfesor(Long profesorId) {
        return usuarioRepository.findById(profesorId).isPresent();
    }

    public boolean existsCurso(Long cursoId) {
        return cursoRepository.findById(cursoId).isPresent();
    }

    public boolean existsAsignacion(Long profesorId, Long cursoId) {
        return findAsignacion(profesorId, cursoId).isPresent();
    }

    public CursoProfesor asignarCurso(CursoProfesor cursoProfesor) {
        // Validar profesor y curso
        if (!existsProfesor(cursoProfesor.getProfesorId())) {
            throw new IllegalArgumentException("El profesor especificado no existe");
        }
        if (!existsCurso(cursoProfesor.getCursoId())) {
            throw new IllegalArgumentException("El curso especificado no existe");
        }
        
        // Validar que no esté ya asignado
        if (existsAsignacion(cursoProfesor.getProfesorId(), cursoProfesor.getCursoId())) {
            throw new IllegalArgumentException("El profesor ya está asignado a este curso");
        }
        
        return save(cursoProfesor);
    }

    public void removerAsignacion(Long profesorId, Long cursoId) {
        findAsignacion(profesorId, cursoId).ifPresent(asignacion -> {
            asignacion.setActivo(false);
            cursoProfesorRepository.save(asignacion);
        });
    }
}
