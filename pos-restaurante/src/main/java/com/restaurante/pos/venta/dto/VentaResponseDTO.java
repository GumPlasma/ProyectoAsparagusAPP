package com.restaurante.pos.venta.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (DATA TRANSFER OBJECT) - RESPUESTA DE VENTA
 * ================================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos completos de una venta desde el servidor
 * hacia el cliente (frontend). Se usa como respuesta en endpoints de consulta.
 *
 * ¿QUÉ REPRESENTA?
 * Es la representación completa y "plana" de una venta para mostrar en la interfaz.
 * Incluye información del cliente, vendedor, mesa, detalles y totales.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita exponer directamente la entidad JPA Venta.
 * - Permite estructurar los datos de forma óptima para el frontend.
 * - Incluye DTOs anidados para entidades relacionadas (cliente, vendedor, mesa, detalles).
 */
@Data
public class VentaResponseDTO {

    /** Identificador único de la venta. */
    private Long id;

    /** Número de comprobante (ej: F001-000123). */
    private String numeroComprobante;

    /** Tipo de comprobante (TICKET, BOLETA, FACTURA). */
    private String tipoComprobante;

    /** Fecha de la transacción. */
    private LocalDate fecha;

    /** Hora exacta de la transacción. */
    private LocalDateTime hora;

    /** Tipo de venta (LLEVAR, MESA, DELIVERY). */
    private String tipoVenta;

    /** Estado de la venta (PENDIENTE, COMPLETADA, ANULADA). */
    private String estado;

    /** Subtotal antes de impuestos y descuentos. */
    private BigDecimal subtotal;

    /** Porcentaje de impuesto aplicado (ej: 18.00). */
    private BigDecimal porcentajeImpuesto;

    /** Monto calculado del impuesto. */
    private BigDecimal montoImpuesto;

    /** Descuento global aplicado. */
    private BigDecimal descuento;

    /** Total final a pagar. */
    private BigDecimal total;

    /** Método de pago utilizado. */
    private String metodoPago;

    /** Monto recibido del cliente. */
    private BigDecimal montoRecibido;

    /** Vuelto entregado al cliente. */
    private BigDecimal vuelto;

    /** Observaciones generales de la venta. */
    private String observaciones;

    /** Dirección de entrega (solo delivery/llevar). */
    private String direccionEntrega;

    /** Teléfono de contacto del cliente. */
    private String telefonoContacto;

    /** Datos simplificados del cliente (null si no tiene cliente asociado). */
    private ClienteSimpleDTO cliente;

    /** Datos del vendedor que registró la venta. */
    private VendedorDTO vendedor;

    /** Datos simplificados de la mesa (null si no es venta desde mesa). */
    private MesaSimpleDTO mesa;

    /** Lista de productos vendidos con sus detalles. */
    private List<DetalleVentaResponseDTO> detalles;

    /** Indica si el registro está activo en el sistema. */
    private Boolean activo;
}
