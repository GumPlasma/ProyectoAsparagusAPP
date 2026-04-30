package com.restaurante.pos.proveedor.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO DE FILTRADO DE FACTURAS PROVEEDOR
 * =====================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los criterios de búsqueda/filtrado para consultar facturas de proveedor.
 *
 * ¿QUÉ ES?
 * - Objeto que representa los parámetros de filtrado que puede enviar el frontend.
 * - Todos los campos son opcionales (pueden ser null); si un campo es null, se ignora en el filtro.
 *
 * DIFERENCIA CON DTOs DE CREACIÓN:
 * - Este DTO NO tiene validaciones obligatorias porque todos los campos son opcionales.
 * - Su propósito es consultar, no modificar datos.
 *
 * EJEMPLO DE USO EN JSON:
 * {
 *   "proveedorId": 1,
 *   "numeroFactura": "F001",
 *   "fechaDesde": "2024-01-01",
 *   "fechaHasta": "2024-12-31",
 *   "estado": "PENDIENTE"
 * }
 *
 * EJEMPLO DE USO PARCIAL:
 * {
 *   "estado": "PROCESADA"
 * }
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class FiltroFacturaDTO {

    /**
     * ID del proveedor por el cual filtrar.
     * Si es null, no se filtra por proveedor.
     */
    private Long proveedorId;

    /**
     * Texto a buscar en el número de factura.
     * Si es null, no se filtra por número.
     * La búsqueda es parcial (LIKE).
     */
    private String numeroFactura;

    /**
     * Fecha inicial del rango de búsqueda.
     * Si es null, no se aplica límite inferior.
     */
    private LocalDate fechaDesde;

    /**
     * Fecha final del rango de búsqueda.
     * Si es null, no se aplica límite superior.
     */
    private LocalDate fechaHasta;

    /**
     * Estado de la factura por el cual filtrar.
     * Valores posibles: PENDIENTE, PROCESADA, ANULADA.
     * Si es null, no se filtra por estado.
     */
    private String estado;
}
