package com.restaurante.pos.venta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO (DATA TRANSFER OBJECT) - CREAR VENTA
 * ========================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos necesarios para crear una nueva venta
 * desde el cliente (frontend) hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene toda la información que el usuario (cajero/vendedor) ingresa al registrar
 * una venta directa (para llevar, delivery, etc.), excluyendo las ventas que se generan
 * automáticamente desde el módulo de mesas.
 *
 * ¿POR QUÉ EXISTE?
 * - Separa la estructura de entrada de la entidad Venta final.
 * - Permite validar los datos antes de procesarlos con anotaciones de Bean Validation.
 * - Incluye la lista de productos (detalles) que conforman la venta.
 *
 * ANOTACIONES DE VALIDACIÓN (Bean Validation):
 * - @NotNull: El campo no puede ser null.
 * - @NotEmpty: La lista no puede ser null ni vacía.
 * - @DecimalMin: El valor numérico no puede ser menor al especificado.
 * - @Min: El valor entero no puede ser menor al especificado.
 */
@Data
public class CrearVentaDTO {

    /** Identificador del cliente (opcional). Si es null, la venta no tiene cliente asociado. */
    private Long clienteId;

    /** Identificador de la mesa (opcional). Solo aplica para ventas tipo "MESA". */
    private Long mesaId;

    /**
     * Identificador del vendedor que registra la venta.
     * Obligatorio: toda venta debe tener un vendedor responsable.
     */
    @NotNull(message = "El vendedor es obligatorio")
    private Long vendedorId;

    /**
     * Tipo de venta.
     * Valores: "LLEVAR", "MESA", "DELIVERY".
     * Valor por defecto: "LLEVAR".
     */
    private String tipoVenta = "LLEVAR";

    /**
     * Tipo de comprobante a emitir.
     * Valores: "TICKET", "BOLETA", "FACTURA".
     * Valor por defecto: "TICKET".
     */
    private String tipoComprobante = "TICKET";

    /**
     * Método de pago utilizado.
     * Valores: "EFECTIVO", "TARJETA", "TRANSFERENCIA".
     * Valor por defecto: "EFECTIVO".
     */
    private String metodoPago = "EFECTIVO";

    /**
     * Descuento global aplicado a la venta.
     * No puede ser negativo.
     * Valor por defecto: 0.
     */
    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
    private BigDecimal descuento = BigDecimal.ZERO;

    /**
     * Monto recibido del cliente (útil para calcular vuelto en efectivo).
     * No puede ser negativo.
     * Valor por defecto: 0.
     */
    @DecimalMin(value = "0.00", message = "El monto recibido no puede ser negativo")
    private BigDecimal montoRecibido = BigDecimal.ZERO;

    /** Observaciones generales sobre la venta. */
    private String observaciones;

    /** Dirección de entrega (solo para delivery o pedidos para llevar). */
    private String direccionEntrega;

    /** Teléfono de contacto del cliente. */
    private String telefonoContacto;

    /**
     * Lista de productos que conforman la venta.
     * Obligatorio: una venta debe tener al menos un producto.
     */
    @NotEmpty(message = "La venta debe tener al menos un producto")
    private List<CrearDetalleVentaDTO> detalles;
}
