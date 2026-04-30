package com.restaurante.pos.proveedor.repository;

import com.restaurante.pos.proveedor.entity.FacturaProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE FACTURA PROVEEDOR
 * ================================
 */
@Repository
public interface FacturaProveedorRepository extends JpaRepository<FacturaProveedor, Long> {

    /**
     * Busca factura por número.
     */
    Optional<FacturaProveedor> findByNumeroFactura(String numeroFactura);

    /**
     * Verifica si existe una factura con el número.
     */
    boolean existsByNumeroFactura(String numeroFactura);

    /**
     * Busca facturas por proveedor.
     */
    List<FacturaProveedor> findByProveedorId(Long proveedorId);

    /**
     * Busca facturas por estado.
     */
    List<FacturaProveedor> findByEstado(String estado);

    /**
     * Busca facturas por rango de fechas.
     */
    @Query("SELECT f FROM FacturaProveedor f WHERE f.fechaEmision BETWEEN :desde AND :hasta")
    List<FacturaProveedor> findByFechaEmisionBetween(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    /**
     * Busca facturas con filtros múltiples.
     */
    @Query("SELECT f FROM FacturaProveedor f WHERE " +
            "(:proveedorId IS NULL OR f.proveedor.id = :proveedorId) AND " +
            "(:numeroFactura IS NULL OR LOWER(f.numeroFactura) LIKE LOWER(CONCAT('%', :numeroFactura, '%'))) AND " +
            "(:estado IS NULL OR f.estado = :estado) AND " +
            "(:fechaDesde IS NULL OR f.fechaEmision >= :fechaDesde) AND " +
            "(:fechaHasta IS NULL OR f.fechaEmision <= :fechaHasta)")
    List<FacturaProveedor> buscarConFiltros(
            @Param("proveedorId") Long proveedorId,
            @Param("numeroFactura") String numeroFactura,
            @Param("estado") String estado,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta
    );

    /**
     * Cuenta facturas por proveedor.
     */
    long countByProveedorId(Long proveedorId);
}
