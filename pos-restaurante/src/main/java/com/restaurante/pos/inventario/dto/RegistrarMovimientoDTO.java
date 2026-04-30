package com.restaurante.pos.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA REGISTRAR UN MOVIMIENTO MANUAL
 * =======================================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Recibir y validar los datos necesarios para registrar
 * un movimiento de inventario manual (entrada o salida).
 *
 * QUÉ ES UN DTO DE ENTRADA:
 * - Define la estructura exacta del JSON que el cliente debe enviar.
 * - Aplica validaciones automáticas con Jakarta Bean Validation (@Valid).
 * - Actúa como contrato de la API: el cliente sabe qué campos son obligatorios.
 *
 * VALIDACIONES APLICADAS:
 * - productoId: obligatorio (@NotNull).
 * - tipo: obligatorio y no vacío (@NotBlank).
 * - motivo: obligatorio y no vacío (@NotBlank).
 * - cantidad: obligatoria y mínimo 1 (@NotNull, @Min(1)).
 * - observaciones: opcional.
 *
 * ANOTACIONES:
 * - @Data: Lombok genera getters, setters y métodos auxiliares.
 */
@Data
public class RegistrarMovimientoDTO {

    /** Identificador del producto al que se le aplicará el movimiento. Obligatorio. */
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    /**
     * Tipo de movimiento. Valores válidos: "ENTRADA" o "SALIDA".
     * Se recibe como String y se convierte a enum en el servicio.
     * Obligatorio.
     */
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    /**
     * Motivo del movimiento. Valores válidos: "COMPRA", "VENTA", "MERMA",
     * "AJUSTE", "DEVOLUCION", "TRANSFERENCIA".
     * Se recibe como String y se convierte a enum en el servicio.
     * Obligatorio.
     */
    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    /** Cantidad a mover. Debe ser al menos 1. Obligatoria. */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /** Observaciones adicionales sobre el movimiento. Opcional. */
    private String observaciones;
}
