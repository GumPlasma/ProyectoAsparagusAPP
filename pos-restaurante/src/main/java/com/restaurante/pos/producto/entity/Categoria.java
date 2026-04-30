package com.restaurante.pos.producto.entity;

import com.restaurante.pos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD CATEGORÍA
 * =================
 *
 * Representa las categorías de productos del restaurante.
 * Las categorías ayudan a organizar el menú.
 *
 * EJEMPLOS DE CATEGORÍAS:
 * - Bebidas
 * - Entradas
 * - Platos principales
 * - Postres
 * - Combos
 *
 * RELACIÓN CON PRODUCTO:
 * - Una categoría puede tener muchos productos (1:N)
 * - Ejemplo: Categoría "Bebidas" tiene: Coca-Cola, Agua, Jugo, etc.
 */
@Entity  // Marca esta clase como entidad JPA (se mapea a una tabla)
@Table(name = "categoria")  // Nombre de la tabla en la base de datos
@Getter  // Lombok genera getters automáticos
@Setter  // Lombok genera setters automáticos
public class Categoria extends BaseEntity {

    // ==========================================================================
    // CAMPOS DE INFORMACIÓN DE LA CATEGORÍA
    // ==========================================================================

    /**
     * Nombre de la categoría.
     * Ejemplos: "Bebidas", "Entradas", "Platos Principales"
     *
     * ANOTACIONES:
     * - @Column: Define la columna en la tabla 'categoria'
     * - nullable = false: Campo obligatorio
     * - unique = true: No puede haber dos categorías con el mismo nombre
     * - length = 100: Máximo 100 caracteres
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Descripción opcional de la categoría.
     * Puede incluir detalles sobre qué tipo de productos contiene.
     * Ejemplo: "Bebidas calientes y frías para acompañar tus comidas"
     *
     * ANOTACIONES:
     * - length = 255: Máximo 255 caracteres
     * - No tiene nullable = false → es opcional (puede ser null)
     */
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /**
     * URL de imagen representativa de la categoría.
     * Para mostrar en el menú visual del POS.
     * Ejemplo: "/imagenes/categorias/bebidas.jpg"
     *
     * ANOTACIONES:
     * - length = 500: Suficiente para URLs largas
     * - Campo opcional
     */
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    /**
     * Orden de aparición en el menú.
     * Las categorías se ordenan por este número de menor a mayor.
     * Ejemplo: 1 = Entradas, 2 = Platos Principales, 3 = Postres
     *
     * ANOTACIONES:
     * - Sin restricciones → opcional
     * - Valor por defecto: 0
     */
    @Column(name = "orden")
    private Integer orden = 0;

    // ==========================================================================
    // RELACIONES CON OTRAS ENTIDADES
    // ==========================================================================

    /**
     * RELACIÓN CON PRODUCTOS
     * =====================
     * @OneToMany: Una categoría tiene muchos productos
     * mappedBy: Indica que el lado propietario es Producto.categoria
     *           (la foreign key está en la tabla producto)
     * fetch = FetchType.LAZY: Carga perezosa, no trae productos automáticamente
     *
     * ¿POR QUÉ LAZY?
     * - Al consultar categorías, no siempre necesitamos los productos
     * - Mejora el rendimiento al evitar cargas innecesarias
     * - Los productos se cargan solo cuando se accede a la lista
     *
     * EN LA BASE DE DATOS:
     * La tabla 'producto' tiene una columna 'categoria_id' que referencia a 'categoria.id'
     */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Producto> productos = new ArrayList<>();

    // ==========================================================================
    // MÉTODOS HEREDADOS DE BaseEntity
    // ==========================================================================
    // - getId(): ID único de la categoría
    // - getFechaCreacion(): Fecha cuando se creó la categoría
    // - getFechaActualizacion(): Fecha de última modificación
    // - getActivo(): Indica si la categoría está activa
    // - eliminar(): Marca la categoría como inactiva (soft delete)
    // - restaurar(): Restaura una categoría eliminada
}