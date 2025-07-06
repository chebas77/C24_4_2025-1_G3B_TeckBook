package com.usuario.backend.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.TimeZone;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@RestController
public class WelcomeController {

    @Value("${spring.application.name:TeckBook Backend}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String version;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        
        // Información básica
        response.put("message", "¡Bienvenido a TeckBook! 🎓");
        response.put("description", "Plataforma educativa para la gestión de aulas virtuales y contenido académico");
        response.put("application", applicationName);
        response.put("version", version);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("status", "🟢 Operativo");
        
        // Información del entorno
        Map<String, Object> environment = new HashMap<>();
        environment.put("profile", System.getProperty("spring.profiles.active", "development"));
        environment.put("java_version", System.getProperty("java.version"));
        environment.put("frontend_url", frontendUrl);
        response.put("environment", environment);
        
        // Stack tecnológico
        Map<String, Object> technologies = new HashMap<>();
        technologies.put("backend", Arrays.asList(
            "☕ Java 17+",
            "🍃 Spring Boot 3.x",
            "🔐 Spring Security",
            "🌐 Spring Data JPA",
            "🔑 JWT Authentication",
            "🔗 OAuth2 (Google)",
            "🐘 MYSQL ",
            "☁️ Cloudinary (Storage)"
        ));
        
        technologies.put("frontend", Arrays.asList(
            "⚛️ React.js",
            "🚀 Vite",
            "🎨 CSS3 + Custom Styles",
            "🔄 React Router",
            "📱 Responsive Design"
        ));
        
        technologies.put("deployment", Arrays.asList(
            "🚀 Koyeb (Backend)",
            "▲ Vercel (Frontend)",
            "🌍 HTTPS/SSL",
            "🔄 CI/CD Integration"
        ));
        
        response.put("technologies", technologies);
        
        // Endpoints principales
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("authentication", Arrays.asList(
            "POST /api/auth/login - Login tradicional",
            "GET /oauth2/authorize/google - Login con Google",
            "GET /api/auth/user - Información del usuario"
        ));
        
        endpoints.put("academic", Arrays.asList(
            "GET /api/aulas - Aulas virtuales del usuario",
            "GET /api/departamentos/activos - Departamentos disponibles",
            "GET /api/carreras/activas - Carreras disponibles",
            "POST /api/aulas/{id}/anuncios - Crear anuncio"
        ));
        
        endpoints.put("health", Arrays.asList(
            "GET /api/health - Estado del sistema",
            "GET /api/debug - Información de depuración"
        ));
        
        response.put("endpoints", endpoints);
        
        // Características principales
        response.put("features", Arrays.asList(
            "🎯 Gestión de aulas virtuales",
            "📚 Compartir material académico", 
            "❓ Sistema de preguntas y respuestas",
            "📎 Subida de archivos",
            "👥 Invitaciones a aulas",
            "📱 Interfaz responsive",
            "🔐 Autenticación segura",
            "☁️ Almacenamiento en la nube",
            "🔄 Sincronización en tiempo real"
        ));
        
        // Links útiles
        Map<String, String> links = new HashMap<>();
        links.put("frontend", frontendUrl);
        links.put("login", frontendUrl + "/");
        links.put("google_auth", "/oauth2/authorize/google");
        links.put("api_health", "/api/health");
        response.put("links", links);
        
        // Información de contacto/soporte
        Map<String, Object> support = new HashMap<>();
        support.put("institution", "TECSUP - Instituto de Educación Superior");
        support.put("domain", "@tecsup.edu.pe");
        support.put("note", "Solo usuarios con correo institucional pueden acceder");
        response.put("support", support);
        
        return ResponseEntity.ok(response);
    }
    
    // Endpoint adicional para información de salud
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("timestamp", LocalDateTime.now());
        
        // Verificar componentes críticos
        Map<String, String> components = new HashMap<>();
        components.put("database", "🟢 Connected");
        components.put("jwt_service", "🟢 Operational");
        components.put("oauth2", "🟢 Configured");
        components.put("file_storage", "🟢 Available");
        
        health.put("components", components);
        
        // Métricas básicas
        Map<String, Object> metrics = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        metrics.put("memory_used_mb", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        metrics.put("memory_total_mb", runtime.totalMemory() / 1024 / 1024);
        metrics.put("processors", runtime.availableProcessors());
        
        health.put("metrics", metrics);
        
        return ResponseEntity.ok(health);
    }
    
    // Endpoint para información del sistema (debugging)
    @GetMapping("/api/debug")
    public ResponseEntity<Map<String, Object>> debug() {
        Map<String, Object> debug = new HashMap<>();
        
        // Variables de entorno relevantes (sin secretos)
        Map<String, String> env = new HashMap<>();
        env.put("FRONTEND_URL", frontendUrl);
        env.put("SPRING_PROFILES_ACTIVE", System.getProperty("spring.profiles.active", "default"));
        env.put("JAVA_VERSION", System.getProperty("java.version"));
        env.put("OS_NAME", System.getProperty("os.name"));
        
        debug.put("environment_variables", env);
        
        // Información del sistema
        Map<String, Object> system = new HashMap<>();
        system.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() + " ms");
        system.put("start_time", new Date(ManagementFactory.getRuntimeMXBean().getStartTime()));
        system.put("timezone", TimeZone.getDefault().getID());
        
        debug.put("system_info", system);
        
        // Headers de ejemplo para CORS
        Map<String, String> corsInfo = new HashMap<>();
        corsInfo.put("allowed_origins", frontendUrl + ", http://localhost:3000, http://localhost:5173");
        corsInfo.put("allowed_methods", "GET, POST, PUT, DELETE, OPTIONS");
        corsInfo.put("allowed_headers", "Authorization, Content-Type, X-Requested-With, Accept");
        
        debug.put("cors_configuration", corsInfo);
        
        return ResponseEntity.ok(debug);
    }
}