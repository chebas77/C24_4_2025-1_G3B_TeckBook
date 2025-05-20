package com.usuario.backend.controller;

import com.usuario.backend.model.Usuario;
import com.usuario.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
        
        String username = userDetails.getUsername();
        Usuario usuario = usuarioService.findByCorreoInstitucional(username);
        
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario, @AuthenticationPrincipal UserDetails userDetails) {
        // Verificar autenticación
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
        
        // Verificar que el usuario autenticado sea el mismo que se está actualizando
        Usuario existingUsuario = usuarioService.findById(id);
        if (existingUsuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        
        if (!existingUsuario.getCorreoInstitucional().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar este usuario");
        }
        
        // Proteger campos críticos
        usuario.setId(id);  // Asegurar que el ID sea el mismo
        usuario.setCorreoInstitucional(existingUsuario.getCorreoInstitucional());  // No permitir cambiar el correo
        usuario.setRol(existingUsuario.getRol());  // No permitir cambiar el rol
        
        // Actualizar el usuario
        Usuario updatedUsuario = usuarioService.actualizarUsuario(usuario);
        return ResponseEntity.ok(updatedUsuario);
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