package com.restaurante.pos.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - AGREGAR PRODUCTO A PEDIDO
 * ======================================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos necesarios para agregar un producto
 * a un pedido existente desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene la información mínima requerida para añadir un producto a un pedido:
 * qué producto, en qué cantidad y con qué notas especiales.
 *
 * ¿POR QUÉ EXISTE?
 * - Separa la estructura de entrada de la entidad DetallePedido.
 * - Permite validar que el producto y cantidad sean obligatorios.
 * - Es reutilizable para agregar productos tanto a pedidos nuevos como existentes.
 *
 * ANOTACIONES DE VALIDACIÓN:
 * - @NotNull: Campos obligatorios.
 * - @Min: Cantidad mínima válida (1).
 */
@Data
public class AgregarProductoPedidoDTO {

    /**
     * Identificador del producto a agregar.
     * Obligatorio: se debe especificar qué producto se desea.
     */
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    /**
     * Cantidad de unidades del producto.
     * Obligatorio, mínimo 1.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /** Notas especiales para este producto (ej: "Sin cebolla", "Bien cocido"). */
    private String notas;
}
