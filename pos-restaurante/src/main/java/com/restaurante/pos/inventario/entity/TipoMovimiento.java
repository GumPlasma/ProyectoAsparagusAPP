package com.restaurante.pos.inventario.entity;

/**
 * ENUM TIPO DE MOVIMIENTO
 * =======================
 *
 * CAPA: Entity / Enum de Dominio
 * RESPONSABILIDAD: Definir los tipos posibles de movimiento de inventario.
 *
 * QUÉ REPRESENTA:
 * Este enum clasifica cada movimiento en dos categorías fundamentales:
 * entradas (suman stock) y salidas (restan stock).
 *
 * POR QUÉ USAR UN ENUM:
 * - Garantiza que solo existan valores predefinidos y válidos.
 * - Evita errores de escritura que ocurrirían con Strings libres.
 * - Facilita el mantenimiento: si se necesita un nuevo tipo, se agrega aquí.
 * - Permite usar switch/clean code en la lógica de negocio.
 */
public enum TipoMovimiento {

    /**
     * ENTRADA: Movimiento que INCREMENTA la cantidad en stock.
     *
     * CUÁNDO SE USA:
     * - Compra a proveedores
     * - Devoluciones de clientes
     * - Ajustes positivos (conteo físico encontró más unidades)
     * - Transferencias desde otra ubicación
     */
    ENTRADA,

    /**
     * SALIDA: Movimiento que DECREMENTA la cantidad en stock.
     *
     * CUÁNDO SE USA:
     * - Ventas al público
     * - Mermas (producto dañado, vencido o perdido)
     * - Ajustes negativos (conteo físico encontró menos unidades)
     * - Devoluciones a proveedores
     * - Transferencias hacia otra ubicación
     */
    SALIDA
}
