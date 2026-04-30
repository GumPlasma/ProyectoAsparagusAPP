package com.restaurante.pos.venta.controller;

import com.restaurante.pos.common.ApiResponse;
import com.restaurante.pos.venta.dto.CrearVentaDTO;
import com.restaurante.pos.venta.entity.DetalleVenta;
import com.restaurante.pos.venta.entity.Venta;
import com.restaurante.pos.venta.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR REST DEL MÓDULO VENTA
 * =================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints HTTP para la gestión de ventas del restaurante.
 * Permite consultar ventas históricas, buscar con filtros, obtener estadísticas
 * y anular transacciones.
 *
 * ¿QUÉ HACE?
 * - Recibe peticiones HTTP relacionadas con ventas completadas.
 * - Delega la lógica de negocio al {@link VentaService}.
 * - Retorna datos de ventas, detalles y estadísticas en formato JSON.
 *
 * ANOTACIONES SPRING:
 * - @RestController: Controlador REST que retorna datos directamente en el body.
 * - @RequestMapping("/ventas"): Ruta base para todos los endpoints de ventas.
 * - @RequiredArgsConstructor: Inyección de dependencias por constructor.
 * - @DateTimeFormat(iso = DateTimeFormat.ISO.DATE): Parsea parámetros de fecha
 *   en formato ISO (yyyy-MM-dd) automáticamente.
 */
@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class VentaController {

    // Servicio de ventas inyectado por constructor.
    private final VentaService ventaService;

    // ============================================
    // OPERACIONES DE CONSULTA
    // ============================================

    /**
     * OBTENER TODAS LAS VENTAS
     * ------------------------
     * GET /ventas
     * Retorna el listado completo de ventas registradas en el sistema.
     */
    @GetMapping
    public List<Venta> obtenerTodas() {
        return ventaService.obtenerTodas();
    }

    /**
     * OBTENER VENTA POR ID
     * --------------------
     * GET /ventas/{id}
     * Retorna los detalles completos de una venta específica.
     */
    @GetMapping("/{id}")
    public Venta obtenerPorId(@PathVariable Long id) {
        return ventaService.obtenerPorId(id);
    }

    /**
     * BUSCAR VENTAS CON FILTROS
     * -------------------------
     * GET /ventas/buscar
     * Permite buscar ventas aplicando múltiples filtros opcionales:
     * rango de fechas, método de pago, número de mesa y estado.
     * Todos los parámetros son opcionales (required = false).
     */
    @GetMapping("/buscar")
    public List<Venta> buscar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) Integer mesaNumero,
            @RequestParam(required = false) String estado) {
        return ventaService.buscar(fechaInicio, fechaFin, metodoPago, mesaNumero, estado);
    }

    /**
     * OBTENER VENTAS POR RANGO DE FECHAS
     * ----------------------------------
     * GET /ventas/fecha?inicio=...&fin=...
     * Retorna las ventas realizadas dentro de un rango de fechas específico.
     */
    @GetMapping("/fecha")
    public List<Venta> getByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ventaService.obtenerPorFecha(inicio, fin);
    }

    /**
     * OBTENER VENTAS POR ESTADO
     * -------------------------
     * GET /ventas/estado/{estado}
     * Filtra ventas según su estado (PENDIENTE, COMPLETADA, ANULADA, etc.).
     */
    @GetMapping("/estado/{estado}")
    public List<Venta> getByEstado(@PathVariable String estado) {
        return ventaService.obtenerPorEstado(estado);
    }

    /**
     * OBTENER VENTAS POR NÚMERO DE MESA
     * ---------------------------------
     * GET /ventas/mesa/{mesaNumero}
     * Retorna todas las ventas asociadas a una mesa específica.
     * Útil para consultar el historial de consumo de una mesa.
     */
    @GetMapping("/mesa/{mesaNumero}")
    public List<Venta> getByMesa(@PathVariable Integer mesaNumero) {
        return ventaService.obtenerPorMesa(mesaNumero);
    }

    // ============================================
    // ESTADÍSTICAS Y REPORTES
    // ============================================

    /**
     * OBTENER ESTADÍSTICAS DE VENTAS
     * ------------------------------
     * GET /ventas/estadisticas?inicio=...&fin=...
     * Retorna métricas agregadas (total de ventas, total de propinas, cantidad de ventas)
     * dentro del rango de fechas especificado.
     */
    @GetMapping("/estadisticas")
    public Map<String, Object> getEstadisticas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ventaService.obtenerEstadisticas(inicio, fin);
    }

    /**
     * OBTENER RESUMEN DEL DÍA
     * -----------------------
     * GET /ventas/resumen/{fecha}
     * Retorna un resumen simplificado de las ventas de un día específico:
     * cantidad de ventas, monto total y fecha.
     */
    @GetMapping("/resumen/{fecha}")
    public Map<String, Object> getResumenDia(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ventaService.obtenerResumenDia(fecha);
    }

    // ============================================
    // OPERACIONES DE MODIFICACIÓN
    // ============================================

    /**
     * CREAR VENTA DIRECTA (POS / Para llevar / Delivery)
     * ----------------------------------------------------
     * POST /ventas/directa
     * Crea una venta sin pasar por el módulo de mesas.
     * Utilizado por la interfaz de cajero (POS) para ventas rápidas.
     */
    @PostMapping("/directa")
    public ResponseEntity<ApiResponse<Venta>> crearVentaDirecta(@Valid @RequestBody CrearVentaDTO dto) {
        Venta venta = ventaService.crearVentaDirecta(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito(venta, "Venta procesada exitosamente"));
    }

    // ============================================
    // ENDPOINTS DE COCINA
    // ============================================

    /**
     * ACTUALIZAR ESTADO DE PREPARACIÓN DE UN DETALLE
     * -----------------------------------------------
     * PUT /ventas/detalles/{detalleId}/estado-preparacion
     * Permite a cocina marcar productos como EN_PREPARACION, LISTO o ENTREGADO.
     */
    @PutMapping("/detalles/{detalleId}/estado-preparacion")
    public ResponseEntity<ApiResponse<DetalleVenta>> actualizarEstadoPreparacion(
            @PathVariable Long detalleId,
            @RequestParam String estado) {
        DetalleVenta detalle = ventaService.actualizarEstadoPreparacion(detalleId, estado);
        return ResponseEntity.ok(ApiResponse.exito(detalle, "Estado actualizado a: " + estado));
    }

    /**
     * OBTENER DETALLES POR ESTADO DE PREPARACIÓN
     * ------------------------------------------
     * GET /ventas/detalles/estado-preparacion/{estado}
     * Útil para la vista de cocina: lista todos los productos pendientes, en preparación, etc.
     */
    @GetMapping("/detalles/estado-preparacion/{estado}")
    public ResponseEntity<ApiResponse<List<DetalleVenta>>> obtenerDetallesPorEstadoPreparacion(
            @PathVariable String estado) {
        List<DetalleVenta> detalles = ventaService.obtenerDetallesPorEstadoPreparacion(estado);
        return ResponseEntity.ok(ApiResponse.exito(detalles));
    }

    /**
     * ANULAR UNA VENTA
     * ----------------
     * DELETE /ventas/{id}
     * Cambia el estado de una venta a "ANULADA" en lugar de eliminarla físicamente.
     * Esto preserva el registro para auditoría y reportes históricos.
     */
    @DeleteMapping("/{id}")
    public void anular(@PathVariable Long id) {
        ventaService.eliminar(id);
    }
}
