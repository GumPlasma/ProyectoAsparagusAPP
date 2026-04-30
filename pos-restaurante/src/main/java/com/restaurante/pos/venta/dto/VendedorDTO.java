package com.restaurante.pos.venta.dto;

import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - VENDEDOR
 * =====================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar una versión mínima de los datos de un vendedor (usuario)
 * cuando se incluye dentro de una venta o reporte.
 *
 * ¿QUÉ REPRESENTA?
 * Es una versión reducida del usuario/vendedor que solo incluye los datos esenciales
 * para identificarlo en el contexto de una venta: ID, username y nombre completo.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita incluir datos sensibles del usuario (contraseña, roles, permisos)
 *   cuando solo se necesita mostrar quién realizó la venta.
 * - Reduce el tamaño de las respuestas JSON al anidar este DTO en VentaResponseDTO.
 * - Facilita la lectura y mantenimiento del código.
 */
@Data
public class VendedorDTO {

    /** Identificador único del vendedor. */
    private Long id;

    /** Nombre de usuario (login) del vendedor. */
    private String username;

    /** Nombre completo del vendedor. */
    private String nombreCompleto;
}
