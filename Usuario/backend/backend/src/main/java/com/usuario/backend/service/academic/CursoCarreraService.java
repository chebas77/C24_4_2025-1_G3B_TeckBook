package com.usuario.backend.service.academic;



import com.usuario.backend.model.entity.academic.CursoCarrera;
import com.usuario.backend.repository.academic.CursoCarreraRepository;
import com.usuario.backend.repository.academic.CursoRepository;
import com.usuario.backend.repository.core.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CursoCarreraService {

    @Autowired
    private CursoCarreraRepository cursoCarreraRepository;
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private CarreraRepository carreraRepository;

    // CRUD básico
    public List<CursoCarrera> findAll() {
        return cursoCarreraRepository.findByActivoTrue();
    }

    public Optional<CursoCarrera> findById(Long id) {
        return cursoCarreraRepository.findById(id);
    }

    public CursoCarrera save(CursoCarrera cursoCarrera) {
        return cursoCarreraRepository.save(cursoCarrera);
    }

    public void deleteById(Long id) {
        cursoCarreraRepository.findById(id).ifPresent(cursoCarrera -> {
            cursoCarrera.setActivo(false);
            cursoCarreraRepository.save(cursoCarrera);
        });
    }

    // Métodos de búsqueda específicos
    public List<CursoCarrera> findCursosByCarrera(Long carreraId) {
        return cursoCarreraRepository.findByCarreraIdAndActivoTrue(carreraId);
    }

    public List<CursoCarrera> findCarrerasByCurso(Long cursoId) {
        return cursoCarreraRepository.findByCursoIdAndActivoTrue(cursoId);
    }

    public List<CursoCarrera> findCursosObligatorios(Long carreraId) {
        return cursoCarreraRepository.findByCarreraIdAndEsObligatorioTrueAndActivoTrue(carreraId);
    }

    public List<CursoCarrera> findCursosByCiclo(Long carreraId, Integer ciclo) {
        return cursoCarreraRepository.findByCarreraIdAndCicloSugeridoAndActivoTrue(carreraId, ciclo);
    }

    public Optional<CursoCarrera> findRelacion(Long cursoId, Long carreraId) {
        return Optional.ofNullable(cursoCarreraRepository.findByCursoIdAndCarreraIdAndActivoTrue(cursoId, carreraId));
    }

    // Validaciones de negocio
    public boolean existsCurso(Long cursoId) {
        return cursoRepository.findById(cursoId).isPresent();
    }

    public boolean existsCarrera(Long carreraId) {
        return carreraRepository.findById(carreraId).isPresent();
    }

    public boolean existsRelacion(Long cursoId, Long carreraId) {
        return findRelacion(cursoId, carreraId).isPresent();
    }

    public CursoCarrera createRelacion(CursoCarrera cursoCarrera) {
        // Validar curso y carrera
        if (!existsCurso(cursoCarrera.getCursoId())) {
            throw new IllegalArgumentException("El curso especificado no existe");
        }
        if (!existsCarrera(cursoCarrera.getCarreraId())) {
            throw new IllegalArgumentException("La carrera especificada no existe");
        }
        
        // Validar que no existe la relación
        if (existsRelacion(cursoCarrera.getCursoId(), cursoCarrera.getCarreraId())) {
            throw new IllegalArgumentException("La relación curso-carrera ya existe");
        }
        
        // Validar ciclo sugerido
        if (cursoCarrera.getCicloSugerido() != null && 
            (cursoCarrera.getCicloSugerido() < 1 || cursoCarrera.getCicloSugerido() > 6)) {
            throw new IllegalArgumentException("El ciclo sugerido debe estar entre 1 y 6");
        }
        
        return save(cursoCarrera);
    }

    public void removeRelacion(Long cursoId, Long carreraId) {
        findRelacion(cursoId, carreraId).ifPresent(relacion -> {
            relacion.setActivo(false);
            cursoCarreraRepository.save(relacion);
        });
    }
}