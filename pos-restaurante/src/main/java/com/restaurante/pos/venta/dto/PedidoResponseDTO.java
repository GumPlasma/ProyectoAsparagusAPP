package com.restaurante.pos.venta.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO (DATA TRANSFER OBJECT) - RESPUESTA DE PEDIDO
 * ================================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos completos de un pedido desde el servidor
 * hacia el cliente como respuesta a una consulta o creación.
 *
 * ¿QUÉ REPRESENTA?
 * Es la representación completa de un pedido para mostrar en la interfaz,
 * incluyendo información de la mesa, la venta asociada y el estado actual.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita exponer la entidad JPA Pedido directamente.
 * - Incluye DTOs anidados para entidades relacionadas (MesaSimpleDTO, VentaResponseDTO).
 * - Estructura los datos de forma óptima para el frontend.
 */
@Data
public class PedidoResponseDTO {

    /** Identificador único del pedido. */
    private Long id;

    /** Número de pedido visible (ej: P001-000456). */
    private Integer numeroPedido;

    /** Fecha y hora en que se inició el pedido. */
    private LocalDateTime fechaInicio;

    /** Fecha y hora en que se cerró el pedido (null si aún está activo). */
    private LocalDateTime fechaCierre;

    /** Estado actual del pedido (PENDIENTE, ENVIADO_COCINA, EN_PREPARACION, LISTO, COMPLETADO). */
    private String estado;

    /** Cantidad de personas en la mesa. */
    private Integer cantidadPersonas;

    /** Nombre del cliente que realizó el pedido. */
    private String nombreCliente;

    /** Observaciones generales del pedido. */
    private String observaciones;

    /** Datos simplificados de la mesa asociada. */
    private MesaSimpleDTO mesa;

    /** Datos de la venta generada a partir de este pedido (null si aún no se convierte en venta). */
    private VentaResponseDTO venta;

    /** Indica si el registro está activo en el sistema. */
    private Boolean activo;
}
