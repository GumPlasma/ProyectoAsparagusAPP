package com.restaurante.pos.usuario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA ACTUALIZAR USUARIO
 * ===========================
 *
 * DIFERENCIA CON CrearUsuarioDTO:
 * - Todos los campos son OPCIONALES (solo se actualizan los que se envían)
 * - No tiene @NotBlank, solo @Size para validar longitud máxima
 * - Incluye passwordNuevo para cambio opcional de contraseña
 *
 * ¿POR QUÉ NO TIENE USERNAME?
 * - El username es único y no debería cambiarse fácilmente
 * - Si se necesita cambiar, se hace mediante otro proceso administrativo
 */
@Data
public class ActualizarUsuarioDTO {

    // ==========================================================================
    // CAMPOS DE INFORMACIÓN PERSONAL (OPCIONALES)
    // ==========================================================================

    /**
     * Nuevo nombre del usuario.
     * Si es null, no se actualiza el nombre.
     *
     * VALIDACIONES:
     * - @Size(max = 100): Solo valida longitud máxima si se envía valor
     */
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    /**
     * Nuevo apellido del usuario.
     * Si es null, no se actualiza el apellido.
     *
     * VALIDACIONES:
     * - @Size(max = 100): Solo valida longitud máxima
     */
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    // ==========================================================================
    // CAMPOS DE CONTACTO (OPCIONALES)
    // ==========================================================================

    /**
     * Nuevo email del usuario.
     *
     * VALIDACIONES:
     * - @Email: Valida formato si se envía valor
     * - Si es null, se mantiene el email actual
     */
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Nuevo teléfono del usuario.
     *
     * SIN VALIDACIONES:
     * - Campo completamente opcional
     */
    private String telefono;

    // ==========================================================================
    // CAMPOS DE CONFIGURACIÓN (OPCIONALES)
    // ==========================================================================

    /**
     * Nuevo ID de rol para el usuario.
     *
     * USO:
     * - Si es null, se mantiene el rol actual
     * - Si se envía un ID, el servicio validará que el rol exista
     */
    private Long rolId;

    /**
     * Nueva contraseña (opcional).
     *
     * IMPORTANTE:
     * - Solo se actualiza si se envía un valor no nulo y no vacío
     * - Se encriptará con BCrypt antes de guardar
     *
     * VALIDACIONES:
     * - @Size(min = 6): Si se envía, debe tener al menos 6 caracteres
     */
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String passwordNuevo;
}
