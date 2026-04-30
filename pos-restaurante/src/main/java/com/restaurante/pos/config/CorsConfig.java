package com.restaurante.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CONFIGURACIÓN DE CORS (Cross-Origin Resource Sharing)
 * =====================================================
 *
 * ¿QUÉ ES CORS?
 * - Es un mecanismo de seguridad de los navegadores que bloquea peticiones
 *   entre diferentes orígenes (dominios/puertos) por defecto.
 *
 * ¿POR QUÉ LO NECESITAMOS?
 * - El frontend corre en http://localhost:5173 (Vite/React)
 * - El backend corre en http://localhost:8080 (Spring Boot)
 * - Sin CORS, el navegador bloquearía las peticiones del frontend al backend
 *
 * CÓMO FUNCIONA:
 * - WebMvcConfigurer: Interfaz que permite personalizar Spring MVC
 * - addCorsMappings: Método que registra las reglas CORS
 */
@Configuration  // Indica que esta clase contiene configuración de Spring
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configura las reglas de CORS para toda la aplicación.
     *
     * @param registry Registro donde se agregan las reglas
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ==========================================================================
        // CONFIGURACIÓN DE CORS
        // ==========================================================================

        registry.addMapping("/**")  // Aplica a TODOS los endpoints de la API
                // ==========================================================================
                // ORIGEN PERMITIDO
                // ==========================================================================
                // Solo permite peticiones desde el frontend en desarrollo
                // Para producción, cambiar al dominio real del frontend
                .allowedOrigins("http://localhost:5173")

                // ==========================================================================
                // MÉTODOS HTTP PERMITIDOS
                // ==========================================================================
                // - GET: Obtener datos
                // - POST: Crear recursos
                // - PUT: Actualizar recursos
                // - DELETE: Eliminar recursos
                // - OPTIONS: Necesario para preflight requests de CORS
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // ==========================================================================
                // HEADERS PERMITIDOS
                // ==========================================================================
                // "*" = permite todos los headers (Content-Type, Authorization, etc.)
                .allowedHeaders("*")

                // ==========================================================================
                // CREDENCIALES
                // ==========================================================================
                // allowCredentials(true) permite enviar cookies y headers de autenticación
                // Necesario si se usa autenticación con sesiones o tokens en cookies
                .allowCredentials(true);
    }

    // ==========================================================================
    // NOTAS IMPORTANTES
    // ==========================================================================
    //
    // 1. PARA PRODUCCIÓN:
    //    - Cambiar allowedOrigins al dominio real del frontend
    //    - O usar allowedOriginPatterns para múltiples dominios
    //
    // 2. SEGURIDAD:
    //    - Nunca usar allowedOrigins("*") con allowCredentials(true)
    //    - Especificar siempre los orígenes permitidos
    //
    // 3. MÉTODOS NO PERMITIDOS:
    //    - PATCH no está incluido (no es necesario en esta API)
    //    - Se puede agregar si se necesita
}
