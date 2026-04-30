package com.restaurante.pos.proveedor.controller;

import com.restaurante.pos.proveedor.dto.ProveedorDTO;
import com.restaurante.pos.proveedor.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * CONTROLADOR REST DEL MÓDULO PROVEEDOR
 * =====================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints HTTP para la gestión de proveedores del restaurante.
 *
 * ¿QUÉ ES UN PROVEEDOR?
 * - Es una empresa o persona que suministra productos al restaurante.
 * - Ejemplos: proveedores de carnes, verduras, bebidas, utensilios.
 * - Cada proveedor puede tener múltiples facturas de compra asociadas.
 *
 * ¿QUÉ HACE ESTE CONTROLADOR?
 * - Recibe peticiones HTTP para crear, listar, buscar, filtrar, actualizar y eliminar proveedores.
 * - Delega toda la lógica de negocio al {@link ProveedorService}.
 * - NO contiene lógica de negocio; solo coordina entrada/salida.
 *
 * ENDPOINTS DISPONIBLES:
 * - GET    /proveedores                    → Listar todos activos
 * - GET    /proveedores/{id}               → Buscar por ID
 * - GET    /proveedores/buscar?termino=... → Búsqueda difusa
 * - GET    /proveedores/categoria/{cat}    → Filtrar por categoría
 * - POST   /proveedores                    → Crear
 * - PUT    /proveedores/{id}               → Actualizar
 * - DELETE /proveedores/{id}               → Eliminar (soft delete)
 *
 * ANOTACIONES SPRING:
 * - @RestController: Controlador REST que retorna JSON automáticamente.
 * - @RequestMapping("/proveedores"): Ruta base para todos los endpoints.
 * - @RequiredArgsConstructor: Inyección de dependencias por constructor.
 */
@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class ProveedorController {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================

    /** Servicio de proveedores que contiene la lógica de negocio. */
    private final ProveedorService proveedorService;

    // ============================================================
    // OPERACIONES CRUD Y BÚSQUEDA
    // ============================================================

    /**
     * LISTAR PROVEEDORES ACTIVOS
     * --------------------------
     * GET /proveedores
     *
     * Obtiene todos los proveedores activos ordenados alfabéticamente por nombre.
     *
     * @return List<ProveedorDTO> con los proveedores activos.
     */
    @GetMapping
    public List<ProveedorDTO> obtenerTodos() {
        return proveedorService.obtenerTodos();
    }

    /**
     * BUSCAR PROVEEDOR POR ID
     * -----------------------
     * GET /proveedores/{id}
     *
     * Obtiene los detalles de un proveedor específico.
     *
     * @param id Identificador único del proveedor.
     * @return ProveedorDTO con los datos del proveedor.
     */
    @GetMapping("/{id}")
    public ProveedorDTO obtenerPorId(@PathVariable Long id) {
        return proveedorService.obtenerPorId(id);
    }

    /**
     * BUSCAR PROVEEDORES POR TEXTO
     * ----------------------------
     * GET /proveedores/buscar?termino=...
     *
     * Realiza una búsqueda difusa por nombre, RUC o nombre del contacto.
     *
     * @param termino Texto a buscar.
     * @return List<ProveedorDTO> con los proveedores coincidentes.
     */
    @GetMapping("/buscar")
    public List<ProveedorDTO> buscar(@RequestParam String termino) {
        return proveedorService.buscar(termino);
    }

    /**
     * FILTRAR PROVEEDORES POR CATEGORÍA
     * ---------------------------------
     * GET /proveedores/categoria/{categoria}
     *
     * Obtiene los proveedores que pertenecen a una categoría específica.
     *
     * @param categoria Nombre de la categoría (ej: "CARNES", "BEBIDAS", "VERDURAS").
     * @return List<ProveedorDTO> con los proveedores de esa categoría.
     */
    @GetMapping("/categoria/{categoria}")
    public List<ProveedorDTO> obtenerPorCategoria(@PathVariable String categoria) {
        return proveedorService.obtenerPorCategoria(categoria);
    }

    /**
     * CREAR PROVEEDOR
     * ---------------
     * POST /proveedores
     *
     * Registra un nuevo proveedor en el sistema.
     *
     * @param dto Datos del proveedor a crear.
     * @return ProveedorDTO del proveedor recién creado.
     */
    @PostMapping
    public ProveedorDTO crear(@RequestBody ProveedorDTO dto) {
        return proveedorService.crear(dto);
    }

    /**
     * ACTUALIZAR PROVEEDOR
     * --------------------
     * PUT /proveedores/{id}
     *
     * Modifica los datos de un proveedor existente.
     *
     * @param id  Identificador del proveedor a actualizar.
     * @param dto Nuevos datos del proveedor.
     * @return ProveedorDTO con la información actualizada.
     */
    @PutMapping("/{id}")
    public ProveedorDTO actualizar(@PathVariable Long id, @RequestBody ProveedorDTO dto) {
        return proveedorService.actualizar(id, dto);
    }

    /**
     * ELIMINAR PROVEEDOR
     * ------------------
     * DELETE /proveedores/{id}
     *
     * Elimina un proveedor del sistema (soft delete).
     *
     * @param id Identificador del proveedor a eliminar.
     * @return ResponseEntity<Void> con HTTP 200 OK.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
