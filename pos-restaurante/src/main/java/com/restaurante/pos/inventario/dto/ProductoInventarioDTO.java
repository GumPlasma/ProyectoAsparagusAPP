package com.restaurante.pos.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO DE PRODUCTO PARA VISTAS DE INVENTARIO
 * =========================================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Proporcionar una vista resumida de un producto
 * para ser incluida dentro de otros DTOs de inventario (evitando
 * exponer la entidad Producto completa).
 *
 * QUÉ REPRESENTA:
 * Los datos mínimos e indispensables de un producto que necesita
 * conocer el módulo de inventario: identificación, precios y disponibilidad.
 *
 * POR QUÉ EXISTE:
 * - Desacopla el módulo de inventario del módulo de productos.
 * - Evita traer campos innecesarios del producto (descripción, categoría,
 *   imágenes) cuando solo se necesita identificarlo en listas de stock.
 * - Previene problemas de serialización JSON por relaciones circulares.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class ProductoInventarioDTO {

    /** Identificador único del producto. */
    private Long id;

    /** Código interno o SKU del producto. */
    private String codigo;

    /** Nombre comercial del producto. */
    private String nombre;

    /** Precio de venta al público. */
    private BigDecimal precio;

    /** Costo de adquisición o producción. */
    private BigDecimal costo;

    /** Indica si el producto está activo y disponible para la venta. */
    private Boolean disponible;
}
