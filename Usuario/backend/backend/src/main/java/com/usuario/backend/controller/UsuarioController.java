package com.usuario.backend.controller;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Usuario usuario = usuarioService.findByCorreoInstitucional(username);
        
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
      
        }
    }
    @PostMapping("/register")
    public Usuario register(@RequestBody Usuario usuario) {
        System.out.println("Registro recibido: correo_institucional=" + usuario.getCorreoInstitucional() + ", password=" + usuario.getPassword());
        if (usuario.getCorreoInstitucional() == null || usuario.getPassword() == null) {
            throw new IllegalArgumentException("Correo institucional y password son requeridos");
        }
        return usuarioService.registrarUsuario(usuario);
    }

    @PostMapping("/login")
    public boolean login(@RequestBody Usuario usuario) {
        System.out.println("Login recibido: correo_institucional=" + usuario.getCorreoInstitucional() + ", password=" + usuario.getPassword());
        if (usuario.getCorreoInstitucional() == null || usuario.getPassword() == null) {
            return false;
        }
        return usuarioService.autenticarUsuario(usuario.getCorreoInstitucional(), usuario.getPassword());
    }
    

}