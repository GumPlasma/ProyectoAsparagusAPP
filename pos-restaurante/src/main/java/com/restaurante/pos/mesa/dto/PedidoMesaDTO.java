package com.restaurante.pos.mesa.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (DATA TRANSFER OBJECT) - PEDIDO DE MESA
 * ===========================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar la información de un pedido individual (línea de producto)
 * dentro de una mesa, tanto de entrada como de salida.
 *
 * ¿QUÉ REPRESENTA?
 * Representa un producto solicitado en una mesa, incluyendo su cantidad,
 * precio unitario, subtotal y notas especiales.
 *
 * ¿POR QUÉ EXISTE?
 * - Se usa para enviar pedidos al crear/agregar productos a una mesa (entrada).
 * - También se usa para devolver la lista de pedidos al consultar una mesa (salida).
 * - Evita circularidad de referencias entre entidades JPA al serializar a JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoMesaDTO {

    /** Identificador único del pedido. */
    private Long id;

    /** Identificador de la mesa a la que pertenece el pedido. */
    private Long mesaId;

    /** Identificador del producto solicitado. */
    private Long productoId;

    /** Nombre del producto (útil para mostrar en la interfaz sin consultar producto). */
    private String productoNombre;

    /** Cantidad de unidades solicitadas. */
    private Integer cantidad;

    /** Precio unitario del producto al momento del pedido. */
    private BigDecimal precioUnitario;

    /** Subtotal calculado (cantidad × precioUnitario). */
    private BigDecimal subtotal;

    /** Notas especiales del pedido (ej: "sin cebolla", "término medio"). */
    private String notas;

    /** Fecha y hora en que se realizó el pedido. */
    private LocalDateTime fechaHora;
}
