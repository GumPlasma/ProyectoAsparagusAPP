package com.restaurante.pos.venta.dto;

import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - MESA SIMPLIFICADA
 * ==============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar una versión mínima de los datos de una mesa.
 *
 * ¿QUÉ REPRESENTA?
 * Es una versión reducida de la mesa que solo incluye los datos esenciales
 * para referenciarla en otros DTOs (como VentaResponseDTO o PedidoResponseDTO).
 *
 * ¿POR QUÉ EXISTE?
 * - Evita incluir todos los campos de una mesa cuando solo se necesita identificarla.
 * - Reduce el tamaño de las respuestas JSON al anidar este DTO.
 * - Facilita la lectura y mantenimiento del código.
 */
@Data
public class MesaSimpleDTO {

    /** Identificador único de la mesa. */
    private Long id;

    /** Número visible de la mesa. */
    private Integer numero;

    /** Ubicación descriptiva de la mesa. */
    private String ubicacion;
}
