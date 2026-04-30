package com.restaurante.pos.producto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA ACTUALIZAR CATEGORÍA
 * =============================
 *
 * PROPÓSITO:
 * - Define los datos que se pueden modificar de una categoría
 * - Todos los campos son OPCIONALES (solo se actualizan los que se envían)
 *
 * DIFERENCIA CON CrearCategoriaDTO:
 * - No tiene @NotBlank (todos los campos son opcionales)
 * - Solo valida longitudes máximas si se envían valores
 *
 * EJEMPLO DE USO (JSON):
 * {
 *   "nombre": "Bebidas Calientes",  // Si se envía, actualiza el nombre
 *   "orden": 2                       // Si se envía, actualiza el orden
 * }
 */
@Data
public class ActualizarCategoriaDTO {

    // ==========================================================================
    // CAMPOS DE LA CATEGORÍA (OPCIONALES)
    // ==========================================================================

    /**
     * Nuevo nombre de la categoría.
     * Si es null, no se actualiza el nombre.
     *
     * VALIDACIONES:
     * - @Size(max = 100): Solo valida si se envía valor
     * - El servicio valida que el nuevo nombre no esté duplicado
     */
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    /**
     * Nueva descripción de la categoría.
     * Si es null, se mantiene la descripción actual.
     *
     * VALIDACIONES:
     * - @Size(max = 255): Solo valida si se envía valor
     */
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    /**
     * Nueva URL de imagen.
     * Si es null, se mantiene la imagen actual.
     *
     * VALIDACIONES:
     * - Ninguna → completamente opcional
     */
    private String imagenUrl;

    /**
     * Nuevo orden de aparición.
     * Si es null, se mantiene el orden actual.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     */
    private Integer orden;
}
