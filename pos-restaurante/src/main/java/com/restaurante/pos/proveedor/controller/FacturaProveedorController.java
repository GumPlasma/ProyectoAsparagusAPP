package com.restaurante.pos.proveedor.controller;

import com.restaurante.pos.common.ApiResponse;
import com.restaurante.pos.proveedor.dto.CrearFacturaDTO;
import com.restaurante.pos.proveedor.entity.FacturaProveedor;
import com.restaurante.pos.proveedor.service.FacturaProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * CONTROLADOR REST DE FACTURAS DE PROVEEDOR
 * =========================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints para la gestión de compras a proveedores.
 */
@RestController
@RequestMapping("/facturas-proveedor")
@RequiredArgsConstructor
public class FacturaProveedorController {

    private final FacturaProveedorService facturaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FacturaProveedor>>> obtenerTodas() {
        List<FacturaProveedor> facturas = facturaService.obtenerTodas();
        return ResponseEntity.ok(ApiResponse.exito(facturas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FacturaProveedor>> obtenerPorId(@PathVariable Long id) {
        FacturaProveedor factura = facturaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito(factura));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<ApiResponse<List<FacturaProveedor>>> obtenerPorProveedor(@PathVariable Long proveedorId) {
        List<FacturaProveedor> facturas = facturaService.obtenerPorProveedor(proveedorId);
        return ResponseEntity.ok(ApiResponse.exito(facturas));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FacturaProveedor>> crear(
            @Valid @RequestBody CrearFacturaDTO dto) {
        FacturaProveedor factura = facturaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito(factura, "Factura registrada y stock actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> anular(@PathVariable Long id) {
        facturaService.anular(id);
        return ResponseEntity.ok(ApiResponse.exito("Factura anulada exitosamente"));
    }
}
