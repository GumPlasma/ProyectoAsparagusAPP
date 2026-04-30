package com.restaurante.pos.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO DE KARDEX DE PRODUCTO
 * =========================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Agrupar toda la información del kardex (libro de movimientos)
 * de un producto en una única estructura de respuesta.
 *
 * QUÉ ES UN KARDEX:
 * - Es un registro contable que muestra el historial detallado de un producto:
 *   sus entradas, salidas, stock actual y valorización.
 * - Es un documento esencial para auditorías de inventario y control interno.
 *
 * QUÉ CONTIENE ESTE DTO:
 * - Información del producto.
 * - Lista cronológica de todos sus movimientos.
 * - Stock actual calculado.
 * - Valor monetario del stock (cantidad * precio promedio).
 *
 * POR QUÉ EXISTE:
 * - Permite obtener en UNA sola petición toda la información necesaria
 *   para generar un reporte de kardex completo.
 * - Evita que el frontend tenga que hacer múltiples consultas separadas.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class KardexDTO {

    /** Datos del producto al que pertenece este kardex. */
    private ProductoInventarioDTO producto;

    /** Lista completa de movimientos del producto, ordenados por fecha descendente. */
    private List<MovimientoResponseDTO> movimientos;

    /** Stock actual del producto al momento de la consulta. */
    private Integer stockActual;

    /** Valor monetario total del stock: stockActual * precioPromedio. */
    private BigDecimal valorStock;
}
