package com.restaurante.pos.venta.repository;

import com.restaurante.pos.venta.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORIO DE VENTA
 * ====================
 *
 * CAPA: Repository (Acceso a Datos / Persistencia)
 * RESPONSABILIDAD: Abstraer el acceso a la base de datos para la entidad {@link Venta}.
 * Proporciona métodos para consultar el historial de ventas con diversos filtros
 * y agregaciones estadísticas.
 *
 * ¿QUÉ HACE?
 * - Hereda operaciones CRUD básicas de JpaRepository (save, findById, findAll, etc.).
 * - Define métodos de consulta derivados del nombre (Spring Data JPA los implementa automáticamente).
 * - Define consultas JPQL personalizadas con @Query para búsquedas complejas y agregaciones.
 *
 * ANOTACIONES SPRING:
 * - @Repository: Marca esta interfaz como componente de acceso a datos.
 *   Habilita la traducción de excepciones de persistencia.
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // ============================================
    // CONSULTAS DERIVADAS DEL NOMBRE
    // Spring Data JPA genera automáticamente la consulta SQL/JPQL.
    // ============================================

    /**
     * Busca ventas por método de pago.
     * @param metodoPago Método de pago (EFECTIVO, TARJETA, TRANSFERENCIA).
     * @return Lista de ventas con ese método de pago.
     */
    List<Venta> findByMetodoPago(String metodoPago);

    /**
     * Busca ventas asociadas a una mesa específica.
     * @param mesaNumero Número de la mesa.
     * @return Lista de ventas realizadas en esa mesa.
     */
    List<Venta> findByMesaNumero(Integer mesaNumero);

    /**
     * Busca ventas dentro de un rango de fechas.
     * @param inicio Fecha inicial.
     * @param fin    Fecha final.
     * @return Lista de ventas en el rango.
     */
    List<Venta> findByFechaBetween(LocalDate inicio, LocalDate fin);

    /**
     * Busca ventas de una fecha específica.
     * @param fecha Fecha a consultar.
     * @return Lista de ventas de ese día.
     */
    List<Venta> findByFecha(LocalDate fecha);

    /**
     * Busca ventas por estado.
     * @param estado Estado a filtrar (PENDIENTE, COMPLETADA, ANULADA).
     * @return Lista de ventas en el estado indicado.
     */
    List<Venta> findByEstado(String estado);

    // ============================================
    // CONSULTAS JPQL PERSONALIZADAS CON @Query
    // ============================================

    /**
     * Busca ventas aplicando múltiples filtros opcionales.
     * Cada condición solo se aplica si el parámetro correspondiente NO es null.
     * Esto permite búsquedas flexibles sin necesidad de múltiples métodos.
     *
     * @param fechaInicio  Fecha inicial (opcional).
     * @param fechaFin     Fecha final (opcional).
     * @param metodoPago   Método de pago (opcional).
     * @param mesaNumero   Número de mesa (opcional).
     * @param estado       Estado de venta (opcional).
     * @return Lista de ventas que cumplen los filtros aplicados.
     */
    @Query("SELECT v FROM Venta v WHERE " +
            "(:fechaInicio IS NULL OR v.fecha >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR v.fecha <= :fechaFin) AND " +
            "(:metodoPago IS NULL OR v.metodoPago = :metodoPago) AND " +
            "(:mesaNumero IS NULL OR v.mesaNumero = :mesaNumero) AND " +
            "(:estado IS NULL OR v.estado = :estado)")
    List<Venta> buscarConFiltros(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("metodoPago") String metodoPago,
            @Param("mesaNumero") Integer mesaNumero,
            @Param("estado") String estado);

    /**
     * Suma el total de todas las ventas COMPLETADAS en un rango de fechas.
     * Se usa para reportes de ingresos y estadísticas.
     * Solo incluye ventas con estado 'COMPLETADA' para excluir anuladas.
     *
     * @param inicio Fecha inicial.
     * @param fin    Fecha final.
     * @return Suma de los totales, o null si no hay ventas.
     */
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin AND v.estado = 'COMPLETADA'")
    Double sumTotalByFechaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    /**
     * Suma las propinas de todas las ventas COMPLETADAS en un rango de fechas.
     * Útil para reportes de propinas recibidas.
     *
     * @param inicio Fecha inicial.
     * @param fin    Fecha final.
     * @return Suma de las propinas, o null si no hay ventas.
     */
    @Query("SELECT SUM(v.propina) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin AND v.estado = 'COMPLETADA'")
    Double sumPropinasByFechaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    /**
     * Cuenta la cantidad de ventas COMPLETADAS en un rango de fechas.
     * Se usa para calcular ticket promedio y estadísticas de volumen.
     *
     * @param inicio Fecha inicial.
     * @param fin    Fecha final.
     * @return Cantidad de ventas completadas.
     */
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin AND v.estado = 'COMPLETADA'")
    Long countByFechaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
