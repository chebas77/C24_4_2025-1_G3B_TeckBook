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

    /**
     * 🔧 MEJORADO: Registra un usuario con validaciones y debug
     */
    public Usuario registrarUsuario(Usuario usuario) {
        try {
            logger.info("🔧 Iniciando registro para usuario: {}", usuario.getCorreoInstitucional());
            
            // 🔥 VALIDACIÓN: Verificar que el usuario no exista ya
            Usuario existingUser = usuarioRepository.findByCorreoInstitucional(usuario.getCorreoInstitucional());
            if (existingUser != null) {
                logger.warn("❌ Usuario ya existe: {}", usuario.getCorreoInstitucional());
                throw new IllegalArgumentException("Ya existe un usuario con el correo: " + usuario.getCorreoInstitucional());
            }
            
            // 🔥 VALIDACIÓN: Verificar campos requeridos
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                logger.error("❌ Password es null o vacío");
                throw new IllegalArgumentException("La contraseña es requerida");
            }
            
            if (usuario.getCorreoInstitucional() == null || usuario.getCorreoInstitucional().trim().isEmpty()) {
                logger.error("❌ Correo institucional es null o vacío");
                throw new IllegalArgumentException("El correo institucional es requerido");
            }
            
            // 🔧 DEBUG: Log de datos recibidos
            logger.debug("🔧 Datos recibidos:");
            logger.debug("   - Nombre: {}", usuario.getNombre());
            logger.debug("   - Apellidos: {}", usuario.getApellidos());
            logger.debug("   - Código: {}", usuario.getCodigo());
            logger.debug("   - Correo: {}", usuario.getCorreoInstitucional());
            logger.debug("   - Password recibido: {}", usuario.getPassword() != null ? "***[PRESENTE]***" : "NULL");
            logger.debug("   - Ciclo: {}", usuario.getCiclo());
            logger.debug("   - CarreraId: {}", usuario.getCarreraId());
            logger.debug("   - Rol: {}", usuario.getRol());
            
            // 🔧 ENCRIPTAR CONTRASEÑA
            String passwordOriginal = usuario.getPassword();
            String passwordEncriptada = passwordEncoder.encode(passwordOriginal);
            usuario.setPassword(passwordEncriptada);
            
            logger.info("✅ Contraseña encriptada correctamente");
            logger.debug("🔧 Hash generado: {}", passwordEncriptada.substring(0, Math.min(30, passwordEncriptada.length())) + "...");
            
            // 🔧 VERIFICACIÓN: Probar que la encriptación funciona inmediatamente
            boolean verificacion = passwordEncoder.matches(passwordOriginal, passwordEncriptada);
            logger.info("🔧 Verificación inmediata de encriptación: {}", verificacion ? "✅ ÉXITO" : "❌ FALLÓ");
            
            if (!verificacion) {
                logger.error("❌ CRÍTICO: La encriptación falló durante el registro");
                throw new RuntimeException("Error en la encriptación de contraseña");
            }
            
            // 🔧 GUARDAR USUARIO
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            logger.info("✅ Usuario guardado exitosamente con ID: {}", usuarioGuardado.getId());
            
            // 🔧 VERIFICACIÓN FINAL: Leer desde BD y verificar password
            Usuario usuarioVerificacion = usuarioRepository.findByCorreoInstitucional(usuario.getCorreoInstitucional());
            if (usuarioVerificacion != null) {
                boolean verificacionFinal = passwordEncoder.matches(passwordOriginal, usuarioVerificacion.getPassword());
                logger.info("🔧 Verificación final desde BD: {}", verificacionFinal ? "✅ ÉXITO" : "❌ FALLÓ");
                
                if (!verificacionFinal) {
                    logger.error("❌ CRÍTICO: El usuario se guardó pero la contraseña no es verificable");
                }
            }
            
            return usuarioGuardado;
            
        } catch (Exception e) {
            logger.error("❌ Error al registrar usuario {}: {}", usuario.getCorreoInstitucional(), e.getMessage(), e);
            throw e;
        }
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
     * 🔧 MEJORADO: Verifica las credenciales con debug detallado
     */
    public boolean autenticarUsuario(String correoInstitucional, String password) {
        try {
            logger.info("🔧 Iniciando autenticación para: {}", correoInstitucional);
            
            // 🔥 VALIDACIÓN: Verificar parámetros
            if (correoInstitucional == null || correoInstitucional.trim().isEmpty()) {
                logger.warn("❌ Correo institucional vacío o null");
                return false;
            }
            
            if (password == null || password.trim().isEmpty()) {
                logger.warn("❌ Password vacío o null");
                return false;
            }
            
            // 🔧 BUSCAR USUARIO
            Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoInstitucional);
            if (usuario == null) {
                logger.warn("❌ Usuario no encontrado en BD: {}", correoInstitucional);
                return false;
            }
            
            logger.info("✅ Usuario encontrado: {} {} (ID: {})", usuario.getNombre(), usuario.getApellidos(), usuario.getId());
            
            // 🔧 DEBUG: Información del usuario
            logger.debug("🔧 Datos del usuario en BD:");
            logger.debug("   - ID: {}", usuario.getId());
            logger.debug("   - Nombre: {}", usuario.getNombre());
            logger.debug("   - Correo: {}", usuario.getCorreoInstitucional());
            logger.debug("   - Rol: {}", usuario.getRol());
            logger.debug("   - Password hash: {}", usuario.getPassword() != null ? usuario.getPassword().substring(0, Math.min(30, usuario.getPassword().length())) + "..." : "NULL");
            logger.debug("   - Password recibido: {}", "***[" + password.length() + " caracteres]***");
            
            // 🔧 VERIFICAR CONTRASEÑA
            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                logger.error("❌ Usuario no tiene contraseña configurada en BD");
                return false;
            }
            
            boolean matches = passwordEncoder.matches(password, usuario.getPassword());
            logger.info("🔧 Resultado de verificación de contraseña: {}", matches ? "✅ CORRECTO" : "❌ INCORRECTO");
            
            if (!matches) {
                logger.warn("❌ Contraseña incorrecta para usuario: {}", correoInstitucional);
                
                // 🔧 DEBUG ADICIONAL: Verificar si es problema de encoding
                logger.debug("🔧 Diagnóstico adicional:");
                logger.debug("   - Longitud password BD: {}", usuario.getPassword().length());
                logger.debug("   - Longitud password recibido: {}", password.length());
                logger.debug("   - Password comienza con $2: {}", usuario.getPassword().startsWith("$2"));
            } else {
                logger.info("✅ Autenticación exitosa para: {}", correoInstitucional);
            }
            
            return matches;
            
        } catch (Exception e) {
            logger.error("❌ Error durante autenticación para {}: {}", correoInstitucional, e.getMessage(), e);
            return false;
        }
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

        logger.debug("Usuario encontrado para Spring Security: {}", usuario.getNombre());

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