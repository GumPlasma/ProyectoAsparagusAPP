package com.restaurante.pos.producto.repository;

import com.restaurante.pos.producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE PRODUCTO
 * =======================
 *
 * Proporciona acceso a los datos de productos en la base de datos.
 *
 * ¿QUÉ ES JpaSpecificationExecutor?
 * - Permite crear consultas dinámicas complejas (Specifications)
 * - Útil para filtros múltiples combinables
 * - Ejemplo: buscar productos por categoría + precio + disponibilidad
 *
 * MÉTODOS HEREDADOS DE JpaRepository:
 * - save(producto) → Guarda o actualiza
 * - findById(id) → Busca por ID
 * - findAll() → Obtiene todos los productos
 * - deleteById(id) → Elimina por ID
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>,
        JpaSpecificationExecutor<Producto> {

    // ==========================================================================
    // MÉTODOS DE BÚSQUEDA POR CAMPOS ESPECÍFICOS
    // ==========================================================================

    /**
     * Busca un producto por su código único.
     *
     * CONVENCION: findByCodigo → WHERE codigo = ?
     *
     * @param codigo Código del producto (ej: código de barras)
     * @return Optional con el producto si existe
     *
     * EJEMPLO DE USO:
     * Optional<Producto> prod = repo.findByCodigo("7701234567890");
     */
    Optional<Producto> findByCodigo(String codigo);

    /**
     * Verifica si existe un producto con el código dado.
     *
     * CONVENCION: existsByCodigo → SELECT COUNT(p) > 0 WHERE codigo = ?
     *
     * @param codigo Código a verificar
     * @return true si existe, false si no
     */
    boolean existsByCodigo(String codigo);

    /**
     * Busca todos los productos de una categoría.
     *
     * CONVENCION: findByCategoriaId → WHERE categoria_id = ?
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos de esa categoría
     *
     * EJEMPLO:
     * List<Producto> bebidas = repo.findByCategoriaId(1L);
     */
    List<Producto> findByCategoriaId(Long categoriaId);

    // ==========================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ==========================================================================

    /**
     * Busca productos activos de una categoría específica.
     *
     * CONSULTA JPQL:
     * "SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.activo = true"
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos activos de esa categoría
     */
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.activo = true")
    List<Producto> findActivosByCategoriaId(@Param("categoriaId") Long categoriaId);

    /**
     * Busca todos los productos disponibles y activos.
     *
     * CONSULTA JPQL:
     * "SELECT p FROM Producto p WHERE p.disponible = true AND p.activo = true"
     *
     * DIFERENCIA ENTRE disponible Y activo:
     * - activo = no está eliminado (soft delete)
     * - disponible = está en stock y se puede vender
     *
     * @return Lista de productos que se pueden vender
     */
    @Query("SELECT p FROM Producto p WHERE p.disponible = true AND p.activo = true")
    List<Producto> findAllDisponibles();

    /**
     * Búsqueda parcial por nombre (insensible a mayúsculas/minúsculas).
     *
     * CONVENCION: findByNombreContainingIgnoreCase
     * - Containing → LIKE %valor%
     * - IgnoreCase → UPPER(nombre) = UPPER(?)
     *
     * @param nombre Fragmento del nombre a buscar
     * @return Lista de productos cuyo nombre contiene el fragmento
     *
     * EJEMPLO:
     * repo.findByNombreContainingIgnoreCase("hamburguesa")
     * → Encuentra: "Hamburguesa Clásica", "hamburguesa Deluxe", etc.
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Búsqueda combinada por nombre o código.
     *
     * CONSULTA JPQL:
     * Busca coincidencias parciales en nombre O código, ignorando mayúsculas
     *
     * @param busqueda Texto a buscar
     * @return Lista de productos que coinciden en nombre o código
     *
     * EJEMPLO:
     * repo.buscarPorNombreOCodigo("coca")
     * → Puede encontrar por nombre: "Coca-Cola" o por código: "0123456COCA"
     */
    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Producto> buscarPorNombreOCodigo(@Param("busqueda") String busqueda);

    /**
     * Busca productos con stock bajo (próximos a agotarse).
     *
     * CONSULTA JPQL:
     * "SELECT p FROM Producto p JOIN Inventario i ON p.id = i.producto.id
     *  WHERE i.cantidad <= i.stockMinimo AND p.activo = true"
     *
     * - JOIN con Inventario: Une producto con su registro de stock
     * - WHERE: Filtra donde cantidad actual <= stock mínimo
     *
     * @return Lista de productos que necesitan reposición
     *
     * EJEMPLO DE USO:
     * List<Producto> porReponer = repo.findProductosConStockBajo();
     * → [Arroz, Aceite, Sal] (ejemplo)
     */
    @Query("SELECT p FROM Producto p JOIN Inventario i ON p.id = i.producto.id " +
            "WHERE i.cantidad <= i.stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo();
}
