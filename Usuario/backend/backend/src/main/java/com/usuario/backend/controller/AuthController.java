package com.usuario.backend.controller;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.security.JwtTokenProvider;
import com.usuario.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;  // Agrega esta importación
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        boolean isAuthenticated = usuarioService.autenticarUsuario(
                usuario.getCorreoInstitucional(), usuario.getPassword());

        if (isAuthenticated) {
            // Generar JWT token
            String token = jwtTokenProvider.generateToken(usuario.getCorreoInstitucional());

            // Devolver token y datos básicos del usuario
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");

            Usuario user = usuarioService.findByCorreoInstitucional(usuario.getCorreoInstitucional());
            response.put("id", user.getId());
            response.put("nombre", user.getNombre());
            response.put("apellidos", user.getApellidos());
            response.put("correoInstitucional", user.getCorreoInstitucional());
            response.put("rol", user.getRol());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        }
    }

    @GetMapping("/google-login")
    public ResponseEntity<?> googleLogin() {
        // Esta URL redirecciona a la página de autenticación de Google
        // Spring Security se encargará de la redirección
        // Este endpoint es solo para proporcionar la URL al frontend
        String redirectUrl = "/oauth2/authorize/google";
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", redirectUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);

            if (jwtTokenProvider.validateToken(jwt)) {
                String email = jwtTokenProvider.getEmailFromToken(jwt);
                Usuario user = usuarioService.findByCorreoInstitucional(email);

                if (user != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("nombre", user.getNombre());
                    userInfo.put("apellidos", user.getApellidos());
                    userInfo.put("correoInstitucional", user.getCorreoInstitucional());
                    userInfo.put("rol", user.getRol());

                    return ResponseEntity.ok(userInfo);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o usuario no encontrado");
    }

    @PostMapping("/test-oauth-user")
    public ResponseEntity<?> testOAuthUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            String lastName = request.get("lastName");

            // Validar dominio
            if (!email.endsWith("@tecsup.edu.pe")) {
                return ResponseEntity.badRequest().body("Solo se permiten correos con dominio @tecsup.edu.pe");
            }

            // Crear usuario
            Usuario newUser = new Usuario();
            newUser.setCorreoInstitucional(email);
            newUser.setNombre(name);
            newUser.setApellidos(lastName);
            newUser.setRol("ESTUDIANTE");
            newUser.setCodigo(email.split("@")[0]);
            newUser.setCiclo("1");

            // Registrar usuario
            Usuario saved = usuarioService.registrarUsuarioOAuth(newUser);

            // Generar token JWT
            String token = jwtTokenProvider.generateToken(email);

            Map<String, Object> response = new HashMap<>();
            response.put("user", saved);
            response.put("token", token);
            response.put("redirectUrl", "http://localhost:5173/oauth/callback?token=" + token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("class", e.getClass().getName());

            if (e.getCause() != null) {
                error.put("cause", e.getCause().getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}