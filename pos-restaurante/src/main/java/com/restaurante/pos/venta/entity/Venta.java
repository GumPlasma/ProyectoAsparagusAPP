package com.restaurante.pos.venta.entity;

import com.restaurante.pos.common.BaseEntity;
import com.restaurante.pos.usuario.entity.Usuario;
import com.restaurante.pos.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD VENTA
 * =============
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "venta" de la base de datos.
 * Representa una transacción de venta completada en el restaurante.
 *
 * ¿QUÉ REPRESENTA?
 * Cada instancia es una venta formal registrada en el sistema, ya sea proveniente
 * de una mesa (tipoVenta = "MESA") o de un pedido para llevar (tipoVenta = "LLEVAR").
 * Contiene toda la información del comprobante: totales, impuestos, descuentos,
 * método de pago, cliente, vendedor, y sus detalles (productos vendidos).
 *
 * HERENCIA:
 * Extiende {@link BaseEntity}, que probablemente contiene campos comunes
 * como id, createdAt, updatedAt, activo, etc.
 *
 * ANOTACIONES JPA:
 * - @Entity: Entidad gestionada por JPA/Hibernate.
 * - @Table(name = "venta"): Nombre de la tabla en la BD.
 * - @Column: Configura propiedades de columnas (nullable, precision, length, etc.).
 * - @ManyToOne: Relaciones con Cliente y Usuario (vendedor).
 * - @OneToMany: Relaciones con DetalleVenta y Pedido.
 *
 * ANOTACIONES LOMBOK:
 * - @Getter / @Setter: Generan getters y setters para todos los campos.
 */
@Entity
@Table(name = "venta")
@Getter
@Setter
public class Venta extends BaseEntity {

    /**
     * Número de comprobante único (ej: F001-000123).
     * Se genera típicamente según la serie del establecimiento.
     */
    @Column(name = "numero_comprobante", unique = true, length = 20)
    private String numeroComprobante;

    /**
     * Tipo de comprobante emitido.
     * Valor por defecto: "TICKET" (también puede ser BOLETA, FACTURA).
     */
    @Column(name = "tipo_comprobante", length = 20)
    private String tipoComprobante = "TICKET";

    /**
     * Fecha de la venta (obligatoria).
     * Se usa para reportes diarios, mensuales y filtros de búsqueda.
     */
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /**
     * Hora exacta de la transacción.
     * Permite ordenar ventas dentro del mismo día.
     */
    @Column(name = "hora")
    private LocalDateTime hora;

    /**
     * Tipo de venta.
     * Valores: "LLEVAR" (para llevar), "MESA" (consumo en mesa), "DELIVERY".
     * Valor por defecto: "LLEVAR".
     */
    @Column(name = "tipo_venta", length = 20)
    private String tipoVenta = "LLEVAR";

    /**
     * Estado de la venta.
     * Valores: "PENDIENTE", "COMPLETADA", "ANULADA".
     * Valor por defecto: "PENDIENTE".
     */
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    /**
     * Subtotal de la venta (suma de los subtotales de todos los detalles).
     * Antes de aplicar impuestos y descuentos.
     * precision = 12, scale = 2 → hasta 999,999,999.99
     */
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Porcentaje de impuesto aplicado (ej: 18.00 para IGV).
     * Valor por defecto: 18.00%.
     */
    @Column(name = "porcentaje_impuesto", precision = 5, scale = 2)
    private BigDecimal porcentajeImpuesto = new BigDecimal("18.00");

