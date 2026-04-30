package com.restaurante.pos.producto.dto;

import lombok.Data;

/**
 * DTO PARA RESPUESTA DE CATEGORÍA
 * ===============================
 *
 * PROPÓSITO:
 * - Define qué datos de la categoría se envían al cliente
 * - Incluye un campo calculado (totalProductos) que no está en la entidad
 *
 * EJEMPLO DE RESPUESTA JSON:
 * {
 *   "id": 1,
 *   "nombre": "Bebidas",
 *   "descripcion": "Bebidas calientes y frías",
 *   "imagenUrl": "/imagenes/bebidas.jpg",
 *   "orden": 1,
 *   "activo": true,
 *   "totalProductos": 15
 * }
 */
@Data
public class CategoriaResponseDTO {

    // ==========================================================================
    // DATOS DE IDENTIFICACIÓN
    // ==========================================================================

    /**
     * ID único de la categoría.
     */
    private Long id;

    // ==========================================================================
    // DATOS DE INFORMACIÓN
    // ==========================================================================

    /**
     * Nombre de la categoría.
     */
    private String nombre;

    /**
     * Descripción de la categoría.
     */
    private String descripcion;

    /**
     * URL de la imagen representativa.
     */
    private String imagenUrl;

    /**
     * Orden de aparición en el menú.
     */
    private Integer orden;

    // ==========================================================================
    // DATOS DE ESTADO
    // ==========================================================================

    /**
     * Estado de la categoría.
     * true = activa, false = eliminada (borrado lógico)
     */
    private Boolean activo;

    // ==========================================================================
    // DATOS CALCULADOS (no están en la entidad)
    // ==========================================================================

    /**
     * Cantidad de productos activos en esta categoría.
     * Se calcula en el service al convertir la entidad a DTO.
     *
     * EJEMPLO:
     * Si la categoría "Bebidas" tiene 15 productos activos:
     * "totalProductos": 15
     */
    private Integer totalProductos;
}
