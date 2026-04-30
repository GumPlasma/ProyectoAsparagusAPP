package com.restaurante.pos.inventario.entity;

/**
 * ENUM MOTIVO DE MOVIMIENTO
 * =========================
 *
 * CAPA: Entity / Enum de Dominio
 * RESPONSABILIDAD: Definir las razones específicas por las cuales
 * puede ocurrir un movimiento de inventario.
 *
 * QUÉ REPRESENTA:
 * Mientras que {@link TipoMovimiento} indica SI suma o resta,
 * este enum indica POR QUÉ ocurrió el movimiento.
 *
 * POR QUÉ USAR UN ENUM:
 * - Estandariza las razones de movimiento en todo el sistema.
 * - Permite generar reportes y estadísticas por motivo.
 * - Facilita la trazabilidad y auditoría del inventario.
 * - Evita inconsistencias por ingreso manual de textos libres.
 */
public enum MotivoMovimiento {

    /**
     * COMPRA: Ingreso de productos por adquisición a un proveedor.
     * Tipo asociado: ENTRADA.
     */
    COMPRA,

    /**
     * VENTA: Salida de productos por venta a un cliente.
     * Tipo asociado: SALIDA.
     */
    VENTA,

    /**
     * MERMA: Pérdida de producto por daño, vencimiento, robo o deterioro.
     * Tipo asociado: SALIDA.
     * Requiere observaciones detalladas.
     */
    MERMA,

    /**
     * AJUSTE: Corrección de stock tras un conteo físico de inventario.
     * Puede ser ENTRADA (sobrante) o SALIDA (faltante).
     */
    AJUSTE,

    /**
     * DEVOLUCION: Retorno de productos.
     * Puede ser:
     * - DEVOLUCION de cliente al negocio -> ENTRADA
     * - DEVOLUCION del negocio a proveedor -> SALIDA
     */
    DEVOLUCION,

    /**
     * TRANSFERENCIA: Movimiento de productos entre diferentes ubicaciones,
     * almacenes o sucursales del restaurante.
     * Para el origen es SALIDA, para el destino es ENTRADA.
     */
    TRANSFERENCIA
}
