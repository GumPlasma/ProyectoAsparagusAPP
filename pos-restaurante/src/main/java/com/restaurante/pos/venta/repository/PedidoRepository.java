package com.restaurante.pos.venta.repository;

import com.restaurante.pos.venta.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORIO DE PEDIDO
 * =====================
 *
 * CAPA: Repository (Acceso a Datos / Persistencia)
 * RESPONSABILIDAD: Gestionar el acceso a la base de datos para la entidad {@link Pedido}.
 * Proporciona consultas para el seguimiento y gestión de pedidos en el restaurante.
 *
 * ¿QUÉ HACE?
 * - Hereda operaciones CRUD básicas de JpaRepository.
 * - Ofrece consultas por estado, mesa, venta y rangos de fecha.
 * - Incluye consultas específicas para la cocina (pedidos pendientes, pedidos activos).
 *
 * ANOTACIONES SPRING:
 * - @Repository: Componente de acceso a datos detectado por Spring.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // ============================================
    // CONSULTAS DERIVADAS DEL NOMBRE
    // ============================================

    /**
     * Busca pedidos por estado.
     * @param estado Estado a filtrar (PENDIENTE, ENVIADO_COCINA, EN_PREPARACION, LISTO, COMPLETADO).
     * @return Lista de pedidos en el estado indicado.
     */
    List<Pedido> findByEstado(String estado);

    /**
     * Busca pedidos asociados a una mesa específica.
     * @param mesaId Identificador de la mesa.
     * @return Lista de pedidos de esa mesa.
     */
    List<Pedido> findByMesaId(Long mesaId);

    /**
     * Busca pedidos asociados a una venta específica.
     * @param ventaId Identificador de la venta.
     * @return Lista de pedidos vinculados a esa venta.
     */
    List<Pedido> findByVentaId(Long ventaId);

    // ============================================
    // CONSULTAS JPQL PERSONALIZADAS CON @Query
    // ============================================

    /**
     * Busca pedidos por estado ordenados por fecha y hora (ascendente).
     * Útil para mostrar la cola de pedidos en orden cronológico.
     *
     * @param estado Estado a filtrar.
     * @return Lista de pedidos ordenados del más antiguo al más reciente.
     */
    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado ORDER BY p.fechaHora")
    List<Pedido> findByEstadoOrderByFechaHora(@Param("estado") String estado);

    /**
     * Obtiene los pedidos que están pendientes en cocina.
     * Estado: 'ENVIADO_COCINA' y activo = true.
     * Ordenados por fecha y hora (los más antiguos primero).
     *
     * @return Lista de pedidos pendientes de preparación en cocina.
     */
    @Query("SELECT p FROM Pedido p WHERE p.estado = 'ENVIADO_COCINA' AND p.activo = true ORDER BY p.fechaHora")
    List<Pedido> findPedidosPendientesCocina();

    /**
     * Obtiene todos los pedidos activos (no completados ni cancelados).
     * Estados considerados: PENDIENTE, ENVIADO_COCINA, EN_PREPARACION.
     * Ordenados por fecha y hora para priorizar los más antiguos.
     *
     * @return Lista de pedidos actualmente en curso.
     */
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('PENDIENTE', 'ENVIADO_COCINA', 'EN_PREPARACION') ORDER BY p.fechaHora")
    List<Pedido> findPedidosActivos();

    /**
     * Busca pedidos dentro de un rango de fecha y hora.
     * Útil para reportes de actividad en horarios específicos.
     *
     * @param inicio Fecha/hora inicial.
     * @param fin    Fecha/hora final.
     * @return Lista de pedidos en el rango especificado.
     */
    @Query("SELECT p FROM Pedido p WHERE p.fechaHora BETWEEN :inicio AND :fin")
    List<Pedido> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
