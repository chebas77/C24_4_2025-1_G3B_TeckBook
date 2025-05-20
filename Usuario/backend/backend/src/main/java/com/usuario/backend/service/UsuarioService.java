package com.usuario.backend.service;

import com.usuario.backend.model.Usuario;
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

    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardarla
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario registrarUsuarioOAuth(Usuario usuario) {
        try {
            // Verificar si ya existe un usuario con ese correo
            Usuario existingUser = usuarioRepository.findByCorreoInstitucional(usuario.getCorreoInstitucional());
            if (existingUser != null) {
                // Actualizar información si es necesario
                existingUser.setNombre(usuario.getNombre());
                existingUser.setApellidos(usuario.getApellidos());
                return usuarioRepository.save(existingUser);
            }

            // Asignar valores por defecto a campos obligatorios
            if (usuario.getCodigo() == null || usuario.getCodigo().isEmpty()) {
                // Generar un código basado en el email
                String emailUsername = usuario.getCorreoInstitucional().split("@")[0];
                usuario.setCodigo(emailUsername);
            }

            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("ESTUDIANTE");
            }

            // Establecer valores por defecto para otros campos obligatorios
            if (usuario.getCiclo() == null || usuario.getCiclo().isEmpty()) {
                usuario.setCiclo("1");
            }

            // Asegurarse de que se asigne un departamento válido (obligatorio por la restricción de la BD)
            if (usuario.getDepartamentoId() == null) {
                usuario.setDepartamentoId(1L); // ID 1: Tecnología Digital
            }

            // Valores por defecto para campos con restricciones de clave foránea que pueden ser NULL
            // Comentados por defecto ya que según el esquema pueden ser NULL
            // if (usuario.getCarreraId() == null) {
            //     usuario.setCarreraId(1L);
            // }
            //
            // if (usuario.getSeccionId() == null) {
            //     usuario.setSeccionId(1L);
            // }

            // Para un usuario OAuth no necesitamos contraseña, pero podemos
            // establecer un valor aleatorio si la columna no permite nulos
            if (usuario.getPassword() == null) {
                String randomPassword = generateRandomPassword();
                usuario.setPassword(passwordEncoder.encode(randomPassword));
            }

            // Guardar el usuario
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            logger.error("Error al registrar usuario OAuth: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Genera una contraseña aleatoria
     * @return Una cadena aleatoria de 12 caracteres
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    /**
     * Verifica las credenciales de un usuario
     * @param correoInstitucional El correo institucional del usuario
     * @param password La contraseña a verificar
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean autenticarUsuario(String correoInstitucional, String password) {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional);
        if (usuario == null) {
            return false;
        }
        // Verificar si la contraseña coincide con la encriptada
        return passwordEncoder.matches(password, usuario.getPassword());
    }

    /**
     * Busca un usuario por su correo institucional
     * @param correoInstitucional El correo institucional a buscar
     * @return El usuario encontrado o null si no existe
     */
    public Usuario findByCorreoInstitucional(String correoInstitucional) {
        return usuarioRepository.findByCorreoInstitucional(correoInstitucional);
    }

    /**
     * Implementación de UserDetailsService para Spring Security
     * Carga un usuario por su nombre de usuario (correo institucional)
     * @param correoInstitucional El correo institucional del usuario
     * @return Un objeto UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si no se encuentra el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String correoInstitucional) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional);
        if (usuario == null) {
            logger.error("Usuario no encontrado con correo: {}", correoInstitucional);
            throw new UsernameNotFoundException("Usuario no encontrado con correo: " + correoInstitucional);
        }

        logger.debug("Usuario encontrado: {}", usuario.getNombre());

        // Para usuarios OAuth, la contraseña puede ser null
        String password = usuario.getPassword() != null ? usuario.getPassword() : "";

        return new User(
                usuario.getCorreoInstitucional(),
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    /**
     * Actualiza la información de un usuario
     * @param usuario El usuario con la información actualizada
     * @return El usuario actualizado
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        // Si se está actualizando la contraseña, encriptarla
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            // Si no se proporciona una contraseña, mantener la existente
            Usuario existingUser = usuarioRepository.findById(usuario.getId()).orElse(null);
            if (existingUser != null) {
                usuario.setPassword(existingUser.getPassword());
            }
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Obtiene todos los usuarios
     * @return Una lista con todos los usuarios
     */
    public Iterable<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su ID
     * @param id El ID del usuario a buscar
     * @return El usuario encontrado o null si no existe
     */
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Elimina un usuario por su ID
     * @param id El ID del usuario a eliminar
     */
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

}