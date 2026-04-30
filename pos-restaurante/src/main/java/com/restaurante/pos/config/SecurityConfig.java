package com.restaurante.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CONFIGURACIÓN DE SEGURIDAD SPRING SECURITY
 * ==========================================
 *
 * CAPA: Config (Configuración de Seguridad)
 * RESPONSABILIDAD: Definir las reglas de seguridad, autenticación, autorización y CORS para toda la aplicación.
 *
 * ¿QUÉ HACE?
 * - Configura el filtro de seguridad que intercepta todas las peticiones HTTP.
 * - Desactiva CSRF (ya que es una API REST stateless).
 * - Configura CORS para permitir peticiones desde el frontend.
 * - Define el codificador de contraseñas (BCrypt).
 * - Permite todas las peticiones (en desarrollo) y configura headers de seguridad.
 *
 * NOTA IMPORTANTE:
 * Actualmente la configuración permite TODAS las peticiones sin autenticación.
 * Esto es útil para desarrollo, pero en producción se debe implementar:
 * - Autenticación con JWT o sesiones.
 * - Autorización basada en roles (ADMIN, VENDEDOR, AUDITOR).
 * - Restricción de endpoints sensibles.
 *
 * ANOTACIONES SPRING:
 * - @Configuration: Indica que esta clase contiene definiciones de beans de Spring.
 * - @EnableWebSecurity: Activa la configuración de seguridad web de Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * CONFIGURACIÓN DEL FILTRO DE SEGURIDAD
     * =====================================
     *
     * Define la cadena de filtros de seguridad que procesa cada petición HTTP.
     *
     * CONFIGURACIONES APLICADAS:
     * - CSRF desactivado: Las APIs REST stateless no necesitan protección CSRF.
     * - CORS activado: Permite peticiones desde orígenes diferentes (frontend en otro puerto).
     * - Autorización: Permite TODAS las peticiones sin autenticación (modo desarrollo).
     * - Headers: Configura frameOptions para permitir iframes del mismo origen (necesario para H2 Console).
     *
     * @param http Objeto HttpSecurity que permite configurar la seguridad web.
     * @return SecurityFilterChain configurado y listo para usar.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ---------- DESACTIVAR CSRF ----------
                // CSRF (Cross-Site Request Forgery) es una protección para formularios web tradicionales.
                // En APIs REST que usan autenticación stateless (JWT/Token), CSRF no es necesario.
                .csrf(AbstractHttpConfigurer::disable)

                // ---------- CONFIGURAR CORS ----------
                // Habilita CORS usando la configuración definida en corsConfigurationSource().
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ---------- REGLAS DE AUTORIZACIÓN ----------
                // En desarrollo: permite TODAS las peticiones sin autenticación.
                // TODO: En producción, restringir según roles y requerir autenticación.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // ---------- CONFIGURACIÓN DE HEADERS ----------
                // frameOptions.sameOrigin() permite que la aplicación se embeba en iframes del mismo origen.
                // Esto es necesario para la consola H2 que usa frames.
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        // Construye y retorna la cadena de filtros configurada.
        return http.build();
    }

    /**
     * CONFIGURACIÓN DE CORS (CROSS-ORIGIN RESOURCE SHARING)
     * =====================================================
     *
     * Define qué orígenes, métodos y headers están permitidos en las peticiones cross-origin.
     *
     * ¿POR QUÉ ES NECESARIO?
     * - El frontend corre en un dominio/puerto diferente al backend.
     * - Los navegadores bloquean peticiones cross-origin por seguridad.
     * - CORS le dice al navegador que el backend permite estas peticiones.
     *
     * CONFIGURACIÓN ACTUAL (DESARROLLO):
     * - Orígenes: Cualquiera (*) → Permite peticiones desde cualquier dominio.
     * - Métodos: GET, POST, PUT, DELETE, OPTIONS, PATCH.
     * - Headers: Todos (*).
     * - Credenciales: Permitidas (cookies, headers de autenticación).
     *
     * @return CorsConfigurationSource con las reglas CORS configuradas.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite cualquier origen (en desarrollo).
        // TODO: En producción, restringir al dominio real del frontend.
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Métodos HTTP permitidos.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Headers permitidos en las peticiones.
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permite enviar credenciales (cookies, authorization headers).
        configuration.setAllowCredentials(true);

        // Tiempo máximo (en segundos) que el navegador puede cachear la respuesta preflight.
        configuration.setMaxAge(3600L);

        // Aplica esta configuración a TODAS las rutas de la aplicación.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * CODIFICADOR DE CONTRASEÑAS (BCRYPT)
     * ===================================
     *
     * Define el algoritmo de hashing para contraseñas.
     *
     * ¿POR QUÉ BCRYPT?
     * - Es un algoritmo de hashing adaptativo: automáticamente maneja salts únicos.
     * - Es resistente a ataques de fuerza bruta y rainbow tables.
     * - Es el estándar recomendado por Spring Security.
     * - El hash incluye el salt, por lo que no necesita almacenarse por separado.
     *
     * USO:
     * - Para codificar: passwordEncoder.encode("password") → genera hash.
     * - Para verificar: passwordEncoder.matches("password", hashAlmacenado) → true/false.
     *
     * @return Instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
