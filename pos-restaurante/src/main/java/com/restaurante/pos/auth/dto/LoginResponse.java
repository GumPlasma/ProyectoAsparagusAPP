package com.restaurante.pos.auth.dto;

/**
 * DTO DE RESPUESTA DE LOGIN
 * =========================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar el resultado del proceso de autenticación hacia el frontend.
 *
 * ¿QUÉ ES?
 * - Objeto plano que representa la respuesta devuelta tras intentar iniciar sesión.
 * - Indica si la autenticación fue exitosa, el mensaje descriptivo, y los datos del usuario.
 *
 * PATRÓN BUILDER:
 * - Esta clase implementa manualmente el patrón Builder para construir respuestas de forma fluida.
 * - Ejemplo de uso:
 *     LoginResponse response = LoginResponse.builder()
 *         .success(true)
 *         .message("Login exitoso")
 *         .nombre("Juan Pérez")
 *         .rol("ADMIN")
 *         .build();
 *
 * ¿POR QUÉ BUILDER?
 * - Permite crear objetos con muchos campos opcionales de forma legible.
 * - Evita constructores con demasiados parámetros (telescoping constructors).
 */
public class LoginResponse {

    // ============================================================
    // CAMPOS DE ESTADO DE LA AUTENTICACIÓN
    // ============================================================

    /** true si las credenciales son correctas y el usuario está activo; false en caso contrario. */
    private boolean success;

    /** Mensaje descriptivo del resultado (ej: "Login exitoso", "Contraseña incorrecta"). */
    private String message;

    // ============================================================
    // CAMPOS DEL USUARIO AUTENTICADO
    // ============================================================

    /** Identificador único del usuario en el sistema. */
    private Long id;

    /** Nombre completo del usuario para mostrar en la interfaz. */
    private String nombre;

    /** Correo electrónico o username del usuario autenticado. */
    private String email;

    /** Nombre del rol asignado al usuario (ej: ADMIN, VENDEDOR, AUDITOR). */
    private String rol;

    // ============================================================
    // CONSTRUCTOR PRIVADO + BUILDER
    // ============================================================

    /**
     * Constructor privado para forzar el uso del patrón Builder.
     * Garantiza que solo se puedan crear instancias mediante LoginResponse.builder().
     */
    private LoginResponse() {}

    /**
     * Punto de entrada para construir una instancia usando el patrón Builder.
     *
     * @return Nueva instancia del Builder lista para configurar.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * CLASE BUILDER INTERNA
     * =====================
     *
     * Permite construir objetos LoginResponse de forma fluida (fluent API).
     * Cada método retorna el mismo Builder para encadenar llamadas.
     */
    public static class Builder {
        private LoginResponse response = new LoginResponse();

        /**
         * Establece el estado de éxito de la autenticación.
         * @param success true si fue exitoso, false si falló.
         * @return El mismo Builder para encadenar.
         */
        public Builder success(boolean success) {
            response.success = success;
            return this;
        }

        /**
         * Establece el mensaje descriptivo de la respuesta.
         * @param message Texto informativo sobre el resultado.
         * @return El mismo Builder para encadenar.
         */
        public Builder message(String message) {
            response.message = message;
            return this;
        }

        /**
         * Establece el ID del usuario autenticado.
         * @param id Identificador numérico del usuario.
         * @return El mismo Builder para encadenar.
         */
        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        /**
         * Establece el nombre del usuario autenticado.
         * @param nombre Nombre completo del usuario.
         * @return El mismo Builder para encadenar.
         */
        public Builder nombre(String nombre) {
            response.nombre = nombre;
            return this;
        }

        /**
         * Establece el email del usuario autenticado.
         * @param email Correo electrónico del usuario.
         * @return El mismo Builder para encadenar.
         */
        public Builder email(String email) {
            response.email = email;
            return this;
        }

        /**
         * Establece el rol del usuario autenticado.
         * @param rol Nombre del rol (ADMIN, VENDEDOR, AUDITOR).
         * @return El mismo Builder para encadenar.
         */
        public Builder rol(String rol) {
            response.rol = rol;
            return this;
        }

        /**
         * Construye y retorna la instancia final de LoginResponse.
         * @return LoginResponse con todos los valores configurados.
         */
        public LoginResponse build() {
            return response;
        }
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================
    // Necesarios para que Jackson serialice la respuesta a JSON
    // y para que el frontend acceda a cada campo.

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
