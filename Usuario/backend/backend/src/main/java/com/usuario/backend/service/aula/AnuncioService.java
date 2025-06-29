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

    public List<Anuncio> getAnunciosDeAula(Long usuarioId, String rol, Long aulaId) {
        // Verifica acceso antes de mostrar anuncios
        if (!aulaVirtualService.puedeAccederAAula(usuarioId, rol, aulaId)) {
            throw new SecurityException("No tiene permiso para ver los anuncios de este aula");
        }
        return anuncioRepository.findByAulaIdAndActivoTrueOrderByFechaPublicacionDesc(aulaId);
    }
}
