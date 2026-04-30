package com.restaurante.pos.proveedor.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO PARA CREAR FACTURA DE PROVEEDOR
 * ===================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos necesarios para registrar una factura de compra.
 *
 * ¿QUÉ ES?
 * - Objeto de entrada que representa una factura completa de compra a proveedor.
 * - Incluye los datos de la factura (cabecera) y una lista de detalles (líneas de producto).
 *
 * FLUJO DE USO:
 * 1. El usuario selecciona un proveedor y registra los datos de la factura.
 * 2. Agrega una o más líneas de productos comprados (detalles).
 * 3. El backend valida los datos y crea la factura con sus detalles.
 * 4. Automáticamente se actualiza el inventario con los productos recibidos.
 *
 * VALIDACIONES:
 * - @NotBlank: Campos de texto obligatorios.
 * - @NotNull: Campos numéricos y objetos obligatorios.
 * - @NotEmpty: La lista de detalles debe tener al menos un elemento.
 * - @DecimalMin: Evita valores negativos en porcentajes.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class CrearFacturaDTO {

    /**
     * Número de factura emitido por el proveedor.
     * Debe ser único en el sistema.
     *
     * VALIDACIONES:
     * - @NotBlank: Obligatorio.
     * - @Size(max = 50): Máximo 50 caracteres.
     */
    @NotBlank(message = "El número de factura es obligatorio")
    @Size(max = 50, message = "El número de factura no puede exceder 50 caracteres")
    private String numeroFactura;

    /**
     * Fecha de emisión de la factura según el documento del proveedor.
     *
     * VALIDACIÓN:
     * - @NotNull: Obligatoria.
     */
    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;

    /**
     * Fecha de recepción de la mercadería en el restaurante.
     * Puede ser diferente a la fecha de emisión.
     * Opcional; si es null se asume la fecha actual.
     */
    private LocalDate fechaRecepcion;

    /**
     * Identificador del proveedor al cual se le compró.
     *
     * VALIDACIÓN:
     * - @NotNull: Obligatorio.
     */
    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    /**
     * Porcentaje de impuesto aplicado a la factura (ej: 18% IGV).
     * Si es null, se aplica el valor por defecto del sistema.
     *
     * VALIDACIÓN:
     * - @DecimalMin(value = "0.00"): No puede ser negativo.
     */
    @DecimalMin(value = "0.00", message = "El porcentaje de impuesto no puede ser negativo")
    private BigDecimal porcentajeImpuesto;

    /**
     * Observaciones adicionales sobre la factura.
     * Ejemplo: "Entrega parcial", "Producto en mal estado", etc.
     */
    private String observaciones;

    /**
     * Lista de productos incluidos en la factura.
     * Cada elemento representa una línea de la factura.
     *
     * VALIDACIÓN:
     * - @NotEmpty: Debe tener al menos un detalle.
     */
    @NotEmpty(message = "La factura debe tener al menos un detalle")
    private List<CrearDetalleFacturaDTO> detalles;
}
