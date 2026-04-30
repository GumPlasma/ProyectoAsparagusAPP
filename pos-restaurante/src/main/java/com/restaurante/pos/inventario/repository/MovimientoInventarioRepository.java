package com.restaurante.pos.inventario.repository;

import com.restaurante.pos.inventario.entity.MovimientoInventario;
import com.restaurante.pos.inventario.entity.MotivoMovimiento;
import com.restaurante.pos.inventario.entity.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORIO DE MOVIMIENTO DE INVENTARIO
 * =======================================
 *
 * CAPA: Repository (Repositorio de Datos / Acceso a BD)
 * RESPONSABILIDAD: Abstraer el acceso a la base de datos para la entidad MovimientoInventario.
 * Gestiona la tabla "movimiento_inventario" que almacena el historial completo
 * de todos los cambios de stock.
 *
 * QUÉ HACE:
 * - Hereda de JpaRepository para operaciones CRUD básicas.
 * - Define métodos de consulta por derivación de nombres (Spring Data JPA los implementa automáticamente).
 * - Define consultas JPQL personalizadas para búsquedas complejas con múltiples filtros.
 *
 * ANOTACIONES SPRING:
 * - @Repository: Identifica esta interfaz como componente de persistencia de Spring,
 *   habilitando la traducción de excepciones y la inyección de dependencias.
 *
 * INTERFAZ JpaRepository<MovimientoInventario, Long>:
 * - Primer parámetro: Entidad gestionada (MovimientoInventario).
 * - Segundo parámetro: Tipo de la clave primaria (Long).
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /**
     * Busca movimientos de un inventario específico ordenados por fecha descendente.
     *
     * POR QUÉ EXISTE: Permite ver el historial de un producto con los movimientos
     * más recientes primero, facilitando la auditoría.
     *
     * Spring Data JPA deriva automáticamente la consulta a partir del nombre:
     * findByInventarioIdOrderByFechaMovimientoDesc ->
     * SELECT ... WHERE inventario_id = ? ORDER BY fecha_movimiento DESC
     *
     * @param inventarioId ID del inventario (registro de stock de un producto).
     * @return Lista de movimientos ordenados del más reciente al más antiguo.
     */
    List<MovimientoInventario> findByInventarioIdOrderByFechaMovimientoDesc(Long inventarioId);

    /**
     * Busca movimientos filtrados por tipo (ENTRADA o SALIDA).
     *
     * @param tipo Tipo de movimiento a filtrar.
     * @return Lista de movimientos del tipo solicitado, ordenados por fecha descendente.
     */
    List<MovimientoInventario> findByTipoOrderByFechaMovimientoDesc(TipoMovimiento tipo);

    /**
     * Busca movimientos filtrados por motivo (COMPRA, VENTA, MERMA, etc.).
     *
     * @param motivo Motivo de movimiento a filtrar.
     * @return Lista de movimientos del motivo solicitado, ordenados por fecha descendente.
     */
    List<MovimientoInventario> findByMotivoOrderByFechaMovimientoDesc(MotivoMovimiento motivo);

    /**
     * Busca movimientos dentro de un rango de fechas.
     *
     * CONSULTA JPQL:
     * - BETWEEN :desde AND :hasta: Filtra registros cuya fecha esté dentro del rango inclusive.
     * - ORDER BY ... DESC: Ordena del más reciente al más antiguo.
     *
     * @param desde Fecha y hora inicial del rango.
     * @param hasta Fecha y hora final del rango.
     * @return Lista de movimientos dentro del rango especificado.
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento BETWEEN :desde AND :hasta ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findByFechaMovimientoBetween(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    /**
     * Busca movimientos realizados por un usuario específico.
     *
     * @param usuarioId ID del usuario.
     * @return Lista de movimientos del usuario, ordenados por fecha descendente.
     */
    List<MovimientoInventario> findByUsuarioIdOrderByFechaMovimientoDesc(Long usuarioId);

    /**
     * Busca movimientos asociados a una venta específica.
     *
     * POR QUÉ EXISTE: Permite trazar qué movimientos de stock se generaron
     * a partir de una venta determinada, útil para auditorías y reversos.
     *
     * @param ventaId ID de la venta.
     * @return Lista de movimientos ligados a esa venta.
     */
    List<MovimientoInventario> findByVentaId(Long ventaId);

    /**
     * Busca movimientos asociados a una factura de proveedor específica.
     *
     * @param facturaProveedorId ID de la factura del proveedor.
     * @return Lista de movimientos ligados a esa factura.
     */
    List<MovimientoInventario> findByFacturaProveedorId(Long facturaProveedorId);

    /**
     * Búsqueda avanzada con múltiples filtros opcionales.
     *
     * CONSULTA JPQL:
     * - Cada condición usa "(:param IS NULL OR campo = :param)" para hacer el filtro opcional.
     * - Si el parámetro viene en null, esa condición se ignora.
     * - Esto permite combinar filtros de forma flexible sin crear múltiples métodos.
     *
     * POR QUÉ EXISTE: Los reportes de auditoría permiten filtrar por producto,
     * tipo, motivo, usuario y rango de fechas simultáneamente.
     *
     * @param inventarioId ID del inventario (opcional).
     * @param tipo         Tipo de movimiento (opcional).
     * @param motivo       Motivo de movimiento (opcional).
     * @param usuarioId    ID del usuario (opcional).
     * @param fechaDesde   Fecha inicial (opcional).
     * @param fechaHasta   Fecha final (opcional).
     * @return Lista de movimientos que cumplen los filtros aplicados.
     */
    @Query("SELECT m FROM MovimientoInventario m WHERE " +
            "(:inventarioId IS NULL OR m.inventario.id = :inventarioId) AND " +
            "(:tipo IS NULL OR m.tipo = :tipo) AND " +
            "(:motivo IS NULL OR m.motivo = :motivo) AND " +
            "(:usuarioId IS NULL OR m.usuario.id = :usuarioId) AND " +
            "(:fechaDesde IS NULL OR m.fechaMovimiento >= :fechaDesde) AND " +
            "(:fechaHasta IS NULL OR m.fechaMovimiento <= :fechaHasta) " +
            "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> buscarConFiltros(
            @Param("inventarioId") Long inventarioId,
            @Param("tipo") TipoMovimiento tipo,
            @Param("motivo") MotivoMovimiento motivo,
            @Param("usuarioId") Long usuarioId,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta
    );

    /**
     * Cuenta movimientos de un tipo específico dentro de un período.
     *
     * CONSULTA JPQL: COUNT(m) retorna el número de registros que cumplen las condiciones.
     *
     * POR QUÉ EXISTE: Permite generar estadísticas de actividad de inventario
     * (por ejemplo, "cuántas entradas hubo esta semana").
     *
     * @param tipo  Tipo de movimiento a contar.
     * @param desde Inicio del período.
     * @param hasta Fin del período.
     * @return Cantidad de movimientos del tipo en el rango de fechas.
     */
    @Query("SELECT COUNT(m) FROM MovimientoInventario m WHERE m.tipo = :tipo AND m.fechaMovimiento BETWEEN :desde AND :hasta")
    long countByTipoAndFechaBetween(
            @Param("tipo") TipoMovimiento tipo,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
