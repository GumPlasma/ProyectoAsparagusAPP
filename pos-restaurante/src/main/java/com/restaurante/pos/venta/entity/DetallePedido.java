package com.restaurante.pos.venta.entity;

import com.restaurante.pos.common.BaseEntity;
import com.restaurante.pos.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * ENTIDAD DETALLE PEDIDO
 * ======================
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "detalle_pedido" de la base de datos.
 *
 * ¿QUÉ REPRESENTA?
 * Representa una línea de producto dentro de un {@link Pedido}.
 * Cada instancia indica qué producto, en qué cantidad y a qué precio
 * fue solicitado dentro de un pedido.
 *
 * DIFERENCIA CON DETALLE VENTA:
 * - DetallePedido: pertenece a un Pedido (etapa previa a la venta).
 * - DetalleVenta: pertenece a una Venta (transacción ya completada).
 * Un DetallePedido puede convertirse en un DetalleVenta al finalizar el pedido.
 *
 * HERENCIA:
 * Extiende {@link BaseEntity} con campos comunes como id, createdAt, etc.
 *
 * ANOTACIONES JPA:
 * - @Entity, @Table: Marcan la clase como entidad JPA.
 * - @Column: Configura propiedades de columnas.
 * - @ManyToOne: Relaciones con Pedido y Producto.
 */
@Entity
@Table(name = "detalle_pedido")
@Getter
@Setter
public class DetallePedido extends BaseEntity {

    /**
     * Cantidad de unidades solicitadas del producto.
     * Obligatorio, debe ser al menos 1.
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario del producto al momento del pedido.
     * Se guarda para mantener el historial correcto.
     * precision = 12, scale = 2.
     */
    @Column(name = "precio_unitario", precision = 12, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    /**
     * Subtotal de esta línea (cantidad × precioUnitario).
     * precision = 12, scale = 2.
     */
    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Notas especiales para esta línea.
     * Ejemplo: "Sin cebolla", "Bien cocido".
     */
    @Column(name = "notas", length = 255)
    private String notas;

    // ============================================
    // RELACIONES CON OTRAS ENTIDADES
    // ============================================

    /**
     * RELACIÓN CON PEDIDO
     * ===================
     * Cada detalle pertenece a un pedido.
     * FetchType.LAZY: el pedido no se carga automáticamente.
     * nullable = false: todo detalle debe estar asociado a un pedido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    /**
     * RELACIÓN CON PRODUCTO
     * =====================
     * Cada detalle corresponde a un producto del catálogo.
     * FetchType.LAZY: el producto no se carga automáticamente.
     * nullable = false: todo detalle debe referirse a un producto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
