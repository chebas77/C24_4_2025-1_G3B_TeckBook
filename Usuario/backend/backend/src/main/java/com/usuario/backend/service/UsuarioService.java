package com.usuario.backend.service;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public boolean autenticarUsuario(String correoInstitucional, String password) {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional);
        if (usuario == null) {
            return false;
        }
        // Verificar si la contraseña coincide con la encriptada
        return passwordEncoder.matches(password, usuario.getPassword());
    }
}