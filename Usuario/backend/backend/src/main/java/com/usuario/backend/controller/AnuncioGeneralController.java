package com.usuario.backend.controller;

import com.usuario.backend.model.entity.Anuncio;
import com.usuario.backend.service.aula.AnuncioService;
import com.usuario.backend.service.user.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/anuncios/general")
public class AnuncioGeneralController {
    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private UsuarioService usuarioService;

    // GET: anuncios generales (es_general = true)
    @GetMapping
    public List<Anuncio> getAnunciosGenerales() {
        return anuncioService.getAnunciosGenerales();
    }

    // GET: todos los anuncios (generales y de aula)
    @GetMapping("/todos")
    public List<Anuncio> getTodosLosAnuncios() {
        return anuncioService.getTodosLosAnuncios();
    }

    // POST: crear un nuevo anuncio general (sin aulaId, es_general = true)
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Anuncio> crearAnuncioGeneral(
        @RequestPart("titulo") String titulo,
        @RequestPart("contenido") String contenido,
        @RequestPart("tipo") String tipo,
        @RequestPart(value = "archivo", required = false) MultipartFile archivo,
        Principal principal
    ) {
        String email = principal.getName();
        var usuario = usuarioService.findByCorreoInstitucional(email);
        Long usuarioId = usuario.getId();
        String rol = usuario.getRol().toString();
        Anuncio anuncio = new Anuncio();
        anuncio.setTitulo(titulo);
        anuncio.setContenido(contenido);
        anuncio.setTipo(tipo);
        anuncio.setEsGeneral(true);
        anuncio.setAulaId(null);
        if (archivo != null && !archivo.isEmpty()) {
            anuncio.setArchivoNombre(archivo.getOriginalFilename());
            anuncio.setArchivoTipo(archivo.getContentType());
            anuncio.setArchivoTamaño(archivo.getSize());
        }
        anuncio.setAutorId(usuarioId != null ? usuarioId.intValue() : null);
        Anuncio creado = anuncioService.crearAnuncioGeneral(usuarioId, rol, anuncio);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }
}
