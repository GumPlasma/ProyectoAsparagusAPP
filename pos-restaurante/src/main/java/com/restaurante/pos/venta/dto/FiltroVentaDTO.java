package com.restaurante.pos.venta.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO (DATA TRANSFER OBJECT) - FILTRO DE VENTAS
 * =============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los criterios de búsqueda/filtrado para consultar ventas.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene todos los posibles filtros que un usuario puede aplicar al buscar ventas
 * en el historial: fechas, vendedor, cliente, estado, método de pago, tipo de venta.
 *
 * ¿POR QUÉ EXISTE?
 * - Permite encapsular múltiples criterios de búsqueda en un solo objeto.
 * - Facilita la construcción de consultas dinámicas en el backend.
 * - Mejora la legibilidad de los endpoints al recibir un solo objeto en lugar de múltiples parámetros.
 */
@Data
public class FiltroVentaDTO {

    /** Fecha inicial del rango de búsqueda. */
    private LocalDate fechaDesde;

    /** Fecha final del rango de búsqueda. */
    private LocalDate fechaHasta;

    /** Identificador del vendedor para filtrar sus ventas. */
    private Long vendedorId;

    /** Identificador del cliente para filtrar sus compras. */
    private Long clienteId;

    /** Estado de la venta (PENDIENTE, COMPLETADA, ANULADA). */
    private String estado;

    /** Método de pago (EFECTIVO, TARJETA, TRANSFERENCIA). */
    private String metodoPago;

    /** Tipo de venta (LLEVAR, MESA, DELIVERY). */
    private String tipoVenta;
}
