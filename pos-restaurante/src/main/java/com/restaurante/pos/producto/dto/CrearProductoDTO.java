package com.restaurante.pos.producto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO PARA CREAR PRODUCTO
 * =======================
 *
 * PROPÓSITO:
 * - Define los datos necesarios para crear un nuevo producto
 * - Valida que los datos cumplan las reglas antes de llegar al servicio
 *
 * EJEMPLO DE USO (JSON):
 * {
 *   "codigo": "7701234567890",
 *   "nombre": "Coca-Cola 500ml",
 *   "descripcion": "Gaseosa Coca-Cola 500ml",
 *   "precio": 3500.00,
 *   "costo": 2000.00,
 *   "imagenUrl": "/imagenes/coca-cola.jpg",
 *   "categoriaId": 2,
 *   "disponible": true,
 *   "requierePreparacion": false,
 *   "tiempoPreparacion": null
 * }
 */
@Data
public class CrearProductoDTO {

    // ==========================================================================
    // CAMPOS DE IDENTIFICACIÓN
    // ==========================================================================

    /**
     * Código del producto (código de barras o interno).
     * Opcional, algunos productos pueden no tener código.
     *
     * VALIDACIONES:
     * - @Size(max = 50): Máximo 50 caracteres si se proporciona
     * - No tiene @NotBlank → es opcional
     * - El servicio valida que sea único si se envía
     */
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String codigo;

    // ==========================================================================
    // CAMPOS DE INFORMACIÓN
    // ==========================================================================

    /**
     * Nombre del producto.
     * Ejemplos: "Coca-Cola 500ml", "Hamburguesa Clásica"
     *
     * VALIDACIONES:
     * - @NotBlank: Obligatorio (no puede estar vacío)
     * - @Size(max = 150): Máximo 150 caracteres
     */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    /**
     * Descripción detallada del producto.
     * Puede incluir ingredientes, presentación, etc.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     */
    private String descripcion;

    /**
     * URL de la imagen del producto.
     * Para mostrar en el menú visual.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     */
    private String imagenUrl;

    // ==========================================================================
    // CAMPOS ECONÓMICOS
    // ==========================================================================

    /**
     * Precio de venta al público.
     * Usado para calcular el total de ventas.
     *
     * VALIDACIONES:
     * - @NotNull: Obligatorio
     * - @DecimalMin(0.01): Debe ser mayor a 0 (no puede ser gratis)
     *
     * TIPO: BigDecimal (nunca usar double/float para dinero)
     * - Evita errores de redondeo
     * - Precisión exacta para cálculos monetarios
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    /**
     * Costo del producto (lo que cuesta adquirirlo).
     * Usado para calcular el margen de ganancia.
     *
     * VALIDACIONES:
     * - @DecimalMin(0.00): No puede ser negativo
     * - No es @NotNull → opcional (puede no tener costo registrado)
     */
    @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
    private BigDecimal costo;

    // ==========================================================================
    // CAMPOS DE CONFIGURACIÓN
    // ==========================================================================

    /**
     * ID de la categoría del producto.
     * Cada producto debe pertenecer a una categoría.
     *
     * VALIDACIONES:
     * - @NotNull: Obligatorio
     * - El servicio valida que la categoría exista
     */
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    /**
     * Indica si el producto está disponible para la venta.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     * - Valor por defecto: true
     *
     * true = disponible para venta
     * false = temporalmente no disponible (sin stock, etc.)
     */
    private Boolean disponible = true;

    /**
     * Indica si el producto requiere preparación en cocina.
     *
     * VALIDACIONES:
     * - Ninguna → opcional
     * - Valor por defecto: true
     *
     * true = requiere preparación (platos, comidas)
     * false = solo se entrega (bebidas embotelladas, snacks)
     */
    private Boolean requierePreparacion = true;

    /**
     * Tiempo estimado de preparación en minutos.
     * Solo aplica si requierePreparacion = true.
     *
     * VALIDACIONES:
     * - @Min(0): No puede ser negativo
     * - null = tiempo no especificado
     */
    @Min(value = 0, message = "El tiempo de preparación no puede ser negativo")
    private Integer tiempoPreparacion;
}
