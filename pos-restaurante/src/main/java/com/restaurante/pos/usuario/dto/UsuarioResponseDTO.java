package com.restaurante.pos.usuario.dto;

import lombok.Data;

/**
 * DTO PARA RESPUESTA DE USUARIO
 * =============================
 *
 * PROPÓSITO:
 * - Define qué datos del usuario se envían al cliente en la respuesta JSON
 * - Excluye datos sensibles como la contraseña
 * - Incluye datos calculados como nombreCompleto
 *
 * DIFERENCIA CON LOS OTROS DTOs:
 * - CrearUsuarioDTO → Datos para CREAR (entrada)
 * - ActualizarUsuarioDTO → Datos para ACTUALIZAR (entrada)
 * - UsuarioResponseDTO → Datos para MOSTRAR (salida)
 */
@Data
public class UsuarioResponseDTO {

    // ==========================================================================
    // DATOS DE IDENTIFICACIÓN
    // ==========================================================================

    /**
     * ID único del usuario.
     * Se usa para referenciar al usuario en otras operaciones.
     */
    private Long id;

    /**
     * Nombre de usuario (username).
     * Usado para login.
     */
    private String username;

    // ==========================================================================
    // DATOS PERSONALES
    // ==========================================================================

    /**
     * Nombre real del usuario.
     */
    private String nombre;

    /**
     * Apellido del usuario.
     */
    private String apellido;

    /**
     * Nombre completo (nombre + apellido).
     * Campo calculado, útil para mostrar en la UI.
     * Ejemplo: "Juan Pérez"
     */
    private String nombreCompleto;

    // ==========================================================================
    // DATOS DE CONTACTO
    // ==========================================================================

    /**
     * Email del usuario.
     */
    private String email;

    /**
     * Teléfono del usuario.
     */
    private String telefono;

    // ==========================================================================
    // DATOS DE CONFIGURACIÓN
    // ==========================================================================

    /**
     * Información del rol asignado.
     * Contiene: id, nombre, descripcion del rol
     */
    private RolDTO rol;

    /**
     * Estado del usuario.
     * true = activo, false = eliminado (borrado lógico)
     */
    private Boolean activo;

    // NOTA: La contraseña NO está incluida (por seguridad)
}