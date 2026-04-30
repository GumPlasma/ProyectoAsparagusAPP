package com.restaurante.pos.inventario.controller;

import com.restaurante.pos.common.ApiResponse;
import com.restaurante.pos.inventario.dto.*;
import com.restaurante.pos.inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLADOR DE INVENTARIO
 * =========================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints HTTP/REST para que clientes externos
 * (frontend, apps móviles, otros servicios) interactúen con el módulo de inventario.
 *
 * QUÉ HACE:
 * - Recibe peticiones HTTP (GET, POST, PUT) relacionadas con inventario y movimientos.
 * - Valida los datos de entrada usando DTOs con @Valid.
 * - Delega la lógica de negocio al InventarioService.
 * - Retorna respuestas estandarizadas con ApiResponse.
 *
 * ANOTACIONES SPRING:
 * - @RestController: Combina @Controller + @ResponseBody. Indica que esta clase
 *   es un controlador REST y los métodos retornan JSON directamente.
 * - @RequestMapping("/inventario"): Prefijo base para TODOS los endpoints de esta clase.
 * - @RequiredArgsConstructor: Lombok genera un constructor con todos los campos final,
 *   permitiendo la inyección de dependencias por constructor (buena práctica en Spring).
 * - @Tag: Anotación de OpenAPI/Swagger para documentar el grupo de endpoints en la UI.
 */
@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "API para gestión de inventario y movimientos de stock")
public class InventarioController {

    // Inyección de dependencia del servicio de inventario.
    // Es 'final' para garantizar inmutabilidad y ser inyectado por constructor.
    private final InventarioService inventarioService;

    // ==========================================
    // ENDPOINTS DE INVENTARIO
    // ==========================================
    // Esta sección agrupa todos los endpoints relacionados con la consulta
    // y gestión del estado actual del inventario (stock, alertas, resumen).

    /**
     * GET /api/inventario
     * Lista todo el inventario activo del sistema.
     *
     * POR QUÉ EXISTE: Permite al administrador/visualizador ver el stock
     * de todos los productos en una sola vista.
     *
     * @return Lista de InventarioResponseDTO envuelta en ApiResponse.
     */
    @GetMapping
    @Operation(summary = "Listar inventario", description = "Obtiene todo el inventario con estado de stock")
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> obtenerTodo() {
        // Delegamos al servicio la obtención de datos y solo encapsulamos la respuesta.
        List<InventarioResponseDTO> inventario = inventarioService.obtenerTodo();
        return ResponseEntity.ok(ApiResponse.exito(inventario));
    }

    /**
     * GET /api/inventario/resumen
     * Obtiene un resumen general del inventario.
     *
     * POR QUÉ EXISTE: Los dashboards administrativos necesitan métricas
     * agregadas (total productos, agotados, valor total) sin cargar todo el detalle.
     *
     * @return ResumenInventarioDTO con estadísticas generales.
     */
    @GetMapping("/resumen")
    @Operation(summary = "Resumen inventario", description = "Obtiene resumen general del inventario")
    public ResponseEntity<ApiResponse<ResumenInventarioDTO>> obtenerResumen() {
        ResumenInventarioDTO resumen = inventarioService.obtenerResumen();
        return ResponseEntity.ok(ApiResponse.exito(resumen));
    }

    /**
     * GET /api/inventario/stock-bajo
     * Lista productos con stock bajo o en alerta.
     *
     * POR QUÉ EXISTE: Facilita la toma de decisiones de compra mostrando
     * qué productos están por debajo del umbral mínimo configurado.
     *
     * @return Lista de alertas de stock bajo.
     */
    @GetMapping("/stock-bajo")
    @Operation(summary = "Stock bajo", description = "Obtiene productos con stock bajo o agotado")
    public ResponseEntity<ApiResponse<List<AlertaStockDTO>>> obtenerStockBajo() {
        List<AlertaStockDTO> alertas = inventarioService.obtenerStockBajo();
        return ResponseEntity.ok(ApiResponse.exito(alertas));
    }

