package com.restaurante.pos.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - CREAR PEDIDO
 * =========================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos necesarios para crear un nuevo pedido
 * desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene la información inicial para registrar un pedido en el sistema,
 * ya sea para una mesa específica o un pedido general.
 *
 * ¿POR QUÉ EXISTE?
 * - Separa la estructura de entrada de la entidad Pedido.
 * - Permite validar los datos mínimos requeridos para crear un pedido.
 * - Facilita la creación de pedidos sin necesidad de enviar todos los campos de la entidad.
 *
 * ANOTACIONES DE VALIDACIÓN:
 * - @NotNull: Campo obligatorio.
 */
@Data
public class CrearPedidoDTO {

    /**
     * Identificador de la mesa donde se realiza el pedido.
     * Obligatorio: todo pedido debe estar asociado a una mesa.
     */
    @NotNull(message = "La mesa es obligatoria")
    private Long mesaId;

    /** Cantidad de personas en la mesa (opcional, para estadísticas). */
    private Integer cantidadPersonas;

    /** Nombre del cliente que realiza el pedido (opcional). */
    private String nombreCliente;

    /** Observaciones generales del pedido. */
    private String observaciones;
}
