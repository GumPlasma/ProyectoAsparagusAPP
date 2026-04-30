package com.restaurante.pos.venta.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO (DATA TRANSFER OBJECT) - RESPUESTA DE DETALLE VENTA
 * =======================================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos de una línea de producto vendido
 * como parte de la respuesta de una venta.
 *
 * ¿QUÉ REPRESENTA?
 * Es la representación de un producto individual dentro de una venta completa,
 * lista para ser mostrada en la interfaz del POS o en un comprobante.
 *
 * ¿POR QUÉ EXISTE?
 * - Se usa dentro de la lista {@link VentaResponseDTO#detalles}.
 * - Evita exponer la entidad JPA DetalleVenta directamente.
 * - Incluye un DTO simplificado del producto en lugar de la entidad completa.
 */
@Data
public class DetalleVentaResponseDTO {

    /** Identificador único del detalle. */
    private Long id;

    /** Cantidad de unidades vendidas. */
    private Integer cantidad;

    /** Precio unitario al momento de la venta. */
    private BigDecimal precioUnitario;

    /** Descuento aplicado a esta línea. */
    private BigDecimal descuento;

    /** Subtotal de esta línea (cantidad × precio - descuento). */
    private BigDecimal subtotal;

    /** Notas especiales del producto. */
    private String notas;

    /** Estado de preparación en cocina (PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO). */
    private String estadoPreparacion;

    /** Datos simplificados del producto vendido. */
    private ProductoVentaDTO producto;
}
