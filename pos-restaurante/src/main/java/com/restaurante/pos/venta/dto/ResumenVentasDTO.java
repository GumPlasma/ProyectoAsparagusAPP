package com.restaurante.pos.venta.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO (DATA TRANSFER OBJECT) - RESUMEN DE VENTAS
 * ==============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar un resumen agregado de ventas para reportes
 * y dashboards del restaurante.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene métricas consolidadas de un período de ventas: cantidad total,
 * montos por método de pago, descuentos y ticket promedio.
 *
 * ¿POR QUÉ EXISTE?
 * - Agrupa métricas clave en un solo objeto para facilitar la visualización en dashboards.
 * - Evita enviar listas completas de ventas cuando solo se necesitan totales.
 * - Permite calcular KPIs (indicadores clave de rendimiento) del negocio.
 */
@Data
public class ResumenVentasDTO {

    /** Cantidad total de ventas en el período. */
    private Integer totalVentas;

    /** Monto total vendido (suma de todos los totales). */
    private BigDecimal montoTotal;

    /** Monto total recaudado en efectivo. */
    private BigDecimal montoEfectivo;

    /** Monto total recaudado con tarjeta. */
    private BigDecimal montoTarjeta;

    /** Monto total de descuentos aplicados. */
    private BigDecimal montoDescuentos;

    /** Ticket promedio (montoTotal / totalVentas). */
    private BigDecimal promedioPorVenta;
}
