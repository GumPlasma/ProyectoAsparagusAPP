package com.restaurante.pos.usuario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA CREAR ROL
 * ==================
 *
 * PROPÓSITO:
 * - Define los datos necesarios para crear un nuevo rol
 * - Valida que los datos cumplan las reglas antes de llegar al servicio
 *
 * EJEMPLO DE USO:
 * {
 *   "nombre": "SUPERVISOR",
 *   "descripcion": "Supervisor de turno con permisos limitados"
 * }
 */
@Data
public class CrearRolDTO {

    // ==========================================================================
    // CAMPOS DEL ROL
    // ==========================================================================

    /**
     * Nombre del rol.
     * Debe ser único (se valida en el servicio).
     * Convención: Usar mayúsculas (ADMIN, VENDEDOR, etc.)
     *
     * VALIDACIONES:
     * - @NotBlank: No puede estar vacío
     * - @Size(max = 50): Máximo 50 caracteres (según BD)
     */
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    /**
     * Descripción del rol.
     * Explica los permisos y responsabilidades de este rol.
     *
     * VALIDACIONES:
     * - @Size(max = 255): Máximo 255 caracteres
     * - No tiene @NotBlank → es opcional
     */
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
}