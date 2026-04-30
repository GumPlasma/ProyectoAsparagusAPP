package com.restaurante.pos.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO DE RESUMEN DE INVENTARIO
 * ============================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Transportar métricas agregadas del estado general
 * del inventario para paneles de control y reportes administrativos.
 *
 * QUÉ REPRESENTA:
 * Un snapshot numérico del inventario: cuántos productos hay, cuántos
 * tienen stock, cuántos están bajos o agotados, y el valor monetario total.
 *
 * POR QUÉ EXISTE:
 * - Los dashboards no necesitan la lista completa de productos; solo
   * necesitan conteos y totales para mostrar tarjetas y gráficas.
 * - Reduce el tráfico de red al enviar solo 5 números en lugar de
 *   cientos de registros de inventario completos.
 * - Facilita la generación de reportes ejecutivos.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class ResumenInventarioDTO {

    /** Total de productos activos con registro de inventario. */
    private Integer totalProductos;

    /** Cantidad de productos que tienen stock disponible (cantidad > 0). */
    private Integer productosConStock;

    /** Cantidad de productos con stock en nivel de alerta (<= stock mínimo). */
    private Integer productosStockBajo;

    /** Cantidad de productos completamente agotados (cantidad = 0). */
    private Integer productosAgotados;

    /** Valor monetario total del inventario: SUM(cantidad * precioPromedio). */
    private BigDecimal valorTotalInventario;
}
