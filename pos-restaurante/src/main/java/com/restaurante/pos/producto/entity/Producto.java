package com.restaurante.pos.producto.entity;

import com.restaurante.pos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * ENTIDAD PRODUCTO
 * ================
 *
 * Representa los productos que se venden en el restaurante.
 * Cada producto pertenece a una categoría.
 *
 * EJEMPLOS DE PRODUCTOS:
 * - Hamburguesa Clásica (Categoría: Platos Principales)
 * - Coca-Cola 500ml (Categoría: Bebidas)
 * - Cheesecake (Categoría: Postres)
 *
 * INVENTARIO:
 * - El stock se gestiona en la entidad Inventario, no aquí
 * - Esto permite un mejor control de movimientos
 */
@Entity
@Table(name = "producto")
@Getter
@Setter
public class Producto extends BaseEntity {

    /**
     * Código interno del producto.
     * Puede usarse como código de barras.
     */
    @Column(name = "codigo", unique = true, length = 50)
    private String codigo;

    /**
     * Nombre del producto.
     * Ejemplo: "Hamburguesa Clásica"
     */
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /**
     * Descripción detallada del producto.
     * Puede incluir ingredientes, presentación, etc.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Precio de venta al público.
     * Usamos BigDecimal para precisión monetaria.
     * NUNCA usar double/float para dinero (errores de redondeo).
     */
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * Costo del producto (para cálculo de ganancias).
     */
    @Column(name = "costo", precision = 10, scale = 2)
    private BigDecimal costo;

    /**
     * URL de imagen del producto.
     * Para mostrar en el menú visual y POS.
     */
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    /**
     * Indica si el producto está disponible para la venta.
     * Un producto puede existir pero no estar disponible temporalmente.
     */
    @Column(name = "disponible")
    private Boolean disponible = true;

    /**
     * Indica si el producto requiere preparación en cocina.
     * true = Se envía a cocina (platos)
     * false = Solo se entrega (bebidas embotelladas)
     */
    @Column(name = "requiere_preparacion")
    private Boolean requierePreparacion = true;

    /**
     * Tiempo estimado de preparación en minutos.
     */
    @Column(name = "tiempo_preparacion")
    private Integer tiempoPreparacion;

    /**
     * RELACIÓN CON CATEGORÍA
     * =====================
     * @ManyToOne: Muchos productos pueden tener la misma categoría
     * @JoinColumn: Foreign key hacia la tabla categoria
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    /**
     * Método auxiliar para calcular el margen de ganancia.
     */
    public BigDecimal getMargenGanancia() {
        if (costo == null || costo.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return precio.subtract(costo).divide(costo, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}