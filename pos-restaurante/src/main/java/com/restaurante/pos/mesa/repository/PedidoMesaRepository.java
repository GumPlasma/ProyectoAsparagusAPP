package com.restaurante.pos.mesa.repository;

import com.restaurante.pos.mesa.entity.PedidoMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * REPOSITORIO DE PEDIDO MESA
 * ==========================
 *
 * CAPA: Repository (Acceso a Datos / Persistencia)
 * RESPONSABILIDAD: Gestionar el acceso a la base de datos para la entidad {@link PedidoMesa},
 * que representa cada línea de producto pedida en una mesa.
 *
 * ¿QUÉ HACE?
 * - Proporciona operaciones CRUD heredadas de JpaRepository.
 * - Ofrece consultas específicas para obtener los pedidos de una mesa.
 * - Incluye operaciones de eliminación masiva por mesa (útil al cerrar/pagar una mesa).
 *
 * ANOTACIONES ESPECIALES:
 * - @Modifying: Indica que la consulta @Query modifica datos (INSERT, UPDATE, DELETE).
 *   Es obligatoria en métodos que ejecuten DELETE o UPDATE para que JPA/hibernate
 *   sincronice correctamente el contexto de persistencia.
 * - @Param: Vincula un parámetro del método con un parámetro nombrado en la consulta JPQL.
 */
@Repository
public interface PedidoMesaRepository extends JpaRepository<PedidoMesa, Long> {

    // ============================================
    // CONSULTAS DERIVADAS DEL NOMBRE
    // ============================================

    /**
     * Obtiene todos los pedidos de una mesa específica, ordenados del más reciente al más antiguo.
     * El orden descendente por fechaHora permite mostrar primero los últimos pedidos realizados.
     * @param mesaId Identificador de la mesa.
     * @return Lista de pedidos ordenados cronológicamente (descendente).
     */
    List<PedidoMesa> findByMesaIdOrderByFechaHoraDesc(Long mesaId);

    /**
     * Obtiene todos los pedidos de una mesa sin orden específico.
     * @param mesaId Identificador de la mesa.
     * @return Lista de pedidos asociados a la mesa.
     */
    List<PedidoMesa> findByMesaId(Long mesaId);

    // ============================================
    // OPERACIONES DE MODIFICACIÓN CON @Query
    // ============================================

    /**
     * Elimina TODOS los pedidos asociados a una mesa.
     * Se usa al procesar el pago de una mesa para limpiar sus pedidos.
     * @Modifying es obligatorio porque esta consulta ejecuta un DELETE.
     *
     * @param mesaId Identificador de la mesa cuyos pedidos se eliminarán.
     */
    @Modifying
    @Query("DELETE FROM PedidoMesa p WHERE p.mesa.id = :mesaId")
    void deleteByMesaId(@Param("mesaId") Long mesaId);

    /**
     * Elimina un pedido específico por su ID.
     * Aunque JpaRepository ya tiene deleteById, este método permite usar
     * una consulta JPQL explícita si se requiere alguna lógica adicional en el futuro.
     * @Modifying indica que es una operación de escritura.
     *
     * @param id Identificador del pedido a eliminar.
     */
    @Modifying
    @Query("DELETE FROM PedidoMesa p WHERE p.id = :id")
    void deleteById(@Param("id") Long id);
}
