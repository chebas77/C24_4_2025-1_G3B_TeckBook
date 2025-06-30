package com.usuario.backend.service.user;

import com.usuario.backend.model.entity.Usuario;
import com.usuario.backend.repository.UsuarioRepository;
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

import java.util.Collections;
import java.util.Random;

@Service
public class UsuarioService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ========== REGISTRO NORMAL ==========
    
    /**
     * 📝 Registra un usuario normal (con validaciones completas)
     */
    public Usuario registrarUsuario(Usuario usuario) {
        try {
            logger.info("📝 Registrando usuario: {}", usuario.getCorreoInstitucional());
            
            // Validar que no existe
            if (usuarioRepository.findByCorreoInstitucional(usuario.getCorreoInstitucional()) != null) {
                throw new IllegalArgumentException("Ya existe un usuario con el correo: " + usuario.getCorreoInstitucional());
            }
            
            // Validar campos requeridos
            validateRequiredFields(usuario);
            
            // Encriptar contraseña
            String passwordOriginal = usuario.getPassword();
            usuario.setPassword(passwordEncoder.encode(passwordOriginal));
            
            // Guardar y verificar
            Usuario saved = usuarioRepository.save(usuario);
            logger.info("✅ Usuario registrado exitosamente: {}", saved.getId());
            
            return saved;
            
        } catch (Exception e) {
            logger.error("❌ Error al registrar usuario: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ========== OAUTH2 ==========
    
    /**
     * 🔐 Guarda usuario OAuth2 (sin validaciones completas)
     */
    public Usuario guardarUsuarioOAuth2(Usuario usuario) {
        try {
            logger.info("🔐 Guardando usuario OAuth2: {}", usuario.getCorreoInstitucional());
            
            // Solo verificar que no existe
            if (usuarioRepository.findByCorreoInstitucional(usuario.getCorreoInstitucional()) != null) {
                throw new IllegalArgumentException("Usuario OAuth2 ya existe: " + usuario.getCorreoInstitucional());
            }
            
            // Asegurar campos mínimos
            if (usuario.getPassword() == null) {
                usuario.setPassword(passwordEncoder.encode(generateRandomPassword()));
            }
            if (usuario.getRol() == null) {
                usuario.setRol(Usuario.RolUsuario.ESTUDIANTE);
            }
            
            Usuario saved = usuarioRepository.save(usuario);
            logger.info("✅ Usuario OAuth2 guardado: {}", saved.getId());
            
            return saved;
            
        } catch (Exception e) {
            logger.error("❌ Error OAuth2: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 🔄 Actualiza usuario OAuth2 existente
     */
    public Usuario registrarUsuarioOAuth(Usuario usuario) {
        try {
            Usuario existingUser = usuarioRepository.findByCorreoInstitucional(usuario.getCorreoInstitucional());
            
            if (existingUser != null) {
                // Actualizar datos existentes
                if (usuario.getNombre() != null) existingUser.setNombre(usuario.getNombre());
                if (usuario.getApellidos() != null) existingUser.setApellidos(usuario.getApellidos());
                if (usuario.getProfileImageUrl() != null) existingUser.setProfileImageUrl(usuario.getProfileImageUrl());
                
                return usuarioRepository.save(existingUser);
            } else {
                // Crear nuevo con datos mínimos
                return guardarUsuarioOAuth2(usuario);
            }
            
        } catch (Exception e) {
            logger.error("❌ Error registrarUsuarioOAuth: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ========== AUTENTICACIÓN ==========
    
    /**
     * 🔐 Autentica usuario con credenciales
     */
    public boolean autenticarUsuario(String correoInstitucional, String password) {
        try {
            logger.info("🔐 Autenticando: {}", correoInstitucional);
            
            if (correoInstitucional == null || password == null) {
                return false;
            }
            
            Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional);
            if (usuario == null) {
                logger.warn("❌ Usuario no encontrado: {}", correoInstitucional);
                return false;
            }
            
            if (usuario.getPassword() == null) {
                logger.error("❌ Usuario sin contraseña configurada");
                return false;
            }
            
            boolean matches = passwordEncoder.matches(password, usuario.getPassword());
            logger.info("🔐 Autenticación {}: {}", matches ? "exitosa" : "fallida", correoInstitucional);
            
            return matches;
            
        } catch (Exception e) {
            logger.error("❌ Error autenticación: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 🔐 Spring Security UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String correoInstitucional) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + correoInstitucional);
        }

        String password = usuario.getPassword() != null ? usuario.getPassword() : "";
        
        return new User(
                usuario.getCorreoInstitucional(),
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // ========== CRUD BÁSICO ==========
    
    /**
     * 📝 Actualiza usuario existente
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        try {
            logger.info("📝 Actualizando usuario: {}", usuario.getId());
            
            // Solo encriptar si la contraseña es nueva
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                if (!usuario.getPassword().startsWith("$2")) {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                }
            } else {
                // Mantener contraseña existente
                Usuario existing = usuarioRepository.findById(usuario.getId()).orElse(null);
                if (existing != null) {
                    usuario.setPassword(existing.getPassword());
                }
            }
            
            Usuario updated = usuarioRepository.save(usuario);
            logger.info("✅ Usuario actualizado: {}", updated.getId());
            
            return updated;
            
        } catch (Exception e) {
            logger.error("❌ Error actualizando usuario: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 🔍 Busca usuario por correo
     */
    public Usuario findByCorreoInstitucional(String correoInstitucional) {
        return usuarioRepository.findByCorreoInstitucional(correoInstitucional);
    }

    /**
     * 🔍 Busca usuario por ID
     */
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * 📋 Obtiene todos los usuarios
     */
    public Iterable<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * 🗑️ Elimina usuario por ID
     */
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * 🔒 Genera contraseña aleatoria para OAuth2
     */
    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    /**
     * ✅ Valida campos requeridos para registro normal
     */
    private void validateRequiredFields(Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
        if (usuario.getCorreoInstitucional() == null || usuario.getCorreoInstitucional().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo institucional es requerido");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son requeridos");
        }
    }
}