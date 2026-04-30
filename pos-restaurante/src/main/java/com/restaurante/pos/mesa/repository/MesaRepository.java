package com.restaurante.pos.mesa.repository;

import com.restaurante.pos.mesa.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE MESA
 * ===================
 *
 * CAPA: Repository (Acceso a Datos / Persistencia)
 * RESPONSABILIDAD: Abstraer el acceso a la base de datos para la entidad {@link Mesa}.
 * Proporciona métodos para crear, leer, actualizar y eliminar registros de mesas,
 * así como consultas personalizadas para reportes y estadísticas.
 *
 * ¿QUÉ HACE?
 * - Extiende JpaRepository, lo que hereda automáticamente métodos CRUD básicos
 *   como save(), findById(), findAll(), deleteById(), count(), etc.
 * - Define métodos de consulta derivados del nombre (Spring Data JPA los implementa
 *   automáticamente en tiempo de ejecución).
 * - Define consultas JPQL personalizadas con @Query para estadísticas complejas.
 *
 * ANOTACIONES SPRING:
 * - @Repository: Marca esta interfaz como un componente de acceso a datos de Spring.
 *   Además, habilita la traducción de excepciones de persistencia a excepciones
 *   de Spring DataAccessException.
 *
 * @param <Mesa> Tipo de entidad que gestiona.
 * @param <Long> Tipo de la clave primaria de la entidad.
 */
@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // ============================================
    // CONSULTAS DERIVADAS DEL NOMBRE
    // Spring Data JPA genera automáticamente la consulta SQL/JPQL.
    // ============================================

    /**
     * Busca una mesa por su número visible.
     * @param numero Número de la mesa (ej: 1, 2, 3...).
     * @return Optional con la mesa encontrada, o vacío si no existe.
     */
    Optional<Mesa> findByNumero(Integer numero);

    /**
     * Busca todas las mesas que tengan un estado específico.
     * Útil para filtrar mesas libres, ocupadas o reservadas.
     * @param estado Estado a buscar (LIBRE, OCUPADA, RESERVADA, PAGADA).
     * @return Lista de mesas en el estado indicado.
     */
    List<Mesa> findByEstado(String estado);

    // ============================================
    // CONSULTAS JPQL PERSONALIZADAS CON @Query
    // ============================================

    /**
     * Cuenta la cantidad de mesas con estado "LIBRE".
     * Se usa en el dashboard para mostrar disponibilidad.
     * @return Cantidad de mesas libres.
     */
    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.estado = 'LIBRE'")
    Long countLibres();

    /**
     * Cuenta la cantidad de mesas con estado "OCUPADA".
     * Se usa en el dashboard para mostrar ocupación actual.
     * @return Cantidad de mesas ocupadas.
     */
    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.estado = 'OCUPADA'")
    Long countOcupadas();

    /**
     * Cuenta la cantidad de mesas con estado "RESERVADA".
     * @return Cantidad de mesas reservadas.
     */
    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.estado = 'RESERVADA'")
    Long countReservadas();

    /**
     * Suma el total de pedidos de todas las mesas activas (OCUPADA o PAGADA).
     * Representa el monto total de ventas en curso en el restaurante.
     * COALESCE devuelve 0 si no hay mesas activas (evita null).
     * @return Suma de totalPedido de mesas activas.
     */
    @Query("SELECT COALESCE(SUM(m.totalPedido), 0) FROM Mesa m WHERE m.estado = 'OCUPADA' OR m.estado = 'PAGADA'")
    Double sumVentasActivas();
}
