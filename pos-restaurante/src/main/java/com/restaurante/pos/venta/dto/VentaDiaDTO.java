package com.restaurante.pos.venta.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO (DATA TRANSFER OBJECT) - VENTAS POR DÍA
 * ===========================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar el resumen de ventas de un día específico.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene la cantidad de ventas y el monto total de un día particular.
 * Se usa típicamente para gráficos de tendencia y reportes diarios.
 *
 * ¿POR QUÉ EXISTE?
 * - Permite construir gráficos de ventas por día (tendencias semanales/mensuales).
 * - Es más ligero que enviar la lista completa de ventas de cada día.
 * - Facilita la comparación entre diferentes días.
 */
@Data
public class VentaDiaDTO {

    /** Fecha del día resumido. */
    private LocalDate fecha;

    /** Cantidad de ventas realizadas ese día. */
    private Integer cantidadVentas;

    /** Monto total vendido ese día. */
    private BigDecimal totalVentas;
}
