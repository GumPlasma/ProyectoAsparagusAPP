package com.restaurante.pos.auth.controller;

import com.restaurante.pos.auth.dto.LoginRequest;
import com.restaurante.pos.auth.dto.LoginResponse;
import com.restaurante.pos.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CONTROLADOR REST DE AUTENTICACIÓN
 * =================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints públicos para el proceso de login y verificación de salud del servicio de autenticación.
 *
 * ¿QUÉ HACE?
 * - Recibe las credenciales de usuario (email/username y contraseña).
 * - Delega la validación de credenciales al {@link AuthService}.
 * - Retorna una respuesta estructurada indicando éxito o fracaso del login.
 * - Provee un endpoint de health check para verificar que el servicio está activo.
 *
 * ANOTACIONES SPRING:
 * - @RestController: Combina @Controller y @ResponseBody; todos los métodos devuelven JSON.
 * - @RequestMapping("/auth"): Define la ruta base para todos los endpoints de autenticación.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================

    /**
     * Servicio de autenticación inyectado por constructor.
     * Contiene la lógica de validación de credenciales y generación de respuestas.
     */
    private final AuthService authService;

    /**
     * Constructor con inyección de dependencias.
     * Spring resuelve automáticamente el bean AuthService y lo pasa al crear el controller.
     *
     * @param authService Servicio que procesa la lógica de login.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ============================================================
    // ENDPOINTS DE AUTENTICACIÓN
    // ============================================================

    /**
     * AUTENTICAR USUARIO (LOGIN)
     * --------------------------
     * POST /auth/login
     *
     * Recibe las credenciales del usuario, las valida y retorna el resultado.
     *
     * @param request DTO con email y contraseña. Se valida automáticamente con @Valid.
     * @return ResponseEntity<LoginResponse>:
     *         - HTTP 200 OK si las credenciales son correctas.
     *         - HTTP 401 UNAUTHORIZED si las credenciales son inválidas o el usuario está inactivo.
     *
     * ANOTACIONES:
     * - @PostMapping: Responde a peticiones HTTP POST en /auth/login.
     * - @Valid: Activa la validación de Bean Validation sobre el DTO (campos @NotBlank).
     * - @RequestBody: Indica que Spring debe deserializar el cuerpo JSON en un objeto LoginRequest.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Delega la lógica de autenticación al servicio.
        LoginResponse response = authService.login(request);

        // Si la autenticación fue exitosa, retorna 200 OK con los datos del usuario.
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Si falló, retorna 401 UNAUTHORIZED con el mensaje de error.
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * HEALTH CHECK DEL SERVICIO DE AUTENTICACIÓN
     * ------------------------------------------
     * GET /auth/health
     *
     * Endpoint simple para verificar que el servicio de autenticación está funcionando.
     * Útil para monitoreo, balanceadores de carga y pruebas de conectividad.
     *
     * @return ResponseEntity<String> con mensaje de confirmación y HTTP 200 OK.
     *
     * ANOTACIONES:
     * - @GetMapping: Responde a peticiones HTTP GET en /auth/health.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
