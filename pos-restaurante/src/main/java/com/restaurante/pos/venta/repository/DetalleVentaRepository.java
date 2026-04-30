package com.restaurante.pos.venta.repository;

import com.restaurante.pos.venta.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORIO DE DETALLE VENTA
 * ============================
 *
 * CAPA: Repository (Acceso a Datos / Persistencia)
 * RESPONSABILIDAD: Gestionar el acceso a la base de datos para la entidad {@link DetalleVenta}.
 * Proporciona consultas para analizar productos vendidos y estados de preparación.
 *
 * ¿QUÉ HACE?
 * - Hereda operaciones CRUD básicas de JpaRepository.
 * - Ofrece consultas por venta, producto y estado de preparación.
 * - Incluye reportes de productos más vendidos (ranking).
 *
 * ANOTACIONES SPRING:
 * - @Repository: Componente de acceso a datos detectado por Spring.
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    // ============================================
    // CONSULTAS DERIVADAS DEL NOMBRE
    // ============================================

    /**
     * Busca todos los detalles asociados a una venta específica.
     * @param ventaId Identificador de la venta.
     * @return Lista de detalles de esa venta.
     */
    List<DetalleVenta> findByVentaId(Long ventaId);

    /**
     * Busca todos los detalles que contengan un producto específico.
     * Útil para analizar el historial de ventas de un producto.
     * @param productoId Identificador del producto.
     * @return Lista de detalles con ese producto.
     */
    List<DetalleVenta> findByProductoId(Long productoId);

    /**
     * Busca detalles por estado de preparación.
     * Útil para la cocina: filtrar productos pendientes, en preparación, listos, etc.
     * @param estado Estado de preparación (PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO).
     * @return Lista de detalles en ese estado.
     */
    List<DetalleVenta> findByEstadoPreparacion(String estado);

    // ============================================
    // CONSULTAS JPQL PERSONALIZADAS CON @Query
    // ============================================

    /**
     * Obtiene el ranking de productos más vendidos de todos los tiempos.
     * Solo considera ventas completadas y activas.
     * Retorna un arreglo de objetos: [productoId, nombreProducto, cantidadTotalVendida].
     *
     * @return Lista de arreglos con el ranking de productos más vendidos.
     */
    @Query("SELECT dv.producto.id, dv.producto.nombre, SUM(dv.cantidad) as total " +
            "FROM DetalleVenta dv " +
            "WHERE dv.venta.estado = 'COMPLETADA' AND dv.venta.activo = true " +
            "GROUP BY dv.producto.id, dv.producto.nombre " +
            "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidos();

    /**
     * Obtiene el ranking de productos más vendidos en un rango de fechas.
     * Solo considera ventas completadas y activas dentro del período.
     * Retorna un arreglo de objetos: [productoId, nombreProducto, cantidadTotalVendida].
     *
     * @param desde Fecha inicial del período.
     * @param hasta Fecha final del período.
     * @return Lista de arreglos con el ranking de productos más vendidos.
     */
    @Query("SELECT dv.producto.id, dv.producto.nombre, SUM(dv.cantidad) as total " +
            "FROM DetalleVenta dv " +
            "WHERE dv.venta.fecha BETWEEN :desde AND :hasta " +
            "AND dv.venta.estado = 'COMPLETADA' AND dv.venta.activo = true " +
            "GROUP BY dv.producto.id, dv.producto.nombre " +
            "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidosPorFecha(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );
}
