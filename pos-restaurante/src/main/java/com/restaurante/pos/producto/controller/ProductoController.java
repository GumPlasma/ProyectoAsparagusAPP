package com.restaurante.pos.producto.controller;

import com.restaurante.pos.producto.dto.*;
import com.restaurante.pos.producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLADOR REST DEL MÓDULO PRODUCTO
 * ====================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints HTTP para la gestión del catálogo de productos del restaurante.
 *
 * ¿QUÉ HACE?
 * - Recibe peticiones HTTP relacionadas con productos (crear, listar, buscar, actualizar, eliminar).
 * - Delega toda la lógica de negocio al {@link ProductoService}.
 * - Retorna respuestas estructuradas en formato JSON con los datos solicitados.
 * - Documenta la API con anotaciones de OpenAPI/Swagger.
 *
 * ENDPOINTS DISPONIBLES:
 * - POST   /productos              → Crear producto
 * - GET    /productos              → Listar todos
 * - GET    /productos/disponibles  → Listar disponibles para venta
 * - GET    /productos/{id}         → Buscar por ID
 * - GET    /productos/categoria/{categoriaId} → Filtrar por categoría
 * - GET    /productos/buscar?q=... → Búsqueda por nombre/descripción
 * - PUT    /productos/{id}         → Actualizar
 * - DELETE /productos/{id}         → Eliminar (soft delete)
 *
 * ANOTACIONES SPRING:
 * - @RestController: Controlador REST que retorna JSON automáticamente.
 * - @RequestMapping("/productos"): Ruta base para todos los endpoints.
 * - @RequiredArgsConstructor: Inyección de dependencias por constructor.
 * - @Tag: Documentación Swagger para agrupar endpoints.
 */
@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "API para gestión de productos del menú")
@RequiredArgsConstructor
public class ProductoController {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================

    /** Servicio de productos que contiene la lógica de negocio. */
    private final ProductoService productoService;

    // ============================================================
    // OPERACIONES CRUD
    // ============================================================

    /**
     * CREAR PRODUCTO
     * --------------
     * POST /productos
     *
     * Crea un nuevo producto en el catálogo del restaurante.
     *
     * @param dto Datos del producto a crear (validados automáticamente con @Valid).
     * @return ResponseEntity<ProductoResponseDTO> con el producto creado y HTTP 201 CREATED.
     *
     * ANOTACIONES:
     * - @PostMapping: Responde a peticiones POST.
     * - @Valid: Activa las validaciones de Bean Validation definidas en el DTO.
     * - @RequestBody: Deserializa el cuerpo JSON en un objeto CrearProductoDTO.
     */
    @PostMapping
    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el menú del restaurante")
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody CrearProductoDTO dto) {
        ProductoResponseDTO producto = productoService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    /**
     * LISTAR TODOS LOS PRODUCTOS
     * --------------------------
     * GET /productos
     *
     * Obtiene el listado completo de productos registrados.
     *
     * @return ResponseEntity<List<ProductoResponseDTO>> con todos los productos y HTTP 200 OK.
     */
    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos del catálogo")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        List<ProductoResponseDTO> productos = productoService.obtenerProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * LISTAR PRODUCTOS DISPONIBLES
     * ----------------------------
     * GET /productos/disponibles
     *
     * Obtiene solo los productos marcados como disponibles para la venta.
     * Útil para mostrar el menú activo en el POS y evitar mostrar productos agotados.
     *
     * @return ResponseEntity<List<ProductoResponseDTO>> con productos disponibles.
     */
    @GetMapping("/disponibles")
    @Operation(summary = "Productos disponibles", description = "Obtiene solo los productos disponibles para venta")
    public ResponseEntity<List<ProductoResponseDTO>> productosDisponibles() {
        List<ProductoResponseDTO> productos = productoService.obtenerProductosDisponibles();
        return ResponseEntity.ok(productos);
    }

    /**
     * BUSCAR PRODUCTO POR ID
     * ----------------------
     * GET /productos/{id}
     *
     * Obtiene los detalles completos de un producto específico.
     *
     * @param id Identificador único del producto (extraído de la URL).
     * @return ResponseEntity<ProductoResponseDTO> con el producto encontrado.
     *
     * ANOTACIONES:
     * - @PathVariable: Vincula el segmento {id} de la URL al parámetro del método.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Obtiene un producto por su identificador único")
    public ResponseEntity<ProductoResponseDTO> obtenerProducto(@PathVariable Long id) {
        ProductoResponseDTO producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * FILTRAR PRODUCTOS POR CATEGORÍA
     * -------------------------------
     * GET /productos/categoria/{categoriaId}
     *
     * Obtiene todos los productos que pertenecen a una categoría específica.
     *
     * @param categoriaId Identificador de la categoría.
     * @return ResponseEntity<List<ProductoResponseDTO>> con los productos de esa categoría.
     */
    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Productos por categoría", description = "Filtra productos según la categoría seleccionada")
    public ResponseEntity<List<ProductoResponseDTO>> productosPorCategoria(@PathVariable Long categoriaId) {
        List<ProductoResponseDTO> productos = productoService.obtenerProductosPorCategoria(categoriaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * BUSCAR PRODUCTOS POR TEXTO
     * --------------------------
     * GET /productos/buscar?q=...
     *
     * Realiza una búsqueda difusa por nombre o descripción del producto.
     *
     * @param q Texto a buscar (query string).
     * @return ResponseEntity<List<ProductoResponseDTO>> con los productos coincidentes.
     *
     * ANOTACIONES:
     * - @RequestParam: Extrae el valor del parámetro de consulta de la URL.
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos", description = "Busca productos por nombre o descripción")
    public ResponseEntity<List<ProductoResponseDTO>> buscarProductos(@RequestParam String q) {
        List<ProductoResponseDTO> productos = productoService.buscarProductos(q);
        return ResponseEntity.ok(productos);
    }

    /**
     * ACTUALIZAR PRODUCTO
     * -------------------
     * PUT /productos/{id}
     *
     * Modifica los datos de un producto existente.
     *
     * @param id  Identificador del producto a actualizar.
     * @param dto Nuevos datos del producto.
     * @return ResponseEntity<ProductoResponseDTO> con el producto actualizado.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarProductoDTO dto) {
        ProductoResponseDTO producto = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(producto);
    }

    /**
     * ELIMINAR PRODUCTO
     * -----------------
     * DELETE /productos/{id}
     *
     * Elimina un producto del catálogo (borrado lógico).
     *
     * @param id Identificador del producto a eliminar.
     * @return ResponseEntity<Void> con HTTP 204 NO CONTENT.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo (borrado lógico)")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
