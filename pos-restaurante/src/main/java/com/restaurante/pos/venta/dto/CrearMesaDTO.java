package com.restaurante.pos.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - CREAR MESA
 * =======================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos necesarios para crear una nueva mesa
 * desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene la información mínima requerida para registrar una mesa en el sistema.
 *
 * ¿POR QUÉ EXISTE?
 * - Separa la estructura de entrada de la entidad Mesa.
 * - Permite validar que el número de mesa sea obligatorio y válido.
 * - Facilita la creación de mesas desde la interfaz de administración.
 *
 * ANOTACIONES DE VALIDACIÓN:
 * - @NotNull: Campo obligatorio.
 * - @Min: Valor mínimo válido (1).
 */
@Data
public class CrearMesaDTO {

    /**
     * Número visible de la mesa.
     * Obligatorio, debe ser al menos 1.
     */
    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número de mesa debe ser al menos 1")
    private Integer numero;

    /**
     * Capacidad máxima de personas.
     * Valor por defecto: 4.
     * Mínimo: 1.
     */
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad = 4;

    /** Ubicación descriptiva de la mesa (ej: "Terraza", "Segundo piso", "Barra"). */
    private String ubicacion;

    /** Notas adicionales sobre la mesa. */
    private String notas;
}
