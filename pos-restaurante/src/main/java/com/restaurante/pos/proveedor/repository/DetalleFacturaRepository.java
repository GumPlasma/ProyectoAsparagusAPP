package com.restaurante.pos.proveedor.repository;

import com.restaurante.pos.proveedor.entity.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORIO DE DETALLE FACTURA
 * ==============================
 */
@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    /**
     * Busca detalles por factura.
     */
    List<DetalleFactura> findByFacturaId(Long facturaId);

    /**
     * Busca detalles por producto.
     * Útil para ver historial de compras de un producto.
     */
    List<DetalleFactura> findByProductoIdOrderByFechaCreacionDesc(Long productoId);
}
