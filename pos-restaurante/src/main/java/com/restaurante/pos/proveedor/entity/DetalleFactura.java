package com.restaurante.pos.proveedor.entity;

import com.restaurante.pos.common.BaseEntity;
import com.restaurante.pos.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * ENTIDAD DETALLE FACTURA
 * =======================
 *
 * Representa cada línea de producto en una factura de proveedor.
 * Cada detalle corresponde a un producto específico.
 *
 * INFORMACIÓN QUE CONTIENE:
 * - Producto comprado
 * - Cantidad comprada
 * - Precio unitario de compra
 * - Subtotal (cantidad × precio)
 *
 * IMPORTANTE:
 * - Al registrar un detalle, se suma automáticamente al inventario
 * - Si se anula la factura, se resta del inventario
 */
@Entity
@Table(name = "detalle_factura")
@Getter
@Setter
public class DetalleFactura extends BaseEntity {

    /**
     * Cantidad comprada del producto.
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario de compra.
     * Este precio actualiza el costo del producto.
     */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /**
     * Subtotal de la línea (cantidad × precio_unitario).
     */
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    /**
     * Descripción del producto en caso no esté en el catálogo.
     */
    @Column(name = "descripcion_adicional", length = 255)
    private String descripcionAdicional;

    /**
     * RELACIÓN CON FACTURA
     * ===================
     * Cada detalle pertenece a una factura.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "factura_id", nullable = false)
    private FacturaProveedor factura;

    /**
     * RELACIÓN CON PRODUCTO
     * ====================
     * Cada detalle corresponde a un producto del inventario.
     * Puede ser null si es un producto no catalogado.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    /**
     * Método para calcular el subtotal.
     */
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }
}
