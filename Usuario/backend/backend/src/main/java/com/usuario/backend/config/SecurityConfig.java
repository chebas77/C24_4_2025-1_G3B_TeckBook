// ===============================================
// SECURITY CONFIG ACTUALIZADO
// ===============================================
package com.usuario.backend.config;

import com.usuario.backend.security.oauth2.CustomOAuth2UserService;
import com.usuario.backend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.usuario.backend.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // Para OAuth2
                )
                .authorizeHttpRequests(auth -> auth
                        // 🔥 ENDPOINTS COMPLETAMENTE PÚBLICOS
                        .requestMatchers("/", "/oauth2/**", "/login/**", "/api/public/**", "/error").permitAll()
                        
                        // 🔥 ENDPOINTS DE AUTENTICACIÓN PÚBLICOS
                        .requestMatchers("/api/usuarios/register", "/api/usuarios/login").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/google-login").permitAll()
                        
                        // 🔥 ENDPOINTS PARA FILTROS EN CASCADA (PÚBLICOS PARA CREAR AULAS)
                        .requestMatchers("/api/departamentos/activos").permitAll()
                        .requestMatchers("/api/carreras/activas").permitAll()
                        .requestMatchers("/api/carreras/departamento/*/activas").permitAll() // 🆕 NUEVO
                        .requestMatchers("/api/ciclos/todos").permitAll() // 🆕 NUEVO
                        .requestMatchers("/api/ciclos/carrera/*").permitAll() // 🆕 NUEVO
                        .requestMatchers("/api/secciones/carrera/*").permitAll() // 🆕 NUEVO
                        .requestMatchers("/api/secciones/carrera/*/ciclo/*").permitAll() // 🆕 NUEVO
                        
                        // 🔥 HEALTH CHECKS PÚBLICOS
                        .requestMatchers("/api/carreras/health").permitAll()
                        .requestMatchers("/api/departamentos/health").permitAll() // 🆕 NUEVO
                        .requestMatchers("/api/ciclos/health").permitAll() // 🆕 NUEVO
                        .requestMatchers("/api/secciones/health").permitAll() // 🆕 NUEVO
                        
                        // 🔥 ENDPOINTS QUE REQUIEREN AUTENTICACIÓN
                        .requestMatchers("/api/usuarios/me", "/api/usuarios/{id}").authenticated()
                        .requestMatchers("/api/auth/user", "/api/auth/logout", "/api/auth/token/status").authenticated()
                        .requestMatchers("/api/upload/**").authenticated()
                        
                        // 🔥 AULAS VIRTUALES - REQUIERE AUTENTICACIÓN
                        .requestMatchers("/api/aulas", "/api/aulas/**").authenticated()
                        .requestMatchers("/api/aulas-virtuales", "/api/aulas-virtuales/**").authenticated()
                        
                        // 🔥 INVITACIONES - REQUIERE AUTENTICACIÓN 
                        .requestMatchers("/api/invitaciones/**").authenticated() // 🆕 AGREGADO
                        
                        // 🔥 ENDPOINTS DE CARRERAS QUE REQUIEREN AUTENTICACIÓN (ADMINISTRACIÓN)
                        .requestMatchers("/api/carreras/{id}", "/api/carreras/departamento/**", 
                                        "/api/carreras/buscar").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/carreras").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/carreras/**").authenticated()
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/carreras/**").authenticated()
                        
                        // 🔥 ENDPOINTS DE DEPARTAMENTOS QUE REQUIEREN AUTENTICACIÓN (ADMINISTRACIÓN)
                        .requestMatchers("/api/departamentos/{id}").authenticated() // 🆕 NUEVO
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/departamentos").authenticated() // 🆕 NUEVO
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/departamentos/**").authenticated() // 🆕 NUEVO
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/departamentos/**").authenticated() // 🆕 NUEVO
                        
                        // 🔥 DEBUG ENDPOINTS (TEMPORALES)
                        .requestMatchers("/api/debug/**").permitAll()
                        
                        // 🔥 TODOS LOS DEMÁS REQUIEREN AUTENTICACIÓN
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(redirectEndpoint -> redirectEndpoint
                                .baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\":\"No autorizado\",\"message\":\"" + authException.getMessage() + "\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 🔥 CONFIGURACIÓN CORS MEJORADA
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}