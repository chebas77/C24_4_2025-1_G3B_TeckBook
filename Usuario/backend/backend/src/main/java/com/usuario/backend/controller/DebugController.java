package com.usuario.backend.controller;

import com.usuario.backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam(required = false) String error) {
        logger.info("Login page accessed. Error: {}", error);

        Map<String, String> response = new HashMap<>();
        if (error != null) {
            response.put("status", "error");
            response.put("message", "Error de autenticación: " + error);
        } else {
            response.put("status", "success");
            response.put("message", "Página de login");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/debug/oauth2-attributes")
    public ResponseEntity<Map<String, Object>> getOAuth2Attributes(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            return ResponseEntity.ok(oAuth2User.getAttributes());
        }
        return ResponseEntity.ok(Collections.singletonMap("error", "No OAuth2 authentication found"));
    }

    @GetMapping("/api/debug/test-token")
    public ResponseEntity<Map<String, String>> testToken(@RequestParam String email) {
        try {
            String token = tokenProvider.generateToken(email);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("email", email);
            response.put("redirect", "http://localhost:5173/oauth/callback?token=" + token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}