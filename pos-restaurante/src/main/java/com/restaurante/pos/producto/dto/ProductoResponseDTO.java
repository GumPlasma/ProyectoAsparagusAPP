package com.restaurante.pos.producto.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO PARA RESPUESTA DE PRODUCTO
 * ==============================
 *
 * PROPÓSITO:
 * - Define qué datos del producto se envían al cliente
 * - Incluye campos calculados como margenGanancia
 * - Usa CategoriaSimpleDTO para no exponer toda la información de la categoría
 *
 * EJEMPLO DE RESPUESTA JSON:
 * {
 *   "id": 1,
 *   "codigo": "7701234567890",
 *   "nombre": "Coca-Cola 500ml",
 *   "descripcion": "Gaseosa Coca-Cola 500ml",
 *   "precio": 3500.00,
 *   "costo": 2000.00,
 *   "margenGanancia": 75.00,
 *   "imagenUrl": "/imagenes/coca-cola.jpg",
 *   "disponible": true,
 *   "requierePreparacion": false,
 *   "tiempoPreparacion": null,
 *   "categoria": {"id": 2, "nombre": "Bebidas"},
 *   "activo": true
 * }
 */
@Data
public class ProductoResponseDTO {

    // ==========================================================================
    // DATOS DE IDENTIFICACIÓN
    // ==========================================================================

    /**
     * ID único del producto.
     */
    private Long id;

    /**
     * Código del producto (código de barras o interno).
     */
    private String codigo;

    // ==========================================================================
    // DATOS DE INFORMACIÓN
    // ==========================================================================

    /**
     * Nombre del producto.
     */
    private String nombre;

    /**
     * Descripción detallada del producto.
     */
    private String descripcion;

    /**
     * URL de la imagen del producto.
     */
    private String imagenUrl;

    // ==========================================================================
    // DATOS ECONÓMICOS
    // ==========================================================================

    /**
     * Precio de venta al público.
     * Usado para calcular el total de ventas.
     */
    private BigDecimal precio;

    /**
     * Costo del producto (lo que cuesta adquirirlo).
     * Usado para calcular la ganancia.
     */
    private BigDecimal costo;

    /**
     * Margen de ganancia en porcentaje.
     * Campo CALCULADO: ((precio - costo) / costo) * 100
     *
     * EJEMPLO:
     * - precio = 3500, costo = 2000
     * - margenGanancia = 75.00 (75% de ganancia)
     */
    private BigDecimal margenGanancia;

    // ==========================================================================
    // DATOS DE OPERACIÓN
    // ==========================================================================

    /**
     * Indica si el producto está disponible para la venta.
     * true = se puede vender, false = temporalmente no disponible
     */
    private Boolean disponible;

    /**
     * Indica si el producto requiere preparación en cocina.
     * true = va a cocina (platos), false = solo se entrega (bebidas)
     */
    private Boolean requierePreparacion;

    /**
     * Tiempo estimado de preparación en minutos.
     * null = no aplica (producto que no requiere preparación)
     */
    private Integer tiempoPreparacion;

    // ==========================================================================
    // DATOS DE RELACIÓN
    // ==========================================================================

    /**
     * Información básica de la categoría.
     * Usa CategoriaSimpleDTO para no exponer datos innecesarios.
     */
    private CategoriaSimpleDTO categoria;

    // ==========================================================================
    // DATOS DE ESTADO
    // ==========================================================================

    /**
     * Estado del producto.
     * true = activo, false = eliminado (borrado lógico)
     */
    private Boolean activo;
}
