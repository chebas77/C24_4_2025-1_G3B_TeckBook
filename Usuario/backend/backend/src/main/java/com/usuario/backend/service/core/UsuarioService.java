package com.usuario.backend.service.core;

import com.usuario.backend.model.entity.core.Usuario;
import com.usuario.backend.repository.core.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmailAndActivoTrue(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        if (usuario.getRol() == null) {
            usuario.setRol(Usuario.RolUsuario.ALUMNO);
        }
        
        return usuarioRepository.save(usuario);
    }

    public Usuario registrarUsuarioOAuth(Usuario usuario) {
        Optional<Usuario> existing = usuarioRepository.findByEmail(usuario.getEmail());
        if (existing.isPresent()) {
            return actualizarDatosOAuth(existing.get(), usuario);
        }
        
        usuario.setPassword(passwordEncoder.encode(generateRandomPassword()));
        usuario.setRol(Usuario.RolUsuario.ALUMNO);
        if (usuario.getDepartamentoId() == null) {
            usuario.setDepartamentoId(1L);
        }
        
        return usuarioRepository.save(usuario);
    }

    public boolean autenticarUsuario(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        Usuario usuario = usuarioOpt.get();
        if (!usuario.getActivo()) {
            return false;
        }
        
        return passwordEncoder.matches(password, usuario.getPassword());
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            if (!usuario.getPassword().startsWith("$2a$")) {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        } else {
            Optional<Usuario> existing = usuarioRepository.findById(usuario.getId());
            if (existing.isPresent()) {
                usuario.setPassword(existing.get().getPassword());
            }
        }
        
        return usuarioRepository.save(usuario);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }
        
        Usuario usuario = usuarioOpt.get();
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }
        
        return new User(
            usuario.getEmail(),
            usuario.getPassword() != null ? usuario.getPassword() : "",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }

    private Usuario actualizarDatosOAuth(Usuario existing, Usuario newData) {
        if (newData.getNombres() != null) existing.setNombres(newData.getNombres());
        if (newData.getApellido() != null) existing.setApellido(newData.getApellido());
        if (newData.getProfileImageUrl() != null) existing.setProfileImageUrl(newData.getProfileImageUrl());
        
        return usuarioRepository.save(existing);
    }

    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString().substring(0, 12);
    }
}