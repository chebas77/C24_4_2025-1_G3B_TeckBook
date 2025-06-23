package com.usuario.backend.repository.academic;

import com.usuario.backend.model.entity.academic.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    
    // Buscar cursos activos
    List<Curso> findByActivoTrue();
    
    // Buscar por departamento
    List<Curso> findByDepartamentoIdAndActivoTrue(Long departamentoId);
    
    // Buscar por ciclo
    List<Curso> findByCicloAndActivoTrue(Integer ciclo);
    
    // Buscar por código único
    Optional<Curso> findByCodigo(String codigo);
    
    // Buscar por nombre que contenga
    List<Curso> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
