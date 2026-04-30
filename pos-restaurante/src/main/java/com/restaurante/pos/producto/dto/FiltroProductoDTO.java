package com.restaurante.pos.producto.dto;

import lombok.Data;

/**
 * DTO DE FILTRADO DE PRODUCTOS
 * ============================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los criterios de búsqueda/filtrado para consultar productos.
 *
 * ¿QUÉ ES?
 * - Objeto que representa los parámetros de filtrado que puede enviar el frontend.
 * - Todos los campos son opcionales (pueden ser null); si un campo es null, se ignora en el filtro.
 *
 * DIFERENCIA CON DTOs DE CREACIÓN/ACTUALIZACIÓN:
 * - Los DTOs de creación/actualización tienen validaciones @NotNull, @NotBlank, etc.
 * - Este DTO NO tiene validaciones obligatorias porque todos los campos son opcionales.
 * - Su propósito es consultar, no modificar datos.
 *
 * EJEMPLO DE USO EN JSON:
 * {
 *   "nombre": "hamburguesa",
 *   "categoriaId": 1,
 *   "disponible": true,
 *   "activo": true
 * }
 *
 * EJEMPLO DE USO PARCIAL (solo algunos filtros):
 * {
 *   "categoriaId": 2,
 *   "disponible": true
 * }
 *
 * NOTA DE IMPLEMENTACIÓN:
 * - Este DTO puede integrarse con JpaSpecificationExecutor o QueryDSL para construir
 *   consultas dinámicas con predicados opcionales.
 * - Alternativamente, el Service puede verificar campo por campo si es null antes de aplicar el filtro.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode automáticamente.
 */
@Data
public class FiltroProductoDTO {

    /**
     * Texto a buscar en el nombre o descripción del producto.
     * Si es null, no se filtra por nombre.
     * Ejemplo: "hamburguesa" → buscará productos que contengan esa palabra.
     */
    private String nombre;

    /**
     * ID de la categoría por la cual filtrar.
     * Si es null, no se filtra por categoría.
     * Ejemplo: 1 → mostrará solo productos de la categoría con ID 1.
     */
    private Long categoriaId;

    /**
     * Filtra productos según su disponibilidad para la venta.
     * true  = solo productos disponibles.
     * false = solo productos no disponibles.
     * null  = no filtra por disponibilidad.
     */
    private Boolean disponible;

    /**
     * Filtra productos según su estado activo/inactivo.
     * true  = solo productos activos.
     * false = solo productos inactivos (eliminados lógicamente).
     * null  = no filtra por estado.
     */
    private Boolean activo;
}
