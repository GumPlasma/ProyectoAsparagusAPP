package com.restaurante.pos.proveedor.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA ACTUALIZAR PROVEEDOR
 * =============================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos para modificar un proveedor existente.
 *
 * ¿QUÉ ES?
 * - Objeto de entrada que el frontend envía al backend para actualizar un proveedor.
 * - A diferencia de CrearProveedorDTO, TODOS los campos son opcionales aquí.
 * - Si un campo es null, significa que no se desea modificar ese valor.
 *
 * ¿POR QUÉ TODOS OPCIONALES?
 * - Permite actualizaciones parciales (PATCH-like) sin necesidad de enviar todos los datos.
 * - El frontend solo envía los campos que el usuario modificó.
 * - El servicio verifica campo por campo si es null antes de aplicar el cambio.
 *
 * VALIDACIONES:
 * - Se aplican solo si el campo viene en la petición (no es null).
 * - @Email valida formato de correo si se proporciona.
 * - @Size limita longitudes si se proporciona el campo.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class ActualizarProveedorDTO {

    /**
     * RUC o NIT del proveedor.
     * Si es null, no se modifica el RUC actual.
     */
    private String rucNit;

    /**
     * Razón social o nombre comercial.
     * Si es null, no se modifica la razón social actual.
     *
     * VALIDACIÓN:
     * - @Size(max = 200): Máximo 200 caracteres si se proporciona.
     */
    @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
    private String razonSocial;

    /**
     * Nombre del contacto.
     * Si es null, no se modifica.
     */
    private String nombreContacto;

    /**
     * Teléfono.
     * Si es null, no se modifica.
     */
    private String telefono;

    /**
     * Correo electrónico.
     * Si es null, no se modifica.
     *
     * VALIDACIÓN:
     * - @Email: Debe tener formato válido si se proporciona.
     */
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Dirección física.
     * Si es null, no se modifica.
     */
    private String direccion;

    /**
     * Sitio web.
     * Si es null, no se modifica.
     */
    private String sitioWeb;

    /**
     * Notas adicionales.
     * Si es null, no se modifica.
     */
    private String notas;
}
