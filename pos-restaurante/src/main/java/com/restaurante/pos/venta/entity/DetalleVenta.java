package com.restaurante.pos.venta.entity;

import com.restaurante.pos.common.BaseEntity;
import com.restaurante.pos.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * ENTIDAD DETALLE VENTA
 * =====================
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "detalle_venta" de la base de datos.
 *
 * ¿QUÉ REPRESENTA?
 * Cada instancia representa una línea de producto dentro de una venta.
 * Es decir, si una venta incluye 2 hamburguesas y 1 gaseosa,
 * habrá 2 registros de DetalleVenta asociados a esa venta.
 *
 * ¿POR QUÉ ES IMPORTANTE?
 * - El precio se guarda en el momento de la venta (no se actualiza si cambia el precio del producto).
 * - Esto permite mantener el historial de ventas correcto aunque los precios del catálogo cambien.
 * - También permite rastrear el estado de preparación de cada producto en cocina.
 *
 * HERENCIA:
 * Extiende {@link BaseEntity}, que contiene campos comunes como id, createdAt, etc.
 *
 * ANOTACIONES JPA:
 * - @Entity, @Table: Marcan la clase como entidad JPA y especifican la tabla.
 * - @Column: Configura propiedades de las columnas.
 * - @ManyToOne: Relaciones con Venta y Producto.
 */
@Entity
@Table(name = "detalle_venta")
@Getter
@Setter
public class DetalleVenta extends BaseEntity {

    /**
     * Cantidad vendida del producto.
     * Obligatorio, debe ser al menos 1.
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario al momento de la venta.
     * Se guarda para mantener historial correcto aunque el precio del producto cambie después.
     * precision = 10, scale = 2.
     */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /**
     * Subtotal de la línea (cantidad × precioUnitario - descuento).
     * Representa el monto de esta línea antes de impuestos.
     */
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    /**
     * Descuento aplicado a esta línea específica.
     * Permite descuentos por producto (ej: promociones, happy hour).
     */
    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    /**
     * Notas especiales para esta línea.
     * Ejemplo: "Sin cebolla", "Término medio", "Extra queso".
     */
    @Column(name = "notas", length = 255)
    private String notas;

    /**
     * Estado de preparación en cocina.
     * Valores: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO.
     * Permite al personal de cocina y meseros saber el estado de cada producto.
     */
    @Column(name = "estado_preparacion", length = 20)
    private String estadoPreparacion = "PENDIENTE";

    // ============================================
    // RELACIONES CON OTRAS ENTIDADES
    // ============================================

    /**
     * RELACIÓN CON VENTA
     * ==================
     * Cada detalle pertenece a una venta.
     * FetchType.EAGER: la venta se carga automáticamente (útil para mostrar info completa).
     * nullable = false: todo detalle debe pertenecer a una venta.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    /**
     * RELACIÓN CON PRODUCTO
     * =====================
     * Cada detalle corresponde a un producto del catálogo.
     * FetchType.EAGER: el producto se carga automáticamente.
     * nullable = false: todo detalle debe referirse a un producto.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // ============================================
    // MÉTODOS DE LÓGICA DE NEGOCIO
    // ============================================

    /**
     * Calcula el subtotal de esta línea de venta.
     * Fórmula: (precioUnitario × cantidad) - descuento.
     * Si el resultado es negativo, se establece en cero.
     */
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            BigDecimal subtotalSinDescuento = precioUnitario.multiply(new BigDecimal(cantidad));
            subtotal = subtotalSinDescuento.subtract(descuento);
            if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
                subtotal = BigDecimal.ZERO;
            }
        }
    }

    /**
     * Marca este detalle como enviado a cocina para su preparación.
     */
    public void enviarACocina() {
        this.estadoPreparacion = "EN_PREPARACION";
    }

    /**
     * Marca este detalle como listo para ser entregado al cliente.
     */
    public void marcarListo() {
        this.estadoPreparacion = "LISTO";
    }

    /**
     * Marca este detalle como entregado al cliente/mesa.
     */
    public void marcarEntregado() {
        this.estadoPreparacion = "ENTREGADO";
    }
}
