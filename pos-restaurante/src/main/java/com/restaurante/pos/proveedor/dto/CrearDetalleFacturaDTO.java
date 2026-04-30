package com.restaurante.pos.proveedor.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO PARA CREAR DETALLE DE FACTURA
 * =================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos de una línea de producto dentro de una factura de compra.
 *
 * ¿QUÉ ES?
 * - Representa UN producto comprado dentro de una factura.
 * - Se usa como elemento de la lista "detalles" dentro de {@link CrearFacturaDTO}.
 *
 * INFORMACIÓN QUE CONTIENE:
 * - Producto comprado (referencia por ID).
 * - Cantidad adquirida.
 * - Precio unitario de compra.
 * - Descripción adicional opcional.
 *
 * VALIDACIONES:
 * - @NotNull: Campos obligatorios.
 * - @Min: La cantidad debe ser al menos 1.
 * - @DecimalMin: El precio debe ser mayor a 0.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class CrearDetalleFacturaDTO {

    /**
     * Identificador del producto comprado.
     * Debe existir en el catálogo de productos.
     *
     * VALIDACIÓN:
     * - @NotNull: Obligatorio.
     */
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    /**
     * Cantidad comprada del producto.
     *
     * VALIDACIONES:
     * - @NotNull: Obligatoria.
     * - @Min(value = 1): Debe ser al menos 1 unidad.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /**
     * Precio unitario de compra del producto.
     * Este precio se usa para actualizar el costo y calcular el precio promedio del inventario.
     *
     * VALIDACIONES:
     * - @NotNull: Obligatorio.
     * - @DecimalMin(value = "0.01"): Debe ser mayor a 0.
     */
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;

    /**
     * Descripción adicional opcional para esta línea.
     * Puede usarse para especificar presentación, lote, vencimiento, etc.
     */
    private String descripcionAdicional;
}
