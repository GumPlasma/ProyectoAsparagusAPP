package com.restaurante.pos.mesa.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO (DATA TRANSFER OBJECT) - PAGO DE MESA
 * =========================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar la información necesaria para procesar el pago
 * de una mesa ocupada desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene todos los datos que el usuario (cajero/mesero) ingresa al momento
 * de cobrar una mesa: método de pago, monto recibido, propina, etc.
 *
 * ¿POR QUÉ EXISTE?
 * - Separa la información de entrada del pago de la entidad Venta final.
 * - Permite validar los datos del pago antes de generar la venta definitiva.
 * - Facilita manejar diferentes tipos de propina (porcentaje o monto fijo).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoMesaDTO {

    /** Identificador de la mesa que se va a pagar. */
    private Long mesaId;

    /**
     * Método de pago utilizado.
     * Valores posibles: EFECTIVO, TARJETA, TRANSFERENCIA.
     */
    private String metodoPago; // EFECTIVO, TARJETA, TRANSFERENCIA

    /**
     * Monto entregado por el cliente (solo aplica para EFECTIVO).
     * Se usa para calcular el vuelto.
     */
    private BigDecimal montoRecibido;

    /** Monto final de propina a aplicar a la venta. */
    private BigDecimal propina;

    /**
     * Tipo de cálculo de propina.
     * Valores posibles: PORCENTAJE (del total), FIJO (monto exacto).
     */
    private String tipoPropina; // PORCENTAJE, FIJO

    /** Valor numérico de la propina (porcentaje o monto fijo según tipoPropina). */
    private BigDecimal valorPropina;
}
