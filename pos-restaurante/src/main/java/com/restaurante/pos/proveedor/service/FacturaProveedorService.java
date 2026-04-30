package com.restaurante.pos.proveedor.service;

import com.restaurante.pos.inventario.service.InventarioService;
import com.restaurante.pos.producto.entity.Producto;
import com.restaurante.pos.producto.repository.ProductoRepository;
import com.restaurante.pos.proveedor.dto.CrearDetalleFacturaDTO;
import com.restaurante.pos.proveedor.dto.CrearFacturaDTO;
import com.restaurante.pos.proveedor.entity.DetalleFactura;
import com.restaurante.pos.proveedor.entity.FacturaProveedor;
import com.restaurante.pos.proveedor.entity.Proveedor;
import com.restaurante.pos.proveedor.repository.FacturaProveedorRepository;
import com.restaurante.pos.proveedor.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVICIO DE FACTURAS DE PROVEEDOR
 * =================================
 *
 * CAPA: Service (Lógica de Negocio)
 * RESPONSABILIDAD: Gestionar el ciclo de vida de las facturas de compra.
 *
 * ¿QUÉ HACE?
 * - Registra facturas de compra con sus detalles.
 * - Actualiza automáticamente el inventario al registrar una factura.
 * - Calcula totales (subtotal, impuestos, total).
 */
@Service
@RequiredArgsConstructor
public class FacturaProveedorService {

    private final FacturaProveedorRepository facturaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    /**
     * Lista todas las facturas registradas.
     */
    public List<FacturaProveedor> obtenerTodas() {
        return facturaRepository.findAll();
    }

    /**
     * Busca una factura por su ID.
     */
    public FacturaProveedor obtenerPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    /**
     * Busca facturas por proveedor.
     */
    public List<FacturaProveedor> obtenerPorProveedor(Long proveedorId) {
        return facturaRepository.findByProveedorId(proveedorId);
    }

    /**
     * REGISTRAR FACTURA DE COMPRA
     * ---------------------------
     * Crea una nueva factura de proveedor y actualiza el inventario.
     *
     * FLUJO:
     * 1. Valida que el proveedor exista.
     * 2. Valida que no exista otra factura con el mismo número.
     * 3. Crea los detalles validando productos.
     * 4. Calcula totales.
     * 5. Guarda la factura.
     * 6. Registra entrada de inventario por cada producto.
     * 7. Marca la factura como PROCESADA.
     */
    @Transactional
    public FacturaProveedor crear(CrearFacturaDTO dto) {
        // Validar proveedor
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // Validar número de factura único
        if (facturaRepository.existsByNumeroFactura(dto.getNumeroFactura())) {
            throw new RuntimeException("Ya existe una factura con ese número");
        }

        // Crear factura
        FacturaProveedor factura = new FacturaProveedor();
        factura.setNumeroFactura(dto.getNumeroFactura());
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setFechaRecepcion(dto.getFechaRecepcion() != null ? dto.getFechaRecepcion() : LocalDate.now());
        factura.setPorcentajeImpuesto(dto.getPorcentajeImpuesto() != null ? dto.getPorcentajeImpuesto() : new BigDecimal("18.00"));
        factura.setObservaciones(dto.getObservaciones());
        factura.setProveedor(proveedor);
        factura.setEstado("PENDIENTE");

        // Crear detalles
        List<DetalleFactura> detalles = new ArrayList<>();
        for (CrearDetalleFacturaDTO detalleDTO : dto.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleDTO.getProductoId()));

            DetalleFactura detalle = new DetalleFactura();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setDescripcionAdicional(detalleDTO.getDescripcionAdicional());
            detalle.calcularSubtotal();
            detalle.setFactura(factura);
            detalles.add(detalle);
        }
        factura.setDetalles(detalles);
        factura.calcularTotal();

        // Guardar factura
        factura = facturaRepository.save(factura);

        // Registrar entrada de inventario por cada detalle
        for (DetalleFactura detalle : factura.getDetalles()) {
            inventarioService.registrarEntradaCompra(
                    detalle.getProducto().getId(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    factura.getId(),
                    null
            );
        }

        // Marcar como procesada
        factura.setEstado("PROCESADA");
        factura = facturaRepository.save(factura);

        return factura;
    }

    /**
     * ANULAR FACTURA
     * --------------
     * Cambia el estado de la factura a ANULADA.
     * Nota: En un sistema completo, aquí también se revertiría el inventario.
     */
    @Transactional
    public void anular(Long id) {
        FacturaProveedor factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if ("ANULADA".equals(factura.getEstado())) {
            throw new RuntimeException("La factura ya está anulada");
        }

        factura.setEstado("ANULADA");
        facturaRepository.save(factura);
    }
}
