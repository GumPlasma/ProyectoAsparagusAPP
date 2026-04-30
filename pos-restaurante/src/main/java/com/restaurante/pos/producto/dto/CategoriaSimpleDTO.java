package com.restaurante.pos.producto.dto;

import lombok.Data;

/**
 * DTO SIMPLE DE CATEGORÍA
 * =======================
 *
 * PROPÓSITO:
 * - Versión simplificada de categoría para usar como objeto anidado
 * - Solo incluye información básica (id y nombre)
 * - Evita exponer datos innecesarios cuando se lista un producto
 *
 * SE USA EN:
 * - ProductoResponseDTO (para mostrar la categoría de un producto)
 * - Otras listas donde solo se necesita referencia básica
 *
 * EJEMPLO DE USO:
 * {
 *   "id": 2,
 *   "nombre": "Bebidas"
 * }
 */
@Data
public class CategoriaSimpleDTO {

    // ==========================================================================
    // DATOS BÁSICOS DE LA CATEGORÍA
    // ==========================================================================

    /**
     * ID único de la categoría.
     */
    private Long id;

    /**
     * Nombre de la categoría.
     * Suficiente para identificar la categoría en la UI.
     */
    private String nombre;

    // NOTA: No incluye descripcion, imagenUrl, orden, etc.
    // porque no son necesarios cuando se muestra como referencia
}
