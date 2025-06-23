package com.usuario.backend.service.comunicacion;


import com.usuario.backend.model.entity.comunicacion.Anuncio;
import com.usuario.backend.repository.comunicacion.AnuncioRepository;
import com.usuario.backend.repository.core.UsuarioRepository;
import com.usuario.backend.repository.academic.SeccionRepository;
import com.usuario.backend.repository.academic.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnuncioService {

    @Autowired
    private AnuncioRepository anuncioRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SeccionRepository seccionRepository;
    
    @Autowired
    private CursoRepository cursoRepository;

    // CRUD básico
    public List<Anuncio> findAll() {
        return anuncioRepository.findByActivoTrue();
    }

    public Optional<Anuncio> findById(Long id) {
        return anuncioRepository.findById(id);
    }

    public Anuncio save(Anuncio anuncio) {
        return anuncioRepository.save(anuncio);
    }

    public void deleteById(Long id) {
        anuncioRepository.findById(id).ifPresent(anuncio -> {
            anuncio.setActivo(false);
            anuncioRepository.save(anuncio);
        });
    }

    // Métodos de búsqueda específicos
    public List<Anuncio> findByTipo(Anuncio.TipoAnuncio tipo) {
        return anuncioRepository.findByTipoAndActivoTrue(tipo);
    }

    public List<Anuncio> findByPrioridad(Anuncio.Prioridad prioridad) {
        return anuncioRepository.findByPrioridadAndActivoTrue(prioridad);
    }

    public List<Anuncio> findByUsuario(Long usuarioId) {
        return anuncioRepository.findByUsuarioIdAndActivoTrueOrderByFechaPublicacionDesc(usuarioId);
    }

    public List<Anuncio> findBySeccion(Long seccionId) {
        return anuncioRepository.findBySeccionIdAndActivoTrueOrderByFechaPublicacionDesc(seccionId);
    }

    public List<Anuncio> findByCurso(Long cursoId) {
        return anuncioRepository.findByCursoIdAndActivoTrueOrderByFechaPublicacionDesc(cursoId);
    }

    public List<Anuncio> findAnunciosGenerales() {
        return anuncioRepository.findByTipoAndActivoTrueOrderByFechaPublicacionDesc(Anuncio.TipoAnuncio.GENERAL);
    }

    public List<Anuncio> findAnunciosUrgentes() {
        return anuncioRepository.findAnunciosUrgentes(LocalDateTime.now());
    }

    public List<Anuncio> findAnunciosVigentes() {
        return anuncioRepository.findAnunciosVigentes(LocalDateTime.now());
    }

    // Validaciones de negocio
    public boolean existsUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId).isPresent();
    }

    public boolean existsSeccion(Long seccionId) {
        return seccionId == null || seccionRepository.findById(seccionId).isPresent();
    }

    public boolean existsCurso(Long cursoId) {
        return cursoId == null || cursoRepository.findById(cursoId).isPresent();
    }

    public boolean isVigente(Anuncio anuncio) {
        return anuncio.getFechaVencimiento() == null || 
               anuncio.getFechaVencimiento().isAfter(LocalDateTime.now());
    }

    public Anuncio createAnuncio(Anuncio anuncio) {
        // Validar usuario (obligatorio)
        if (!existsUsuario(anuncio.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario especificado no existe");
        }
        
        // Validar sección (si se especifica)
        if (!existsSeccion(anuncio.getSeccionId())) {
            throw new IllegalArgumentException("La sección especificada no existe");
        }
        
        // Validar curso (si se especifica)
        if (!existsCurso(anuncio.getCursoId())) {
            throw new IllegalArgumentException("El curso especificado no existe");
        }
        
        // Validar tipo vs contexto
        if (anuncio.getTipo() == Anuncio.TipoAnuncio.SECCION && anuncio.getSeccionId() == null) {
            throw new IllegalArgumentException("Los anuncios de sección requieren especificar una sección");
        }
        
        if (anuncio.getTipo() == Anuncio.TipoAnuncio.CURSO && anuncio.getCursoId() == null) {
            throw new IllegalArgumentException("Los anuncios de curso requieren especificar un curso");
        }
        
        // Validar fecha de vencimiento
        if (anuncio.getFechaVencimiento() != null && 
            anuncio.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser en el pasado");
        }
        
        // Establecer fecha de publicación si no está definida
        if (anuncio.getFechaPublicacion() == null) {
            anuncio.setFechaPublicacion(LocalDateTime.now());
        }
        
        return save(anuncio);
    }

    public Anuncio updateAnuncio(Long id, Anuncio anuncioActualizado) {
        return anuncioRepository.findById(id).map(anuncio -> {
            // Solo permitir actualizar contenido, prioridad y fecha de vencimiento
            anuncio.setTitulo(anuncioActualizado.getTitulo());
            anuncio.setContenido(anuncioActualizado.getContenido());
            anuncio.setPrioridad(anuncioActualizado.getPrioridad());
            anuncio.setFechaVencimiento(anuncioActualizado.getFechaVencimiento());
            
            // Validar fecha de vencimiento
            if (anuncio.getFechaVencimiento() != null && 
                anuncio.getFechaVencimiento().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("La fecha de vencimiento no puede ser en el pasado");
            }
            
            return anuncioRepository.save(anuncio);
        }).orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado"));
    }

    // Métodos de utilidad
    public List<Anuncio> findAnunciosParaEstudiante(Long estudianteId, Long seccionId, List<Long> cursosIds) {
        List<Anuncio> anuncios = findAnunciosGenerales(); // Anuncios generales
        
        // Agregar anuncios de la sección del estudiante
        if (seccionId != null) {
            anuncios.addAll(findBySeccion(seccionId));
        }
        
        // Agregar anuncios de los cursos del estudiante
        if (cursosIds != null) {
            for (Long cursoId : cursosIds) {
                anuncios.addAll(findByCurso(cursoId));
            }
        }
        
        // Filtrar solo vigentes y ordenar por fecha
        return anuncios.stream()
                .filter(this::isVigente)
                .sorted((a1, a2) -> a2.getFechaPublicacion().compareTo(a1.getFechaPublicacion()))
                .distinct()
                .toList();
    }
}