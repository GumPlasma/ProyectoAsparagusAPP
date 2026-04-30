package com.restaurante.pos.cliente.repository;

import com.restaurante.pos.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE CLIENTE
 * ======================
 *
 * CAPA: Repository (Acceso a Datos)
 * RESPONSABILIDAD: Definir el contrato de acceso a datos para la entidad Cliente.
 * Abstrae las operaciones de lectura y escritura sobre la base de datos.
 *
 * ¿QUÉ HACE?
 * - Extiende JpaRepository para heredar métodos CRUD básicos (save, findById, findAll, deleteById).
 * - Define consultas personalizadas mediante derivación de nombres o JPQL.
 * - NO contiene lógica de negocio; solo recupera y persiste entidades.
 *
 * SPRING DATA JPA:
 * - Genera automáticamente la implementación de esta interfaz en tiempo de ejecución.
 * - Permite cambiar el motor de base de datos sin afectar las capas superiores.
 *
 * ANOTACIONES:
 * - @Repository: Marca la interfaz como componente de persistencia de Spring;
 *   convierte excepciones de BD en excepciones de Spring DataAccessException.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // ============================================================
    // CONSULTAS POR DERIVACIÓN DE NOMBRE (QUERY METHODS)
    // ============================================================
    // Spring Data JPA interpreta el nombre del método y genera automáticamente
    // la consulta SQL correspondiente. No es necesario escribir JPQL ni SQL nativo.

    /**
     * Busca un cliente por su número de DNI.
     *
     * @param dni Documento nacional de identidad a buscar.
     * @return Optional<Cliente> con el cliente encontrado, o vacío si no existe.
     *
     * El nombre findByDni se traduce internamente en:
     * SELECT * FROM clientes WHERE dni = ? LIMIT 1
     */
    Optional<Cliente> findByDni(String dni);

    /**
     * Busca un cliente por su dirección de correo electrónico.
     *
     * @param email Correo electrónico a buscar.
     * @return Optional<Cliente> con el cliente encontrado, o vacío si no existe.
     *
     * El nombre findByEmail se traduce internamente en:
     * SELECT * FROM clientes WHERE email = ? LIMIT 1
     */
    Optional<Cliente> findByEmail(String email);

    // ============================================================
    // CONSULTA PERSONALIZADA JPQL
    // ============================================================
    // Cuando la derivación de nombre no es suficiente, se usa @Query con JPQL.

    /**
     * Realiza una búsqueda difusa (like) sobre nombre, apellido, dni y teléfono.
     *
     * @param busqueda Término de búsqueda proporcionado por el usuario.
     * @return Lista de clientes que coinciden parcialmente con el término.
     *
     * @Query permite definir consultas JPQL explícitas vinculadas al método.
     *
     * LOWER(...) se usa en nombre y apellido para hacer la búsqueda insensible
     * a mayúsculas/minúsculas. CONCAT('%', :busqueda, '%') genera el patrón like.
     */
    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "c.dni LIKE CONCAT('%', :busqueda, '%') OR " +
            "c.telefono LIKE CONCAT('%', :busqueda, '%')")
    List<Cliente> buscar(String busqueda);

    // ============================================================
    // CONSULTAS DERIVADAS ADICIONALES
    // ============================================================

    /**
     * Recupera todos los clientes cuyo estado activo sea true.
     *
     * @return Lista de clientes vigentes (no eliminados lógicamente).
     *
     * El nombre findByActivoTrue se traduce internamente en:
     * SELECT * FROM clientes WHERE activo = true
     */
    List<Cliente> findByActivoTrue();
}
