package com.restaurante.pos.mesa.controller;

import com.restaurante.pos.mesa.dto.*;
import com.restaurante.pos.mesa.service.MesaService;
import com.restaurante.pos.venta.entity.Venta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR REST DEL MÓDULO MESA
 * =================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints HTTP que permiten a los clientes (frontend,
 * aplicaciones móviles, etc.) interactuar con el sistema de mesas del restaurante.
 *
 * ¿QUÉ HACE?
 * - Recibe peticiones HTTP (GET, POST, PUT, DELETE) relacionadas con mesas.
 * - Delega toda la lógica de negocio al {@link MesaService}.
 * - Convierte los datos de entrada (JSON) en DTOs y los pasa al servicio.
 * - Retorna las respuestas al cliente en formato JSON.
 *
 * ANOTACIONES SPRING:
 * - @RestController: Indica que esta clase es un controlador REST. Combina @Controller
 *   y @ResponseBody, lo que significa que todos los métodos retornan datos directamente
 *   en el cuerpo de la respuesta HTTP (generalmente JSON).
 * - @RequestMapping("/mesas"): Define la ruta base para todos los endpoints de esta clase.
 *   Todas las URLs empezarán con /mesas.
 * - @RequiredArgsConstructor: Genera un constructor con todos los campos 'final'. Spring
 *   inyecta automáticamente el {@link MesaService} por constructor (inyección de dependencias).
 */
@RestController
@RequestMapping("/mesas")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class MesaController {

    // Inyección del servicio de mesas. 'final' asegura que no se pueda reasignar.
    private final MesaService mesaService;

    // ============================================
    // OPERACIONES CRUD BÁSICAS
    // ============================================

    /**
     * OBTENER TODAS LAS MESAS
     * -----------------------
     * GET /mesas
     * Retorna la lista completa de mesas registradas en el sistema,
     * incluyendo su estado, posición y pedidos activos.
     */
    @GetMapping
    public List<MesaDTO> obtenerTodas() {
        return mesaService.obtenerTodas();
    }

    /**
     * OBTENER UNA MESA POR ID
     * -----------------------
     * GET /mesas/{id}
     * Retorna los detalles de una mesa específica identificada por su ID.
     */
    @GetMapping("/{id}")
    public MesaDTO obtenerPorId(@PathVariable Long id) {
        return mesaService.obtenerPorId(id);
    }

    /**
     * OBTENER ESTADÍSTICAS DE MESAS
     * -----------------------------
     * GET /mesas/estadisticas
     * Retorna un resumen con cantidad de mesas libres, ocupadas, reservadas
     * y el total acumulado de ventas activas. Útil para el dashboard del POS.
     */
    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {
        return mesaService.obtenerEstadisticas();
    }

    /**
     * CREAR UNA NUEVA MESA
     * --------------------
     * POST /mesas
     * Recibe un JSON con los datos de la nueva mesa (número, capacidad, posición)
     * y la registra en el sistema con estado inicial "LIBRE".
     */
    @PostMapping
    public MesaDTO crear(@RequestBody MesaDTO dto) {
        return mesaService.crear(dto);
    }

    /**
     * ACTUALIZAR DATOS DE UNA MESA
     * ----------------------------
     * PUT /mesas/{id}
     * Permite modificar la capacidad y posición de una mesa existente.
     */
    @PutMapping("/{id}")
    public MesaDTO actualizar(@PathVariable Long id, @RequestBody MesaDTO dto) {
        return mesaService.actualizar(id, dto);
    }

    /**
     * ACTUALIZAR POSICIÓN VISUAL DE LA MESA
     * -------------------------------------
     * PUT /mesas/{id}/posicion
     * Actualiza únicamente las coordenadas X e Y de la mesa en el mapa visual
     * del restaurante. Se usa cuando el usuario arrastra la mesa en la interfaz.
     */
    @PutMapping("/{id}/posicion")
    public MesaDTO actualizarPosicion(
            @PathVariable Long id,
            @RequestParam Integer posicionX,
            @RequestParam Integer posicionY) {
        return mesaService.actualizarPosicion(id, posicionX, posicionY);
    }

    /**
     * ELIMINAR UNA MESA
     * -----------------
     * DELETE /mesas/{id}
     * Elimina una mesa del sistema. Solo permite eliminar mesas que no estén
     * actualmente ocupadas, para evitar pérdida de datos de ventas en curso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    // ============================================
    // OPERACIONES DE GESTIÓN DE MESA (CICLO DE VIDA)
    // ============================================

    /**
     * ABRIR UNA MESA (INICIAR ATENCIÓN)
     * ---------------------------------
     * POST /mesas/{id}/abrir
     * Cambia el estado de la mesa a "OCUPADA", registra la hora de apertura
     * y prepara la mesa para recibir pedidos de productos.
     */
    @PostMapping("/{id}/abrir")
    public MesaDTO abrirMesa(@PathVariable Long id) {
        return mesaService.abrirMesa(id);
    }

    /**
     * AGREGAR PRODUCTO A UNA MESA
     * ---------------------------
     * POST /mesas/{id}/productos
     * Recibe un producto y cantidad para agregarlo al pedido activo de la mesa.
     * Actualiza automáticamente el total acumulado del pedido.
     */
    @PostMapping("/{id}/productos")
    public MesaDTO agregarProducto(@PathVariable Long id, @RequestBody PedidoMesaDTO dto) {
        return mesaService.agregarProducto(id, dto);
    }

    /**
     * ELIMINAR PRODUCTO DE UNA MESA
     * -----------------------------
     * DELETE /mesas/{mesaId}/productos/{pedidoId}
     * Quita un producto específico del pedido de la mesa y recalcula el total.
     * Se identifica el pedido por su ID interno (pedidoId).
     */
    @DeleteMapping("/{mesaId}/productos/{pedidoId}")
    public MesaDTO eliminarProducto(@PathVariable Long mesaId, @PathVariable Long pedidoId) {
        return mesaService.eliminarProducto(mesaId, pedidoId);
    }

    /**
     * ACTUALIZAR PROPINA DE LA MESA
     * -----------------------------
     * PUT /mesas/{id}/propina
     * Permite establecer o modificar el monto de propina para el pedido actual.
     */
    @PutMapping("/{id}/propina")
    public MesaDTO actualizarPropina(@PathVariable Long id, @RequestParam BigDecimal propina) {
        return mesaService.actualizarPropina(id, propina);
    }

    /**
     * PROCESAR PAGO DE UNA MESA
     * -------------------------
     * POST /mesas/{id}/pagar
     * Convierte todos los pedidos de la mesa en una venta formal,
     * registra el método de pago, calcula el vuelto si es efectivo,
     * limpia la mesa y la deja en estado "LIBRE".
     */
    @PostMapping("/{id}/pagar")
    public Venta procesarPago(@PathVariable Long id, @RequestBody PagoMesaDTO dto) {
        dto.setMesaId(id);
        return mesaService.procesarPago(dto);
    }
}
