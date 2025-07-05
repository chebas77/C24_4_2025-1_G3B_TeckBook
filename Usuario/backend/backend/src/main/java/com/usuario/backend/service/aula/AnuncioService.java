package com.usuario.backend.service.aula;

import com.usuario.backend.model.entity.Anuncio;
import com.usuario.backend.repository.AnuncioRepository;
import com.usuario.backend.service.aula.AulaVirtualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnuncioService {
    @Autowired
    private AnuncioRepository anuncioRepository;
    @Autowired
    private AulaVirtualService aulaVirtualService;

    public List<Anuncio> getAnunciosDeAula(Long usuarioId, String rol, Integer aulaId) {
        System.out.println("[AnuncioService] usuarioId=" + usuarioId + ", rol=" + rol + ", aulaId=" + aulaId);
        boolean acceso = aulaVirtualService.puedeAccederAAula(usuarioId, rol, aulaId);
        System.out.println("[AnuncioService] Acceso a aula: " + acceso);
        if (!acceso) {
            System.out.println("[AnuncioService] Acceso denegado para usuario " + usuarioId + " en aula " + aulaId);
            throw new SecurityException("No tiene permiso para ver los anuncios de este aula");
        }
        List<Anuncio> anuncios = anuncioRepository.findByAulaIdAndActivoTrueOrderByFechaPublicacionDesc(aulaId);
        System.out.println("[AnuncioService] Anuncios retornados: " + (anuncios != null ? anuncios.size() : 0));
        return anuncios;
    }

    public Anuncio crearAnuncio(Long usuarioId, String rol, Integer aulaId, Anuncio anuncio) {
        // Verifica acceso antes de crear anuncio
        if (!aulaVirtualService.puedeAccederAAula(usuarioId, rol, aulaId)) {
            throw new SecurityException("No tiene permiso para crear anuncios en este aula");
        }
        anuncio.setAulaId(aulaId);
        anuncio.setAutorId(usuarioId.intValue());
        anuncio.setFechaPublicacion(java.time.LocalDateTime.now());
        anuncio.setActivo(true);
        // tipo ya es String, no requiere conversión
        return anuncioRepository.save(anuncio);
    }

    public List<Anuncio> getAnunciosGenerales() {
        return anuncioRepository.findByEsGeneralTrueAndActivoTrueOrderByFechaPublicacionDesc();
    }

    public Anuncio crearAnuncioGeneral(Long usuarioId, String rol, Anuncio anuncio) {
        anuncio.setAulaId(null);
        anuncio.setAutorId(usuarioId.intValue());
        anuncio.setFechaPublicacion(java.time.LocalDateTime.now());
        anuncio.setActivo(true);
        anuncio.setEsGeneral(true);
        return anuncioRepository.save(anuncio);
    }

    public List<Anuncio> getTodosLosAnuncios() {
        return anuncioRepository.findAll();
    }
}
