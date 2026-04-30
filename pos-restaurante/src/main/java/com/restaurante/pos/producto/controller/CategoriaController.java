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
 * CONTROLADOR REST DEL MÓDULO CATEGORÍA
 * =====================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints HTTP para la gestión de categorías de productos.
 *
 * ¿QUÉ ES UNA CATEGORÍA?
 * - Es una clasificación que agrupa productos del menú.
 * - Ejemplos: "Platos Principales", "Bebidas", "Postres", "Entradas".
 * - Cada producto debe pertenecer a exactamente una categoría.
 *
 * ¿QUÉ HACE ESTE CONTROLADOR?
 * - Recibe peticiones HTTP para crear, listar, buscar, actualizar y eliminar categorías.
 * - Delega la lógica de negocio al {@link ProductoService}.
 * - Retorna respuestas JSON con los datos de las categorías.
 *
 * ENDPOINTS DISPONIBLES:
 * - POST   /categorias       → Crear categoría
 * - GET    /categorias       → Listar todas
 * - GET    /categorias/{id}  → Buscar por ID
 * - PUT    /categorias/{id}  → Actualizar
 * - DELETE /categorias/{id}  → Eliminar
 *
 * ANOTACIONES SPRING:
 * - @RestController: Controlador REST que retorna JSON automáticamente.
 * - @RequestMapping("/categorias"): Ruta base para todos los endpoints.
 * - @RequiredArgsConstructor: Inyección de dependencias por constructor.
 * - @Tag: Documentación Swagger para agrupar endpoints.
 */
@RestController
@RequestMapping("/categorias")
@Tag(name = "Categorías", description = "API para gestión de categorías de productos")
@RequiredArgsConstructor
public class CategoriaController {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================

    /** Servicio que gestiona tanto productos como categorías. */
    private final ProductoService productoService;

    // ============================================================
    // OPERACIONES CRUD
    // ============================================================

    /**
     * CREAR CATEGORÍA
     * ---------------
     * POST /categorias
     *
     * Crea una nueva categoría para clasificar productos.
     *
     * @param dto Datos de la categoría a crear (validados con @Valid).
     * @return ResponseEntity<CategoriaResponseDTO> con la categoría creada y HTTP 201 CREATED.
     *
     * ANOTACIONES:
     * - @PostMapping: Responde a peticiones POST.
     * - @Valid: Activa validaciones de Bean Validation.
     * - @RequestBody: Deserializa el cuerpo JSON en objeto CrearCategoriaDTO.
     */
    @PostMapping
    @Operation(summary = "Crear categoría", description = "Registra una nueva categoría de productos")
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(
            @Valid @RequestBody CrearCategoriaDTO dto) {
        CategoriaResponseDTO categoria = productoService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    /**
     * LISTAR TODAS LAS CATEGORÍAS
     * ---------------------------
     * GET /categorias
     *
     * Obtiene el listado completo de categorías registradas.
     *
     * @return ResponseEntity<List<CategoriaResponseDTO>> con todas las categorías.
     */
    @GetMapping
    @Operation(summary = "Listar categorías", description = "Obtiene todas las categorías de productos")
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        List<CategoriaResponseDTO> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * BUSCAR CATEGORÍA POR ID
     * -----------------------
     * GET /categorias/{id}
     *
     * Obtiene los detalles de una categoría específica.
     *
     * @param id Identificador único de la categoría.
     * @return ResponseEntity<CategoriaResponseDTO> con la categoría encontrada.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoría por ID", description = "Obtiene una categoría por su identificador único")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoria(@PathVariable Long id) {
        CategoriaResponseDTO categoria = productoService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(categoria);
    }

    /**
     * ACTUALIZAR CATEGORÍA
     * --------------------
     * PUT /categorias/{id}
     *
     * Modifica los datos de una categoría existente.
     *
     * @param id  Identificador de la categoría a actualizar.
     * @param dto Nuevos datos de la categoría.
     * @return ResponseEntity<CategoriaResponseDTO> con la categoría actualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Modifica los datos de una categoría existente")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarCategoriaDTO dto) {
        CategoriaResponseDTO categoria = productoService.actualizarCategoria(id, dto);
        return ResponseEntity.ok(categoria);
    }

    /**
     * ELIMINAR CATEGORÍA
     * ------------------
     * DELETE /categorias/{id}
     *
     * Elimina una categoría del sistema.
     * Nota: Solo se puede eliminar si no tiene productos asociados (regla de negocio en el Service).
     *
     * @param id Identificador de la categoría a eliminar.
     * @return ResponseEntity<Void> con HTTP 204 NO CONTENT.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        productoService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
