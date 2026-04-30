package com.restaurante.pos.inventario.repository;

import com.restaurante.pos.inventario.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE INVENTARIO
 * =========================
 *
 * CAPA: Repository (Repositorio de Datos / Acceso a BD)
 * RESPONSABILIDAD: Abstraer el acceso a la base de datos para la entidad Inventario.
 * Esta interfaz define las operaciones de lectura y escritura disponibles
 * sobre la tabla "inventario".
 *
 * QUÉ HACE:
 * - Hereda de JpaRepository, lo que proporciona automáticamente métodos CRUD
 *   como save(), findById(), findAll(), deleteById(), etc.
 * - Declara métodos personalizados con consultas JPQL/SQL para necesidades específicas.
 * - Es el ÚNICO lugar donde se interactúa directamente con la tabla de inventario.
 *
 * ANOTACIONES SPRING:
 * - @Repository: Marca esta interfaz como componente de acceso a datos de Spring.
 *   Además, habilita la traducción de excepciones de persistencia a excepciones
 *   de Spring DataAccessException.
 *
 * INTERFAZ JpaRepository<Inventario, Long>:
 * - Primer parámetro (Inventario): Tipo de la entidad gestionada.
 * - Segundo parámetro (Long): Tipo de la clave primaria de la entidad.
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    /**
     * Busca el inventario asociado a un producto específico.
     *
     * POR QUÉ EXISTE: Spring Data JPA genera automáticamente la consulta
     * a partir del nombre del método (derivación de query).
     * "findByProductoId" -> SELECT ... WHERE producto_id = ?
     *
     * @param productoId Identificador del producto.
     * @return Optional con el inventario encontrado, o vacío si no existe.
     */
    Optional<Inventario> findByProductoId(Long productoId);

    /**
     * Verifica si ya existe un registro de inventario para un producto.
     *
     * POR QUÉ EXISTE: Permite evitar duplicados antes de inicializar
     * un nuevo inventario para un producto.
     *
     * @param productoId Identificador del producto.
     * @return true si ya existe inventario para ese producto.
     */
    boolean existsByProductoId(Long productoId);

    /**
     * Busca todos los inventarios cuyo stock está en nivel de alerta.
     *
     * CONSULTA JPQL:
     * - SELECT i FROM Inventario i: Selecciona entidades Inventario.
     * - WHERE i.cantidad <= i.stockMinimo: Condición de stock bajo.
     * - AND i.activo = true: Solo registros activos (heredado de BaseEntity).
     *
     * POR QUÉ EXISTE: Los dashboards de alerta necesitan esta lista filtrada
     * directamente desde la base de datos sin procesar en memoria.
     *
     * @return Lista de inventarios con stock bajo.
     */
    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo AND i.activo = true")
    List<Inventario> findConStockBajo();

    /**
     * Busca todos los inventarios con cantidad igual a cero.
     *
     * CONSULTA JPQL: Filtra por cantidad = 0 y estado activo.
     *
     * @return Lista de productos agotados.
     */
    @Query("SELECT i FROM Inventario i WHERE i.cantidad = 0 AND i.activo = true")
    List<Inventario> findAgotados();

    /**
     * Busca todos los inventarios activos cargando eagermente sus productos.
     *
     * CONSULTA JPQL:
     * - JOIN FETCH i.producto p: Realiza un JOIN FETCH que carga la entidad Producto
     *   en la MISMA consulta, evitando el problema de consultas N+1.
     * - WHERE i.activo = true AND p.activo = true: Solo registros activos de ambas entidades.
     *
     * POR QUÉ EXISTE: Cuando se lista el inventario, siempre se necesita el nombre
     * y código del producto. Sin JOIN FETCH, Hibernate haría una consulta extra por cada producto.
     *
     * @return Lista de inventarios con sus productos ya cargados.
     */
    @Query("SELECT i FROM Inventario i JOIN FETCH i.producto p WHERE i.activo = true AND p.activo = true")
    List<Inventario> findAllActivosWithProducto();

    /**
     * Calcula el valor monetario total del inventario.
     *
     * CONSULTA JPQL:
     * - SUM(i.cantidad * i.precioPromedio): Multiplica cantidad por precio promedio
     *   de cada producto y suma el total.
     * - Retorna Double porque SUM en JPQL puede retornar null si no hay registros.
     *
     * POR QUÉ EXISTE: Proporciona una métrica financiera directamente desde la BD
     * sin necesidad de traer todos los registros a memoria.
     *
     * @return Valor total del inventario, o null si no hay registros activos.
     */
    @Query("SELECT SUM(i.cantidad * i.precioPromedio) FROM Inventario i WHERE i.activo = true")
    Double calcularValorTotal();

    /**
     * Cuenta cuántos productos tienen stock bajo.
     *
     * CONSULTA JPQL: COUNT(i) cuenta las entidades que cumplen la condición.
     *
     * @return Cantidad de productos en alerta de stock bajo.
     */
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.cantidad <= i.stockMinimo AND i.activo = true")
    long countConStockBajo();

    /**
     * Cuenta cuántos productos están agotados.
     *
     * @return Cantidad de productos con cantidad = 0.
     */
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.cantidad = 0 AND i.activo = true")
    long countAgotados();
}
