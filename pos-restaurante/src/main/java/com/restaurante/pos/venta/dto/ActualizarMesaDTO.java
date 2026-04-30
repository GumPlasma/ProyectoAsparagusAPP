package com.restaurante.pos.venta.dto;

import lombok.Data;

/**
 * DTO (DATA TRANSFER OBJECT) - ACTUALIZAR MESA
 * ============================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos para actualizar una mesa existente
 * desde el cliente hacia el servidor.
 *
 * ¿QUÉ REPRESENTA?
 * Contiene los campos que pueden modificarse de una mesa ya registrada.
 * Todos los campos son opcionales: solo se actualizan los que se envían.
 *
 * ¿POR QUÉ EXISTE?
 * - Permite actualizaciones parciales sin enviar todos los campos de la mesa.
 * - Separa la estructura de actualización de la entidad Mesa.
 * - Facilita la modificación rápida desde la interfaz de administración.
 */
@Data
public class ActualizarMesaDTO {

    /** Nueva capacidad máxima de personas. */
    private Integer capacidad;

    /** Nueva ubicación descriptiva. */
    private String ubicacion;

    /** Nuevo estado (LIBRE, OCUPADA, RESERVADA, PAGADA). */
    private String estado;

    /** Nuevas notas adicionales. */
    private String notas;
}
