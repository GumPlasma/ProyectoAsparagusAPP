package com.restaurante.pos.venta.dto;

import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - PRODUCTO EN VENTA
 * ==============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar una versión mínima de los datos de un producto
 * cuando se incluye dentro de una venta o detalle de venta.
 *
 * ¿QUÉ REPRESENTA?
 * Es una versión reducida del producto que solo incluye los datos esenciales
 * para identificarlo en el contexto de una venta: ID, código y nombre.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita incluir todos los campos del producto (precio de costo, stock, etc.)
 *   cuando solo se necesita identificarlo en un comprobante o lista.
 * - Reduce el tamaño de las respuestas JSON al anidar este DTO.
 * - Facilita la lectura y mantenimiento del código.
 */
@Data
public class ProductoVentaDTO {

    /** Identificador único del producto. */
    private Long id;

    /** Código interno del producto (ej: PRD001). */
    private String codigo;

    /** Nombre descriptivo del producto. */
    private String nombre;
}
