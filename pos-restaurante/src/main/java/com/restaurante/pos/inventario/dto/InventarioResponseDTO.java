package com.restaurante.pos.inventario.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO DE RESPUESTA DE INVENTARIO
 * ==============================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Transportar datos del inventario desde la capa de servicio
 * hacia la capa de presentación o API REST.
 *
 * QUÉ ES UN DTO:
 * - Objeto plano (POJO) que NO contiene lógica de negocio.
 * - Su único propósito es encapsular datos para transferirlos entre capas
 *   o a través de la red (serialización a JSON).
 * - Desacopla la estructura interna de las entidades JPA de lo que expone la API.
 *
 * POR QUÉ EXISTE:
 * - La entidad Inventario puede tener relaciones cíclicas o datos sensibles.
 * - El DTO permite incluir campos calculados (stockBajo, stockAgotado) que no
 *   existen como columnas en la base de datos.
 * - Facilita el versionado de la API sin afectar el modelo de datos.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera automáticamente getters, setters, toString, equals y hashCode.
 */
@Data
public class InventarioResponseDTO {

    /** Identificador único del registro de inventario. */
    private Long id;

    /** Cantidad actual en stock del producto. */
    private Integer cantidad;

    /** Umbral mínimo de stock que dispara alertas de reposición. */
    private Integer stockMinimo;

    /** Límite máximo de stock recomendado para el producto. */
    private Integer stockMaximo;

    /** Ubicación física donde se almacena el producto. Ej: "Estante A-3". */
    private String ubicacion;

    /** Fecha y hora del último movimiento registrado. */
    private LocalDateTime ultimoMovimiento;

    /** Precio promedio ponderado del stock actual (para valorización contable). */
    private BigDecimal precioPromedio;

    /** Indica si el stock actual está en nivel de alerta (cantidad <= stockMinimo). */
    private Boolean stockBajo;

    /** Indica si el producto está completamente agotado (cantidad == 0). */
    private Boolean stockAgotado;

    /** Información resumida del producto asociado a este inventario. */
    private ProductoInventarioDTO producto;
}
