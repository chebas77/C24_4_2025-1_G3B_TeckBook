package com.usuario.backend.controller.aula;

import com.usuario.backend.model.entity.Anuncio;
import com.usuario.backend.service.aula.AnuncioService;
import com.usuario.backend.service.user.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/aulas/{aulaId}/anuncios")
public class AnuncioController {
    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private UsuarioService usuarioService;

    // GET: anuncios de un aula (solo para usuarios autorizados)
    @GetMapping
    public List<Anuncio> getAnunciosDeAula(@PathVariable Long aulaId, Principal principal) {
        String email = principal.getName();
        var usuario = usuarioService.findByCorreoInstitucional(email);
        Long usuarioId = usuario.getId();
        String rol = usuario.getRol().toString(); // El rol real del usuario autenticado
        return anuncioService.getAnunciosDeAula(usuarioId, rol, aulaId);
    }

    // POST: crear un nuevo anuncio en un aula (solo para usuarios autorizados)
    @PostMapping
    public Anuncio crearAnuncio(
        @PathVariable Long aulaId,
        @RequestBody Anuncio anuncio,
        Principal principal
    ) {
        String email = principal.getName();
        var usuario = usuarioService.findByCorreoInstitucional(email);
        Long usuarioId = usuario.getId();
        String rol = usuario.getRol().toString();
        return anuncioService.crearAnuncio(usuarioId, rol, aulaId, anuncio);
    }
}
