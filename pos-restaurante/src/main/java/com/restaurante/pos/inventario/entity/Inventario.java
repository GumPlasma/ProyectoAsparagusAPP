package com.restaurante.pos.inventario.entity;

import com.restaurante.pos.common.BaseEntity;
import com.restaurante.pos.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD INVENTARIO
 * ==================
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "inventario" de la base de datos
 * y representar el estado de stock de un producto en la aplicación.
 *
 * QUÉ REPRESENTA:
 * Cada instancia de esta clase corresponde a UNA fila en la tabla inventario.
 * Un producto tiene exactamente UN registro de inventario (relación 1:1).
 *
 * INFORMACIÓN QUE CONTIENE:
 * - Cantidad actual en stock
 * - Stock mínimo (alerta de reposición)
 * - Stock máximo (límite de almacenamiento)
 * - Última fecha de movimiento
 * - Precio promedio ponderado para valorización
 *
 * IMPORTANTE:
 * - El stock se actualiza EXCLUSIVAMENTE mediante movimientos (entradas/salidas).
 * - Nunca se debe modificar directamente la cantidad sin registrar un MovimientoInventario,
 *   para mantener la trazabilidad y auditoría del sistema.
 *
 * ANOTACIONES JPA:
 * - @Entity: Declara esta clase como entidad gestionada por JPA/Hibernate.
 * - @Table(name = "inventario"): Especifica el nombre exacto de la tabla en la BD.
 * - @Getter / @Setter: Lombok genera automáticamente getters y setters.
 */
@Entity
@Table(name = "inventario")
@Getter
@Setter
public class Inventario extends BaseEntity {

    /**
     * Cantidad actual en stock.
     * Se actualiza automáticamente con cada movimiento de entrada o salida.
     *
     * ANOTACIÓN JPA:
     * - @Column: Mapea este campo a la columna "cantidad". nullable = false garantiza
     *   que no pueda existir un registro sin cantidad definida.
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad = 0;

    /**
     * Stock mínimo permitido para este producto.
     * Cuando la cantidad llega a este valor o lo desciende, se genera una alerta.
     * Valor por defecto: 5 unidades.
     */
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5;

    /**
     * Stock máximo permitido para este producto.
     * Representa el límite de almacenamiento o la cantidad ideal a mantener.
     * Valor por defecto: 100 unidades.
     */
    @Column(name = "stock_maximo")
    private Integer stockMaximo = 100;

    /**
     * Ubicación física del producto en las instalaciones.
     * Ejemplo: "Estante A-3", "Refrigerador 2", "Bodega principal".
     */
    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    /**
     * Fecha y hora del último movimiento registrado.
     * Permite identificar qué productos no tienen rotación reciente.
     */
    @Column(name = "ultimo_movimiento")
    private java.time.LocalDateTime ultimoMovimiento;

    /**
     * Precio promedio ponderado del stock actual.
     * Se recalcula con cada entrada por compra usando la fórmula de promedio ponderado.
     * Es útil para valorización contable del inventario.
     *
     * ANOTACIÓN JPA:
     * - precision = 10, scale = 2: Define DECIMAL(10,2) en la base de datos.
     */
    @Column(name = "precio_promedio", precision = 10, scale = 2)
    private BigDecimal precioPromedio = BigDecimal.ZERO;

    // ==========================================
    // RELACIONES JPA
    // ==========================================

    /**
     * RELACIÓN CON PRODUCTO
     * =====================
     * Cada registro de inventario corresponde a exactamente un producto del catálogo.
     * Relación 1:1 (un producto tiene un solo inventario).
     *
     * ANOTACIONES JPA:
     * - @OneToOne: Indica relación uno a uno con Producto.
     * - fetch = FetchType.EAGER: El producto se carga inmediatamente junto con el inventario.
     *   Esto es conveniente porque casi siempre se necesita el nombre/código del producto.
     * - @JoinColumn: Define la columna foránea "producto_id" en la tabla inventario.
     * - unique = true: Garantiza integridad referencial (un producto no puede tener dos inventarios).
     * - nullable = false: Todo inventario debe estar asociado a un producto.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", unique = true, nullable = false)
    private Producto producto;

    /**
     * RELACIÓN CON MOVIMIENTOS
     * ========================
     * Un inventario puede tener muchos movimientos a lo largo del tiempo.
     * Esta es una relación bidireccional: MovimientoInventario tiene el campo "inventario".
     *
     * ANOTACIÓN JPA:
     * - mappedBy = "inventario": Indica que el otro lado de la relación (MovimientoInventario)
     *   es el propietario y contiene la clave foránea.
     * - fetch = FetchType.LAZY: Los movimientos NO se cargan automáticamente al consultar
     *   el inventario, evitando cargar datos innecesarios y mejorando el rendimiento.
     */
    @OneToMany(mappedBy = "inventario", fetch = FetchType.LAZY)
    private List<MovimientoInventario> movimientos = new ArrayList<>();

    // ==========================================
    // MÉTODOS DE NEGOCIO
    // ==========================================

    /**
     * Verifica si el stock actual está en nivel de alerta.
     *
     * @return true si cantidad <= stockMinimo.
     */
    public boolean isStockBajo() {
        return cantidad <= stockMinimo;
    }

    /**
     * Verifica si hay suficiente stock para satisfacer una cantidad solicitada.
     *
     * @param cantidadSolicitada Cantidad que se desea retirar o verificar.
     * @return true si hay stock suficiente.
     */
    public boolean hayStock(int cantidadSolicitada) {
        return cantidad >= cantidadSolicitada;
    }
}
