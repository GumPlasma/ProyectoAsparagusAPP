package com.restaurante.pos.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - CREAR CLIENTE
 * ==========================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos necesarios para registrar un nuevo cliente
 * desde el cliente (frontend) hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene la información personal y de contacto de un cliente nuevo.
 *
 * ¿POR QUÉ EXISTE?
 * - Separa la estructura de entrada de la entidad Cliente.
 * - Permite validar formatos de datos (email, tamaños de campos) antes de persistir.
 * - Facilita la creación rápida de clientes desde el punto de venta.
 *
 * ANOTACIONES DE VALIDACIÓN:
 * - @Size: Limita la longitud máxima de campos de texto.
 * - @Email: Valida que el email tenga formato correcto.
 */
@Data
public class CrearClienteDTO {

    /**
     * Tipo de documento de identidad.
     * Valor por defecto: "DNI".
     * Otros valores: "RUC", "CE", "PASAPORTE".
     */
    private String tipoDocumento = "DNI";

    /**
     * Número de documento de identidad.
     * Máximo 20 caracteres.
     */
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    private String numeroDocumento;

    /**
     * Nombre del cliente.
     * Máximo 150 caracteres.
     */
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    /** Apellido del cliente. */
    private String apellido;

    /**
     * Teléfono de contacto.
     * Máximo 20 caracteres.
     */
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    /**
     * Correo electrónico del cliente.
     * Debe tener formato válido de email.
     */
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Dirección del cliente.
     * Máximo 300 caracteres.
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccion;

    /** Notas adicionales sobre el cliente. */
    private String notas;
}
