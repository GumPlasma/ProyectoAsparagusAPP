package com.restaurante.pos.venta.dto;

import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - RESPUESTA DE CLIENTE
 * =================================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos completos de un cliente desde el servidor
 * hacia el cliente como respuesta a una consulta.
 *
 * ¿QUÉ REPRESENTA?
 * Es la representación completa de un cliente registrado en el sistema,
 * incluyendo información de contacto, documentos y estado de fidelización.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita exponer la entidad JPA Cliente directamente.
 * - Incluye campos calculados como 'nombreCompleto' para facilitar la visualización.
 * - Permite mostrar información de fidelización (puntos, cliente frecuente).
 */
@Data
public class ClienteResponseDTO {

    /** Identificador único del cliente. */
    private Long id;

    /** Tipo de documento (DNI, RUC, CE, PASAPORTE). */
    private String tipoDocumento;

    /** Número de documento de identidad. */
    private String numeroDocumento;

    /** Nombre del cliente. */
    private String nombre;

    /** Apellido del cliente. */
    private String apellido;

    /** Nombre completo (nombre + apellido, calculado). */
    private String nombreCompleto;

    /** Teléfono de contacto. */
    private String telefono;

    /** Correo electrónico. */
    private String email;

    /** Dirección del cliente. */
    private String direccion;

    /** Notas adicionales. */
    private String notas;

    /** Puntos acumulados en el programa de fidelización. */
    private Integer puntos;

    /** Indica si el cliente es considerado frecuente. */
    private Boolean clienteFrecuente;

    /** Indica si el registro está activo en el sistema. */
    private Boolean activo;
}
