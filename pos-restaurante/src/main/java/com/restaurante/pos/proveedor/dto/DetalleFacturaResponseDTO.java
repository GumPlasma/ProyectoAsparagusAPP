package com.restaurante.pos.proveedor.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO DE RESPUESTA DE DETALLE DE FACTURA
 * ======================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos de una línea de producto dentro de una factura de compra.
 *
 * ¿QUÉ ES?
 * - Representa UN producto comprado, como parte de la respuesta completa de una factura.
 * - Se usa como elemento de la lista "detalles" dentro de {@link FacturaResponseDTO}.
 *
 * DATOS INCLUIDOS:
 * - Cantidad, precio unitario y subtotal de la línea.
 * - Descripción adicional opcional.
 * - Datos resumidos del producto (anidados como ProductoSimpleDTO).
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class DetalleFacturaResponseDTO {

    /**
     * Identificador único del detalle.
     */
    private Long id;

    /**
     * Cantidad comprada de este producto.
     */
    private Integer cantidad;

    /**
     * Precio unitario de compra.
     */
    private BigDecimal precioUnitario;

    /**
     * Subtotal de la línea (cantidad × precio unitario).
     */
    private BigDecimal subtotal;

    /**
     * Descripción adicional opcional.
     * Ejemplo: presentación, número de lote, fecha de vencimiento.
     */
    private String descripcionAdicional;

    /**
     * Datos resumidos del producto comprado.
     * Usa ProductoSimpleDTO para no enviar todos los campos del producto.
     */
    private ProductoSimpleDTO producto;
}
