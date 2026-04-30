package com.restaurante.pos.proveedor.dto;

import lombok.Data;

/**
 * DTO SIMPLIFICADO DE PRODUCTO
 * ============================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar una vista mínima de un producto para anidar en otras respuestas.
 *
 * ¿QUÉ ES?
 * - Versión reducida del producto que solo incluye los datos esenciales.
 * - Se usa para evitar enviar información innecesaria cuando solo se necesita identificar un producto.
 *
 * ¿DÓNDE SE USA?
 * - Anidado dentro de {@link DetalleFacturaResponseDTO} para mostrar qué producto se compró.
 * - En listados donde solo se necesita identificar el producto sin mostrar precios, stock, etc.
 *
 * DIFERENCIA CON ProductoResponseDTO:
 *   ProductoResponseDTO → Datos completos del producto (precio, categoría, disponibilidad, etc.).
 *   ProductoSimpleDTO   → Solo identificación básica (id, código, nombre).
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class ProductoSimpleDTO {

    /**
     * Identificador único del producto.
     */
    private Long id;

    /**
     * Código interno o de barras del producto.
     * Ejemplo: "PROD-001", "1234567890123".
     */
    private String codigo;

    /**
     * Nombre del producto.
     * Ejemplo: "Hamburguesa Clásica", "Coca-Cola 500ml".
     */
    private String nombre;
}
