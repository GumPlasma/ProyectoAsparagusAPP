package com.restaurante.pos.proveedor.repository;

import com.restaurante.pos.proveedor.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE PROVEEDOR
 * ========================
 *
 * CAPA: Repository (Acceso a Datos)
 * RESPONSABILIDAD: Definir el contrato de acceso a datos para la entidad Proveedor.
 *
 * ¿QUÉ HACE?
 * - Abstrae las operaciones de lectura y escritura sobre la base de datos.
 * - Hereda métodos CRUD básicos de JpaRepository (save, findById, findAll, deleteById).
 * - Define consultas personalizadas mediante derivación de nombres o JPQL.
 * - NO contiene lógica de negocio.
 *
 * SPRING DATA JPA:
 * - Genera automáticamente la implementación de esta interfaz en tiempo de ejecución.
 * - La derivación de nombres permite crear consultas sin escribir SQL/JPQL explícito.
 *
 * ANOTACIONES:
 * - @Repository: Marca la interfaz como componente de persistencia de Spring.
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // ============================================================
    // CONSULTAS POR DERIVACIÓN DE NOMBRE (QUERY METHODS)
    // ============================================================
    // Spring Data JPA interpreta el nombre del método y genera automáticamente
    // la consulta SQL correspondiente.

    /**
     * Busca un proveedor por su número de RUC.
     *
     * @param ruc Registro Único de Contribuyente a buscar.
     * @return Optional<Proveedor> con el proveedor encontrado, o vacío si no existe.
     *
     * El nombre findByRuc se traduce internamente en:
     * SELECT * FROM proveedores WHERE ruc = ? LIMIT 1
     */
    Optional<Proveedor> findByRuc(String ruc);

    /**
     * Busca un proveedor por su dirección de correo electrónico.
     *
     * @param email Correo electrónico a buscar.
     * @return Optional<Proveedor> con el proveedor encontrado, o vacío si no existe.
     */
    Optional<Proveedor> findByEmail(String email);

    /**
     * Obtiene todos los proveedores de una categoría específica.
     *
     * @param categoria Nombre de la categoría (ej: "CARNES", "BEBIDAS").
     * @return Lista de proveedores de esa categoría.
     */
    List<Proveedor> findByCategoria(String categoria);

    /**
     * Obtiene todos los proveedores activos.
     *
     * @return Lista de proveedores vigentes (no eliminados lógicamente).
     */
    List<Proveedor> findByActivoTrue();

    /**
     * Obtiene todos los proveedores activos ordenados alfabéticamente por nombre.
     *
     * @return Lista de proveedores activos ordenados ascendentemente por nombre.
     */
    List<Proveedor> findByActivoTrueOrderByNombreAsc();

    // ============================================================
    // CONSULTA PERSONALIZADA JPQL
    // ============================================================

    /**
     * Realiza una búsqueda difusa (like) sobre nombre, RUC y nombre del contacto.
     *
     * @param busqueda Término de búsqueda proporcionado por el usuario.
     * @return Lista de proveedores que coinciden parcialmente con el término.
     *
     * LOWER(...) se usa para hacer la búsqueda insensible a mayúsculas/minúsculas.
     * CONCAT('%', :busqueda, '%') genera el patrón LIKE para búsqueda parcial.
     */
    @Query("SELECT p FROM Proveedor p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "p.ruc LIKE CONCAT('%', :busqueda, '%') OR " +
            "LOWER(p.contacto) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Proveedor> buscar(String busqueda);
}
