package com.restaurante.pos.proveedor.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO DE RESPUESTA DE FACTURA PROVEEDOR
 * =====================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos completos de una factura de compra hacia el frontend.
 *
 * ¿QUÉ ES?
 * - Objeto de salida que representa una factura ya registrada en el sistema.
 * - Incluye tanto la cabecera como los detalles de la factura.
 * - Incluye información enriquecida del proveedor (anidada como ProveedorSimpleDTO).
 *
 * DATOS INCLUIDOS:
 * - Información general de la factura (número, fechas, totales).
 * - Estado de la factura (PENDIENTE, PROCESADA, ANULADA).
 * - Proveedor asociado (resumido).
 * - Lista de productos comprados con sus montos.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class FacturaResponseDTO {

    /**
     * Identificador único de la factura en el sistema.
     */
    private Long id;

    /**
     * Número de factura según el documento del proveedor.
     * Ejemplo: "F001-00001234".
     */
    private String numeroFactura;

    /**
     * Fecha de emisión de la factura.
     */
    private LocalDate fechaEmision;

    /**
     * Fecha de recepción de la mercadería en el restaurante.
     */
    private LocalDate fechaRecepcion;

    /**
     * Subtotal antes de impuestos (suma de subtotales de detalles).
     */
    private BigDecimal subtotal;

    /**
     * Porcentaje de impuesto aplicado (ej: 18.00 para 18%).
     */
    private BigDecimal porcentajeImpuesto;

    /**
     * Monto calculado del impuesto.
     */
    private BigDecimal montoImpuesto;

    /**
     * Total de la factura (subtotal + impuesto).
     */
    private BigDecimal total;

    /**
     * Estado actual de la factura.
     * Valores posibles: PENDIENTE, PROCESADA, ANULADA.
     */
    private String estado;

    /**
     * Observaciones adicionales registradas.
     */
    private String observaciones;

    /**
     * Datos resumidos del proveedor asociado.
     * Usa ProveedorSimpleDTO para no enviar datos innecesarios.
     */
    private ProveedorSimpleDTO proveedor;

    /**
     * Lista de líneas de producto incluidas en la factura.
     * Cada elemento es un DetalleFacturaResponseDTO.
     */
    private List<DetalleFacturaResponseDTO> detalles;

    /**
     * Estado activo de la factura.
     * true = vigente; false = eliminada lógicamente.
     */
    private Boolean activo;
}