    /**
     * Monto calculado del impuesto (subtotalConDescuento × porcentajeImpuesto / 100).
     * Se calcula automáticamente en el método calcularTotales().
     */
    @Column(name = "monto_impuesto", precision = 12, scale = 2)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    /**
     * Descuento global aplicado a la venta.
     * Se resta del subtotal antes de calcular impuestos.
     */
    @Column(name = "descuento", precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    /**
     * Total final a pagar (subtotalConDescuento + montoImpuesto).
     * Es el monto que el cliente debe abonar.
     */
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Método de pago utilizado.
     * Valores: "EFECTIVO", "TARJETA", "TRANSFERENCIA", "YAPE", "PLIN".
     * Valor por defecto: "EFECTIVO".
     */
    @Column(name = "metodo_pago", length = 20)
    private String metodoPago = "EFECTIVO";

    /**
     * Monto recibido del cliente (útil para calcular vuelto en efectivo).
     */
    @Column(name = "monto_recibido", precision = 12, scale = 2)
    private BigDecimal montoRecibido = BigDecimal.ZERO;

    /**
     * Vuelto entregado al cliente (solo para pagos en efectivo).
     * Calculado como: montoRecibido - total.
     */
    @Column(name = "vuelto", precision = 12, scale = 2)
    private BigDecimal vuelto = BigDecimal.ZERO;

    // ===== CAMPOS ESPECÍFICOS PARA VENTAS DESDE MESA =====

    /**
     * Número de la mesa desde donde se generó la venta.
     * Solo aplica cuando tipoVenta = "MESA".
     */
    @Column(name = "mesa_numero")
    private Integer mesaNumero;

    /**
     * Propina adicional incluida en la venta.
     * Se registra cuando el cliente decide dejar propina.
     */
    @Column(name = "propina", precision = 12, scale = 2)
    private BigDecimal propina = BigDecimal.ZERO;

    // ======================================================

    /**
     * Observaciones generales sobre la venta.
     * Ejemplo: "Cliente solicita factura", "Entrega en recepción".
     * columnDefinition = "TEXT" permite textos largos.
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Dirección de entrega (solo para ventas delivery o para llevar).
     */
    @Column(name = "direccion_entrega", length = 300)
    private String direccionEntrega;

    /**
     * Teléfono de contacto del cliente para esta venta.
     */
    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    // ============================================
    // RELACIONES CON OTRAS ENTIDADES
    // ============================================

    /**
     * RELACIÓN CON CLIENTE
     * ====================
     * Muchas ventas pueden pertenecer a un mismo cliente.
     * FetchType.EAGER: carga el cliente automáticamente al obtener la venta.
     * nullable: el cliente es opcional (ventas sin cliente registrado).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /**
     * RELACIÓN CON VENDEDOR (USUARIO)
     * ================================
     * Muchas ventas pueden ser realizadas por un mismo vendedor.
     * FetchType.EAGER: carga el usuario/vendedor automáticamente.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;

    /**
     * RELACIÓN CON DETALLES DE VENTA
     * ==============================
     * Una venta tiene muchos detalles (líneas de producto).
     * CascadeType.ALL: operaciones sobre la venta se propagan a sus detalles.
     * FetchType.LAZY: los detalles no se cargan hasta que se acceda a ellos.
     */
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles = new ArrayList<>();

    /**
     * RELACIÓN CON PEDIDOS
     * ====================
     * Una venta puede estar asociada a uno o más pedidos.
     * FetchType.LAZY para optimizar rendimiento.
     */
    @OneToMany(mappedBy = "venta", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    // ============================================
    // MÉTODOS DE LÓGICA DE NEGOCIO
    // ============================================

    /**
     * Calcula los totales de la venta sumando los subtotales de todos los detalles,
     * aplicando descuentos e impuestos.
     *
     * Lógica:
     * 1. Suma todos los subtotales de los detalles → subtotal.
     * 2. Resta el descuento global → subtotalConDescuento.
     * 3. Calcula el impuesto sobre el subtotal con descuento.
     * 4. El total = subtotalConDescuento + montoImpuesto.
     */
    public void calcularTotales() {
        subtotal = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            subtotal = subtotal.add(detalle.getSubtotal());
        }

        BigDecimal subtotalConDescuento = subtotal.subtract(descuento);
        if (subtotalConDescuento.compareTo(BigDecimal.ZERO) < 0) {
            subtotalConDescuento = BigDecimal.ZERO;
        }

        montoImpuesto = subtotalConDescuento.multiply(porcentajeImpuesto)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        total = subtotalConDescuento.add(montoImpuesto);
    }

    /**
     * Agrega un detalle a la venta y establece la relación bidireccional.
     * @param detalle El detalle de venta a agregar.
     */
    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }
}
