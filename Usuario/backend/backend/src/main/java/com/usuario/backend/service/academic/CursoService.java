package com.usuario.backend.service.academic;


import com.usuario.backend.model.entity.academic.Curso;
import com.usuario.backend.repository.academic.CursoRepository;
import com.usuario.backend.repository.core.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private DepartamentoRepository departamentoRepository;

    // CRUD básico
    public List<Curso> findAll() {
        return cursoRepository.findByActivoTrue();
    }

    public Optional<Curso> findById(Long id) {
        return cursoRepository.findById(id);
    }

    public Curso save(Curso curso) {
        return cursoRepository.save(curso);
    }

    public void deleteById(Long id) {
        cursoRepository.findById(id).ifPresent(curso -> {
            curso.setActivo(false);
            cursoRepository.save(curso);
        });
    }

    // Métodos de búsqueda específicos
    public List<Curso> findByDepartamento(Long departamentoId) {
        return cursoRepository.findByDepartamentoIdAndActivoTrue(departamentoId);
    }

    public List<Curso> findByCiclo(Integer ciclo) {
        return cursoRepository.findByCicloAndActivoTrue(ciclo);
    }

    public Optional<Curso> findByCodigo(String codigo) {
        return cursoRepository.findByCodigo(codigo);
    }

    public List<Curso> searchByNombre(String nombre) {
        return cursoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    // Validaciones de negocio
    public boolean existsByCodigo(String codigo) {
        return cursoRepository.findByCodigo(codigo).isPresent();
    }

    public boolean existsDepartamento(Long departamentoId) {
        return departamentoRepository.findById(departamentoId).isPresent();
    }

    public Curso createCurso(Curso curso) {
        // Validar departamento
        if (!existsDepartamento(curso.getDepartamentoId())) {
            throw new IllegalArgumentException("El departamento especificado no existe");
        }
        
        // Validar código único
        if (existsByCodigo(curso.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un curso con este código");
        }
        
        // Validar ciclo
        if (curso.getCiclo() < 1 || curso.getCiclo() > 6) {
            throw new IllegalArgumentException("El ciclo debe estar entre 1 y 6");
        }
        
        return save(curso);
    }

    public Curso updateCurso(Long id, Curso cursoActualizado) {
        return cursoRepository.findById(id).map(curso -> {
            // Validar departamento
            if (!existsDepartamento(cursoActualizado.getDepartamentoId())) {
                throw new IllegalArgumentException("El departamento especificado no existe");
            }
            
            // Validar código único (excepto el actual)
            if (!cursoActualizado.getCodigo().equals(curso.getCodigo()) 
                && existsByCodigo(cursoActualizado.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un curso con este código");
            }
            
            // Validar ciclo
            if (cursoActualizado.getCiclo() < 1 || cursoActualizado.getCiclo() > 6) {
                throw new IllegalArgumentException("El ciclo debe estar entre 1 y 6");
            }
            
            curso.setNombre(cursoActualizado.getNombre());
            curso.setCodigo(cursoActualizado.getCodigo());
            curso.setDescripcion(cursoActualizado.getDescripcion());
            curso.setCiclo(cursoActualizado.getCiclo());
            curso.setCreditos(cursoActualizado.getCreditos());
            curso.setHorasTeorias(cursoActualizado.getHorasTeorias());
            curso.setHorasPracticas(cursoActualizado.getHorasPracticas());
            curso.setDepartamentoId(cursoActualizado.getDepartamentoId());
            
            return cursoRepository.save(curso);
        }).orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
    }
}