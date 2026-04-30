package com.restaurante.pos.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - ACTUALIZAR CLIENTE
 * ===============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos para actualizar un cliente existente
 * desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene los campos que pueden modificarse de un cliente ya registrado.
 * Todos los campos son opcionales: solo se actualizan los que se envían.
 *
 * ¿POR QUÉ EXISTE?
 * - Permite actualizaciones parciales (PATCH-like) sin enviar todos los campos.
 * - Separa la estructura de actualización de la entidad Cliente.
 * - Incluye validaciones para mantener la integridad de los datos.
 *
 * ANOTACIONES DE VALIDACIÓN:
 * - @Email: Valida el formato del correo electrónico.
 */
@Data
public class ActualizarClienteDTO {

    /** Tipo de documento (DNI, RUC, CE, PASAPORTE). */
    private String tipoDocumento;

    /** Número de documento de identidad. */
    private String numeroDocumento;

    /** Nombre del cliente. */
    private String nombre;

    /** Apellido del cliente. */
    private String apellido;

    /** Teléfono de contacto. */
    private String telefono;

    /**
     * Correo electrónico.
     * Debe tener formato válido si se proporciona.
     */
    @Email(message = "El email debe ser válido")
    private String email;

    /** Dirección del cliente. */
    private String direccion;

    /** Notas adicionales. */
    private String notas;

    /** Indica si el cliente es considerado frecuente. */
    private Boolean clienteFrecuente;
}
