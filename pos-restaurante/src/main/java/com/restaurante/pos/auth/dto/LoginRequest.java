package com.restaurante.pos.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO DE SOLICITUD DE LOGIN
 * =========================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar las credenciales de autenticación desde el frontend hacia el backend.
 *
 * ¿QUÉ ES?
 * - Objeto plano que representa los datos enviados por el usuario al intentar iniciar sesión.
 * - Se usa en el endpoint POST /auth/login como cuerpo de la petición (@RequestBody).
 *
 * VALIDACIONES:
 * - @NotBlank: Garantiza que el campo no sea nulo ni vacío (ni espacios en blanco).
 *   Si la validación falla, Spring devuelve automáticamente un error 400 Bad Request.
 *
 * SEGURIDAD:
 * - La contraseña viaja en texto plano en esta clase, por lo que es CRÍTICO que la comunicación
 *   se realice sobre HTTPS en producción para evitar interceptaciones.
 * - El backend NUNCA almacena la contraseña en texto plano; solo la usa para comparar con el hash BCrypt.
 */
public class LoginRequest {

    /**
     * Correo electrónico o nombre de usuario del usuario que intenta autenticarse.
     *
     * @NotBlank(message = "El email es obligatorio"):
     *   Valida que este campo no esté vacío. Si lo está, devuelve el mensaje indicado.
     */
    @NotBlank(message = "El email es obligatorio")
    private String email;

    /**
     * Contraseña del usuario en texto plano.
     *
     * @NotBlank(message = "La contraseña es obligatoria"):
     *   Valida que este campo no esté vacío.
     *
     * IMPORTANTE: Esta contraseña se comparará con el hash almacenado en la base de datos
     * usando PasswordEncoder.matches() en el AuthService.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    // ============================================================
    // CONSTRUCTORES
    // ============================================================

    /**
     * Constructor vacío requerido por Jackson para deserialización JSON.
     * Spring usa este constructor automáticamente al convertir el cuerpo de la petición en objeto.
     */
    public LoginRequest() {}

    /**
     * Constructor con parámetros para crear instancias de forma programática.
     *
     * @param email    Correo o username del usuario.
     * @param password Contraseña en texto plano.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================
    // Necesarios para que Jackson serialice/deserialice correctamente,
    // y para que las validaciones de Bean Validation accedan a los campos.

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
