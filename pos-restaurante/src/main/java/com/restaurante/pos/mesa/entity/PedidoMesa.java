package com.restaurante.pos.mesa.entity;

import com.restaurante.pos.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ENTIDAD PEDIDO MESA
 * ===================
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "pedidos_mesa" de la base de datos.
 *
 * ¿QUÉ REPRESENTA?
 * Representa una línea de pedido dentro de una mesa ocupada.
 * Cada instancia indica que se solicitó un producto específico en una cantidad
 * determinada para una mesa determinada.
 *
 * EJEMPLO: Si en la Mesa 3 se piden 2 hamburguesas y 1 gaseosa,
 * se crean 2 registros de PedidoMesa (uno para hamburguesas, otro para gaseosa).
 *
 * ANOTACIONES JPA:
 * - @Entity: Marca la clase como entidad JPA gestionada por Hibernate.
 * - @Table(name = "pedidos_mesa"): Nombre exacto de la tabla en la base de datos.
 * - @ManyToOne: Define relaciones de muchos-a-uno con Mesa y Producto.
 * - @JoinColumn: Especifica la columna de clave foránea en la tabla.
 *
 * ANOTACIONES LOMBOK:
 * - @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder: ver Mesa.java
 */
@Entity
@Table(name = "pedidos_mesa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoMesa {

    /**
     * ID único del pedido (clave primaria autoincremental).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RELACIÓN CON MESA
     * =================
     * Muchos pedidos pueden pertenecer a una misma mesa.
     * FetchType.LAZY: la mesa NO se carga automáticamente al obtener el pedido,
     * sino solo cuando se accede al campo (mejora el rendimiento).
     * nullable = false: todo pedido DEBE estar asociado a una mesa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    /**
     * RELACIÓN CON PRODUCTO
     * =====================
     * Cada pedido corresponde a un producto del catálogo.
     * FetchType.LAZY mejora rendimiento evitando cargar todo el producto innecesariamente.
     * nullable = false: todo pedido debe referirse a un producto existente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    /**
     * Cantidad de unidades del producto solicitadas.
     * Obligatorio, debe ser al menos 1.
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario del producto AL MOMENTO del pedido.
     * Se guarda explícitamente para mantener el historial correcto
     * aunque el precio del producto cambie posteriormente.
     * precision = 10, scale = 2 → hasta 99,999,999.99
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    /**
     * Subtotal de esta línea de pedido (cantidad × precioUnitario).
     * Se calcula en el servicio antes de guardar.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * Notas especiales para este pedido.
     * Ejemplo: "Sin cebolla", "Bien cocido", "Para llevar".
     */
    private String notas;

    /**
     * Fecha y hora exacta en que se realizó el pedido.
     * Se establece automáticamente en el servicio al crear el pedido.
     */
    private LocalDateTime fechaHora;
}
