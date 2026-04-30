package com.restaurante.pos.producto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO PARA ACTUALIZAR PRODUCTO
 * ============================
 *
 * PROPÓSITO:
 * - Define los datos que se pueden modificar de un producto
 * - Todos los campos son OPCIONALES (solo se actualizan los que se envían)
 *
 * DIFERENCIA CON CrearProductoDTO:
 * - No tiene @NotBlank (todos los campos son opcionales)
 * - Solo valida valores si se envían
 *
 * EJEMPLO DE USO (JSON):
 * {
 *   "precio": 4000.00,        // Actualiza solo el precio
 *   "disponible": false        // Marca como no disponible
 * }
 */
@Data
public class ActualizarProductoDTO {

    // ==========================================================================
    // CAMPOS DE IDENTIFICACIÓN (OPCIONALES)
    // ==========================================================================

    /**
     * Nuevo código del producto.
     * Si es null o vacío, se establece como null.
     * Si es diferente, el servicio valida que no esté duplicado.
     *
     * VALIDACIONES:
     * - Ninguna aquí (se valida en el servicio)
     */
    private String codigo;

    // ==========================================================================
    // CAMPOS DE INFORMACIÓN (OPCIONALES)
    // ==========================================================================

    /**
     * Nuevo nombre del producto.
     * Si es null, se mantiene el nombre actual.
     *
     * VALIDACIONES:
     * - @Size(max = 150): Solo valida si se envía valor
     */
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    /**
     * Nueva descripción del producto.
     * Si es null, se mantiene la descripción actual.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     */
    private String descripcion;

    /**
     * Nueva URL de imagen.
     * Si es null, se mantiene la imagen actual.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     */
    private String imagenUrl;

    // ==========================================================================
    // CAMPOS ECONÓMICOS (OPCIONALES)
    // ==========================================================================

    /**
     * Nuevo precio de venta.
     * Si es null, se mantiene el precio actual.
     *
     * VALIDACIONES:
     * - @DecimalMin(0.01): Si se envía, debe ser mayor a 0
     */
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    /**
     * Nuevo costo del producto.
     * Si es null, se mantiene el costo actual.
     *
     * VALIDACIONES:
     * - @DecimalMin(0.00): Si se envía, no puede ser negativo
     */
    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    private BigDecimal costo;

    // ==========================================================================
    // CAMPOS DE CONFIGURACIÓN (OPCIONALES)
    // ==========================================================================

    /**
     * Nueva categoría del producto.
     * Si es null, se mantiene la categoría actual.
     *
     * VALIDACIONES:
     * - El servicio valida que la categoría exista
     */
    private Long categoriaId;

    /**
     * Estado de disponibilidad.
     * Si es null, se mantiene el estado actual.
     *
     * true = disponible para venta
     * false = temporalmente no disponible
     */
    private Boolean disponible;

    /**
     * Indica si requiere preparación.
     * Si es null, se mantiene el valor actual.
     *
     * true = requiere preparación (platos)
     * false = solo se entrega (bebidas embotelladas)
     */
    private Boolean requierePreparacion;

    /**
     * Nuevo tiempo de preparación en minutos.
     * Si es null, se mantiene el valor actual.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     */
    private Integer tiempoPreparacion;
}