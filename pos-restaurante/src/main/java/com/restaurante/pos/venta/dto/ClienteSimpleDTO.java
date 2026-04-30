package com.restaurante.pos.venta.dto;

import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - CLIENTE SIMPLIFICADO
 * =================================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar una versión mínima de los datos de un cliente.
 *
 * ¿QUÉ REPRESENTA?
 * Es una versión reducida del cliente que solo incluye los datos esenciales
 * para mostrar en listados, comprobantes o referencias dentro de otros DTOs.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita incluir todos los campos del cliente cuando solo se necesita identificarlo.
 * - Reduce el tamaño de las respuestas JSON al anidar este DTO en VentaResponseDTO.
 * - Facilita la lectura y mantenimiento del código.
 */
@Data
public class ClienteSimpleDTO {

    /** Identificador único del cliente. */
    private Long id;

    /** Nombre completo del cliente (nombre + apellido). */
    private String nombreCompleto;

    /** Teléfono de contacto. */
    private String telefono;
}
