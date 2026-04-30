package com.restaurante.pos.producto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA CREAR CATEGORÍA
 * ========================
 *
 * PROPÓSITO:
 * - Define los datos necesarios para crear una nueva categoría de productos
 * - Valida que los datos cumplan las reglas antes de llegar al servicio
 *
 * EJEMPLO DE USO (JSON):
 * {
 *   "nombre": "Bebidas",
 *   "descripcion": "Bebidas calientes y frías",
 *   "imagenUrl": "/imagenes/bebidas.jpg",
 *   "orden": 1
 * }
 */
@Data
public class CrearCategoriaDTO {

    // ==========================================================================
    // CAMPOS DE LA CATEGORÍA
    // ==========================================================================

    /**
     * Nombre de la categoría.
     * Debe ser único (se valida en el servicio).
     *
     * VALIDACIONES:
     * - @NotBlank: No puede estar vacío o ser null
     * - @Size(max = 100): Máximo 100 caracteres (según BD)
     *
     * EJEMPLOS:
     * - "Bebidas"
     * - "Platos Principales"
     * - "Postres"
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    /**
     * Descripción opcional de la categoría.
     * Explica qué tipo de productos contiene.
     *
     * VALIDACIONES:
     * - @Size(max = 255): Máximo 255 caracteres si se proporciona
     * - No tiene @NotBlank → es opcional
     */
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    /**
     * URL de la imagen representativa.
     * Para mostrar en el menú visual.
     *
     * VALIDACIONES:
     * - Ninguna → completamente opcional
     *
     * EJEMPLO: "/imagenes/categorias/bebidas.jpg"
     */
    private String imagenUrl;

    /**
     * Orden de aparición en el menú.
     * Las categorías con orden menor aparecen primero.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     * - Si es null, el servicio asigna 0 por defecto
     *
     * EJEMPLO:
     * - 1 = Entradas (primero)
     * - 2 = Platos Principales
     * - 3 = Postres (último)
     */
    private Integer orden;
}
