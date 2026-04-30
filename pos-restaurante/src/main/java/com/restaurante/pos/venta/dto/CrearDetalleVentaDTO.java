package com.restaurante.pos.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO (DATA TRANSFER OBJECT) - CREAR DETALLE DE VENTA
 * ===================================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos de un producto individual dentro de una venta
 * desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Representa una línea de producto que se incluirá en una venta nueva.
 * Indica qué producto se vende, en qué cantidad, con qué descuento y notas.
 *
 * ¿POR QUÉ EXISTE?
 * - Se usa dentro de la lista {@link CrearVentaDTO#detalles}.
 * - Permite validar cada línea de producto individualmente.
 * - Separa la estructura de entrada de la entidad DetalleVenta.
 *
 * ANOTACIONES DE VALIDACIÓN:
 * - @NotNull: Campos obligatorios.
 * - @Min: Cantidad mínima válida.
 * - @DecimalMin: Valor mínimo no negativo.
 */
@Data
public class CrearDetalleVentaDTO {

    /**
     * Identificador del producto a vender.
     * Obligatorio: cada línea debe referirse a un producto existente.
     */
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    /**
     * Cantidad de unidades del producto.
     * Obligatorio, mínimo 1.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /**
     * Descuento aplicado a esta línea específica.
     * No puede ser negativo.
     * Valor por defecto: 0.
     */
    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
    private BigDecimal descuento = BigDecimal.ZERO;

    /** Notas especiales para esta línea (ej: "Sin cebolla", "Bien cocido"). */
    private String notas;
}
