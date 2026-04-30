package com.restaurante.pos.proveedor.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA CREAR PROVEEDOR
 * ========================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos necesarios para registrar un nuevo proveedor.
 *
 * ¿QUÉ ES?
 * - Objeto de entrada que el frontend envía al backend para crear un proveedor.
 * - Contiene validaciones de Bean Validation que se activan con @Valid en el controller.
 *
 * VALIDACIONES INCLUIDAS:
 * - @NotBlank: Campos obligatorios que no pueden estar vacíos.
 * - @Size: Límites de longitud para evitar desbordamiento en la base de datos.
 * - @Email: Valida formato de correo electrónico.
 *
 * NOTA:
 * - Todos los campos son opcionales excepto los marcados con @NotBlank.
 * - El servicio realiza validaciones adicionales de unicidad (RUC, email).
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class CrearProveedorDTO {

    /**
     * RUC o NIT del proveedor.
     * Identificador fiscal del proveedor.
     *
     * VALIDACIÓN:
     * - @Size(max = 20): No puede exceder 20 caracteres.
     */
    @Size(max = 20, message = "El RUC/NIT no puede exceder 20 caracteres")
    private String rucNit;

    /**
     * Razón social o nombre comercial del proveedor.
     * Nombre oficial de la empresa.
     *
     * VALIDACIONES:
     * - @NotBlank: Es obligatorio.
     * - @Size(max = 200): Máximo 200 caracteres.
     */
    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
    private String razonSocial;

    /**
     * Nombre de la persona de contacto dentro del proveedor.
     *
     * VALIDACIÓN:
     * - @Size(max = 150): Máximo 150 caracteres.
     */
    @Size(max = 150, message = "El nombre de contacto no puede exceder 150 caracteres")
    private String nombreContacto;

    /**
     * Teléfono del proveedor o contacto.
     *
     * VALIDACIÓN:
     * - @Size(max = 20): Máximo 20 caracteres.
     */
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    /**
     * Correo electrónico del proveedor.
     *
     * VALIDACIONES:
     * - @Email: Debe tener formato válido de email.
     * - @Size(max = 150): Máximo 150 caracteres.
     */
    @Email(message = "El email debe ser válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;

    /**
     * Dirección física del proveedor.
     *
     * VALIDACIÓN:
     * - @Size(max = 300): Máximo 300 caracteres.
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccion;

    /**
     * Sitio web del proveedor (opcional).
     * Ejemplo: "https://www.proveedor.com".
     */
    private String sitioWeb;

    /**
     * Notas adicionales sobre el proveedor.
     * Información complementaria como condiciones de pago, horarios, etc.
     */
    private String notas;
}
