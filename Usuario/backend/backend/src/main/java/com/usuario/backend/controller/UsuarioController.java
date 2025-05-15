package com.usuario.backend.controller;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public Usuario register(@RequestBody Usuario usuario) {
        System.out.println("Registro recibido: email=" + usuario.getEmail() + ", password=" + usuario.getPassword());
        if (usuario.getEmail() == null || usuario.getPassword() == null) {
            throw new IllegalArgumentException("Email y password son requeridos");
        }
        return usuarioService.registrarUsuario(usuario);
    }

    @PostMapping("/login")
    public boolean login(@RequestBody Usuario usuario) {
        System.out.println("Login recibido: email=" + usuario.getEmail() + ", password=" + usuario.getPassword());
        if (usuario.getEmail() == null || usuario.getPassword() == null) {
            return false;
        }
        return usuarioService.autenticarUsuario(usuario.getEmail(), usuario.getPassword());
    }
}
