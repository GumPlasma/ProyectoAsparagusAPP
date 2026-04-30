package com.restaurante.pos.proveedor.entity;

import com.restaurante.pos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD FACTURA PROVEEDOR
 * =========================
 *
 * Representa las facturas de compra a proveedores.
 * Cada factura puede contener múltiples productos (detalles).
 *
 * FLUJO DE INGRESO DE MERCADERÍA:
 * 1. Llega mercadería del proveedor con factura
 * 2. Se registra la factura en el sistema
 * 3. Se registran los productos en detalle_factura
 * 4. Automáticamente se suma al inventario
 *
 * ESTADOS DE LA FACTURA:
 * - PENDIENTE: Recién registrada, no procesada
 * - PROCESADA: Ya actualizó el inventario
 * - ANULADA: Cancelada, revirtió cambios en inventario
 */
@Entity
@Table(name = "factura_proveedor")
@Getter
@Setter
public class FacturaProveedor extends BaseEntity {

    /**
     * Número de factura del proveedor.
     * Combinación de serie y número.
     * Ejemplo: "F001-00001234"
     */
    @Column(name = "numero_factura", nullable = false, length = 50)
    private String numeroFactura;

    /**
     * Fecha de emisión de la factura.
     */
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    /**
     * Fecha de recepción en el restaurante.
     */
    @Column(name = "fecha_recepcion")
    private LocalDate fechaRecepcion;

    /**
     * Subtotal antes de impuestos.
     */
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Porcentaje de impuesto (IGV).
     * Normalmente 18% en Perú.
     */
    @Column(name = "porcentaje_impuesto", precision = 5, scale = 2)
    private BigDecimal porcentajeImpuesto = new BigDecimal("18.00");

    /**
     * Monto del impuesto.
     */
    @Column(name = "monto_impuesto", precision = 12, scale = 2)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    /**
     * Total de la factura.
     */
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Estado de la factura.
     * Valores: PENDIENTE, PROCESADA, ANULADA
     */
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    /**
     * Observaciones adicionales.
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * RELACIÓN CON PROVEEDOR
     * =====================
     * Cada factura pertenece a un proveedor.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    /**
     * RELACIÓN CON DETALLES
     * ====================
     * Una factura tiene muchos detalles (productos).
     */
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalles = new ArrayList<>();

    /**
     * Calcula el total basándose en los detalles.
     */
    public void calcularTotal() {
        subtotal = BigDecimal.ZERO;
        for (DetalleFactura detalle : detalles) {
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        montoImpuesto = subtotal.multiply(porcentajeImpuesto)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        total = subtotal.add(montoImpuesto);
    }
}