    /**
     * GET /api/inventario/agotados
     * Lista productos sin stock (cantidad = 0).
     *
     * POR QUÉ EXISTE: Permite identificar rápidamente productos que no
     * pueden ser vendidos hasta nueva reposición.
     *
     * @return Lista de productos agotados.
     */
    @GetMapping("/agotados")
    @Operation(summary = "Productos agotados", description = "Obtiene productos sin stock")
    public ResponseEntity<ApiResponse<List<AlertaStockDTO>>> obtenerAgotados() {
        List<AlertaStockDTO> alertas = inventarioService.obtenerAgotados();
        return ResponseEntity.ok(ApiResponse.exito(alertas));
    }

    /**
     * GET /api/inventario/{id}
     * Obtiene un registro de inventario específico por su ID interno.
     *
     * @param id Identificador único del registro de inventario.
     * @return Detalle del inventario solicitado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener inventario", description = "Obtiene un inventario por su ID")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> obtenerPorId(@PathVariable Long id) {
        // @PathVariable extrae el valor {id} de la URL y lo vincula al parámetro.
        InventarioResponseDTO inventario = inventarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito(inventario));
    }

    /**
     * GET /api/inventario/producto/{productoId}
     * Obtiene el inventario asociado a un producto específico.
     *
     * POR QUÉ EXISTE: Desde el catálogo de productos se necesita consultar
     * el stock sin conocer el ID interno del inventario, solo el ID del producto.
     *
     * @param productoId Identificador del producto.
     * @return Inventario del producto solicitado.
     */
    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Inventario por producto", description = "Obtiene el inventario de un producto")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> obtenerPorProducto(
            @PathVariable Long productoId) {
        InventarioResponseDTO inventario = inventarioService.obtenerPorProducto(productoId);
        return ResponseEntity.ok(ApiResponse.exito(inventario));
    }

    /**
     * POST /api/inventario/inicializar/{productoId}
     * Crea un registro de inventario nuevo para un producto.
     *
     * POR QUÉ EXISTE: Cuando se crea un producto en el catálogo, no tiene
     * inventario automáticamente. Este endpoint permite inicializarlo
     * con valores por defecto (stock = 0, mínimo = 5, máximo = 100).
     *
     * @param productoId ID del producto a inicializar.
     * @return Inventario creado con estado HTTP 201 (CREATED).
     */
    @PostMapping("/inicializar/{productoId}")
    @Operation(summary = "Inicializar inventario", description = "Crea registro de inventario para un producto")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> inicializarInventario(
            @PathVariable Long productoId) {
        InventarioResponseDTO inventario = inventarioService.inicializarInventario(productoId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito(inventario, "Inventario inicializado exitosamente"));
    }

    /**
     * PUT /api/inventario/{id}
     * Actualiza la configuración de un inventario (límites mínimo/máximo, ubicación).
     *
     * POR QUÉ EXISTE: El administrador necesita ajustar los umbrales de alerta
     * y la ubicación física sin alterar la cantidad de stock actual.
     *
     * @param id  ID del inventario a modificar.
     * @param dto Datos de configuración validados con @Valid.
     * @return Inventario actualizado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inventario", description = "Actualiza configuración de stock mínimo/máximo")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> actualizarConfiguracion(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarInventarioDTO dto) {
        // @Valid activa las validaciones de Bean Validation definidas en el DTO.
        // @RequestBody indica que el JSON del cuerpo de la petición se deserializa en el DTO.
        InventarioResponseDTO inventario = inventarioService.actualizarConfiguracion(id, dto);
        return ResponseEntity.ok(ApiResponse.exito(inventario, "Configuración actualizada exitosamente"));
    }

    // ==========================================
    // ENDPOINTS DE MOVIMIENTOS
    // ==========================================
    // Esta sección agrupa endpoints para consultar y registrar movimientos
    // de stock (entradas, salidas, ajustes) y el kardex de productos.

    /**
     * GET /api/inventario/movimientos
     * Lista todos los movimientos de inventario existentes.
     *
     * @return Lista completa de movimientos ordenados por fecha descendente.
     */
    @GetMapping("/movimientos")
    @Operation(summary = "Listar movimientos", description = "Obtiene todos los movimientos de inventario")
    public ResponseEntity<ApiResponse<List<MovimientoResponseDTO>>> obtenerMovimientos() {
        List<MovimientoResponseDTO> movimientos = inventarioService.obtenerTodosMovimientos();
        return ResponseEntity.ok(ApiResponse.exito(movimientos));
    }

    /**
     * GET /api/inventario/movimientos/producto/{productoId}
     * Lista movimientos filtrados por producto.
     *
     * POR QUÉ EXISTE: Permite ver el historial completo de un producto específico
     * para auditoría o seguimiento.
     *
     * @param productoId ID del producto cuyos movimientos se quieren consultar.
     * @return Lista de movimientos del producto.
     */
    @GetMapping("/movimientos/producto/{productoId}")
    @Operation(summary = "Movimientos por producto", description = "Obtiene movimientos de un producto")
    public ResponseEntity<ApiResponse<List<MovimientoResponseDTO>>> obtenerMovimientosPorProducto(
            @PathVariable Long productoId) {
        List<MovimientoResponseDTO> movimientos = inventarioService.obtenerMovimientosPorProducto(productoId);
        return ResponseEntity.ok(ApiResponse.exito(movimientos));
    }

    /**
     * GET /api/inventario/kardex/{productoId}
     * Obtiene el kardex (libro de movimientos) de un producto.
     *
     * POR QUÉ EXISTE: El kardex es un reporte contable/inventarial que muestra
     * el producto, su stock actual, su valorización y el historial detallado
     * de cada movimiento. Es esencial para auditorías.
     *
     * @param productoId ID del producto.
     * @return Objeto KardexDTO con toda la información del kardex.
     */
    @GetMapping("/kardex/{productoId}")
    @Operation(summary = "Kardex", description = "Obtiene el kardex completo de un producto")
    public ResponseEntity<ApiResponse<KardexDTO>> obtenerKardex(@PathVariable Long productoId) {
        KardexDTO kardex = inventarioService.obtenerKardex(productoId);
        return ResponseEntity.ok(ApiResponse.exito(kardex));
    }

    /**
     * POST /api/inventario/movimientos
     * Registra un movimiento manual de inventario (entrada o salida).
     *
     * POR QUÉ EXISTE: Los administradores necesitan registrar manualmente
     * ajustes, mermas, devoluciones u otras operaciones que no provienen
     * automáticamente de compras o ventas.
     *
     * @param dto       Datos del movimiento validados.
     * @param usuarioId ID del usuario que realiza la operación (header opcional X-Usuario-Id).
     * @return Movimiento registrado con estado HTTP 201.
     */
    @PostMapping("/movimientos")
    @Operation(summary = "Registrar movimiento", description = "Registra un movimiento manual de inventario")
    public ResponseEntity<ApiResponse<MovimientoResponseDTO>> registrarMovimiento(
            @Valid @RequestBody RegistrarMovimientoDTO dto,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {
        // @RequestHeader lee un valor de la cabecera HTTP. Es opcional para flexibilidad.
        MovimientoResponseDTO movimiento = inventarioService.registrarMovimiento(dto, usuarioId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito(movimiento, "Movimiento registrado exitosamente"));
    }

    /**
     * POST /api/inventario/{id}/ajuste
     * Realiza un ajuste de inventario (positivo o negativo) sobre un registro existente.
     *
     * POR QUÉ EXISTE: Los ajustes son correcciones al stock real descubierto
     * tras un conteo físico. Pueden incrementar o decrementar la cantidad.
     *
     * @param id        ID del inventario a ajustar.
     * @param dto       Datos del ajuste validados.
     * @param usuarioId ID del usuario que realiza el ajuste.
     * @return Movimiento de ajuste registrado.
     */
    @PostMapping("/{id}/ajuste")
    @Operation(summary = "Ajuste inventario", description = "Realiza un ajuste de inventario (positivo o negativo)")
    public ResponseEntity<ApiResponse<MovimientoResponseDTO>> realizarAjuste(
            @PathVariable Long id,
            @Valid @RequestBody AjusteInventarioDTO dto,
            @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {
        MovimientoResponseDTO movimiento = inventarioService.realizarAjuste(id, dto, usuarioId);
        return ResponseEntity.ok(ApiResponse.exito(movimiento, "Ajuste realizado exitosamente"));
    }
}
