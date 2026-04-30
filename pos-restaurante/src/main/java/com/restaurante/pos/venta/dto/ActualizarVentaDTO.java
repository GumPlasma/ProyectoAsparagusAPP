package com.restaurante.pos.venta.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO (DATA TRANSFER OBJECT) - ACTUALIZAR VENTA
 * =============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos para actualizar una venta existente
 * desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene los campos que pueden modificarse de una venta ya registrada.
 * Todos los campos son opcionales: solo se actualizan los que se envían.
 *
 * ¿POR QUÉ EXISTE?
 * - Permite actualizaciones parciales de una venta (ej: cambiar método de pago, observaciones).
 * - Separa la estructura de actualización de la entidad Venta.
 * - Facilita correcciones menores sin necesidad de reenviar todos los datos.
 */
@Data
public class ActualizarVentaDTO {

    /** Nuevo método de pago (EFECTIVO, TARJETA, TRANSFERENCIA). */
    private String metodoPago;

    /** Nuevo monto recibido del cliente. */
    private BigDecimal montoRecibido;

    /** Nuevas observaciones de la venta. */
    private String observaciones;

    /** Nuevo estado de la venta (PENDIENTE, COMPLETADA, ANULADA). */
    private String estado;
}
