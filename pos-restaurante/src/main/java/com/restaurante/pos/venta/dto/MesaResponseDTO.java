package com.restaurante.pos.venta.dto;

import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - RESPUESTA DE MESA
 * ==============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos completos de una mesa desde el servidor
 * hacia el cliente como respuesta a una consulta.
 *
 * ¿QUÉ REPRESENTA?
 * Es la representación completa de una mesa para mostrar en la interfaz de administración
 * o en el mapa visual del restaurante.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita exponer la entidad JPA Mesa directamente.
 * - Incluye campos calculados como 'disponible' para facilitar la lógica del frontend.
 * - Permite incluir información adicional como el código QR de la mesa.
 */
@Data
public class MesaResponseDTO {

    /** Identificador único de la mesa. */
    private Long id;

    /** Número visible de la mesa. */
    private Integer numero;

    /** Capacidad máxima de personas. */
    private Integer capacidad;

    /** Ubicación descriptiva de la mesa. */
    private String ubicacion;

    /** Estado actual (LIBRE, OCUPADA, RESERVADA, PAGADA). */
    private String estado;

    /** Código QR asociado a la mesa (para escaneo por clientes). */
    private String codigoQr;

    /** Notas adicionales sobre la mesa. */
    private String notas;

    /** Indica si el registro está activo en el sistema. */
    private Boolean activo;

    /** Indica si la mesa está disponible para nuevos clientes (estado = LIBRE o RESERVADA). */
    private Boolean disponible;
}
