package com.restaurante.pos.venta.entity;

import com.restaurante.pos.mesa.entity.Mesa;
import com.restaurante.pos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD PEDIDO
 * ==============
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "pedido" de la base de datos.
 *
 * ¿QUÉ REPRESENTA?
 * Representa un pedido genérico en el restaurante, que puede estar asociado
 * a una mesa (consumo en local) o a una venta directa (para llevar/delivery).
 * Es diferente de {@link com.restaurante.pos.mesa.entity.PedidoMesa}, que es
 * específicamente una línea de producto dentro de una mesa.
 *
 * DIFERENCIA CLAVE:
 * - PedidoMesa: línea de producto en una mesa (ej: "2 hamburguesas en mesa 3").
 * - Pedido: agrupación lógica de productos que puede convertirse en una venta.
 *
 * HERENCIA:
 * Extiende {@link BaseEntity} con campos comunes como id, createdAt, etc.
 *
 * ANOTACIONES JPA:
 * - @Entity, @Table: Marcan la clase como entidad JPA.
 * - @Column: Configura propiedades de columnas.
 * - @ManyToOne: Relaciones opcionales con Mesa y Venta.
 * - @OneToMany: Relación con los detalles del pedido.
 */
@Entity
@Table(name = "pedido")
@Getter
@Setter
public class Pedido extends BaseEntity {

    /**
     * Número de pedido visible (ej: P001-000456).
     * Se genera automáticamente según la configuración del local.
     */
    @Column(name = "numero_pedido", length = 20)
    private String numeroPedido;

    /**
     * Estado del pedido.
     * Valores: PENDIENTE, ENVIADO_COCINA, EN_PREPARACION, LISTO, ENTREGADO, COMPLETADO.
     * Valor por defecto: "PENDIENTE".
     */
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE";

    /**
     * Fecha y hora en que se registró el pedido.
     */
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    /**
     * Total acumulado del pedido (suma de los subtotales de sus detalles).
     * precision = 12, scale = 2.
     */
    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Observaciones generales del pedido.
     * columnDefinition = "TEXT" permite textos largos.
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // ============================================
    // RELACIONES CON OTRAS ENTIDADES
    // ============================================

    /**
     * RELACIÓN CON MESA (OPCIONAL)
     * ============================
     * Un pedido puede estar asociado a una mesa (consumo en local).
     * FetchType.LAZY: la mesa no se carga automáticamente.
     * nullable: es opcional (pedidos para llevar no tienen mesa).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    /**
     * RELACIÓN CON VENTA (OPCIONAL)
     * =============================
     * Un pedido puede convertirse en una venta formal.
     * FetchType.LAZY para optimizar rendimiento.
     * nullable: es opcional (un pedido nuevo aún no tiene venta asociada).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    /**
     * RELACIÓN CON DETALLES DEL PEDIDO
     * ================================
     * Un pedido tiene muchos detalles (líneas de producto).
     * CascadeType.ALL: las operaciones se propagan a los detalles.
     * FetchType.LAZY: los detalles no se cargan hasta acceder a ellos.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetallePedido> detalles = new ArrayList<>();
}
