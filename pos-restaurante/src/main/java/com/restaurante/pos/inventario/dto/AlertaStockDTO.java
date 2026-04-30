package com.restaurante.pos.inventario.dto;

import lombok.Data;

/**
 * DTO DE ALERTA DE STOCK
 * ======================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Transportar la información de alerta cuando un producto
 * tiene stock bajo o está agotado.
 *
 * QUÉ REPRESENTA:
 * Este DTO se usa en los endpoints /stock-bajo y /agotados para notificar
 * al área de compras o al administrador qué productos requieren atención.
 *
 * POR QUÉ EXISTE:
 * - Proporciona solo la información esencial para una alerta, sin cargar
 *   todo el detalle del inventario completo.
 * - Incluye un mensaje descriptivo que el frontend puede mostrar directamente.
 * - Permite que los dashboards muestren notificaciones de forma eficiente.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class AlertaStockDTO {

    /** Identificador del producto en alerta. */
    private Long productoId;

    /** Código único del producto (SKU o código interno). */
    private String codigo;

    /** Nombre descriptivo del producto. */
    private String nombre;

    /** Cantidad actual disponible en stock. */
    private Integer stockActual;

    /** Umbral mínimo configurado para este producto. */
    private Integer stockMinimo;

    /** Mensaje legible para el usuario: "Stock bajo" o "Sin stock". */
    private String mensaje;
}
