package com.restaurante.pos.producto.service;

import com.restaurante.pos.producto.dto.*;
import com.restaurante.pos.producto.entity.Categoria;
import com.restaurante.pos.producto.entity.Producto;
import com.restaurante.pos.producto.repository.CategoriaRepository;
import com.restaurante.pos.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICIO DE PRODUCTO
 * ====================
 *
 * Contiene la LÓGICA DE NEGOCIO para gestión de productos y categorías.
 *
 * RESPONSABILIDADES:
 * 1. Aplicar reglas de negocio (validaciones, cálculos)
 * 2. Validar datos antes de guardar (unicidad, existencia)
 * 3. Coordinar entre repositories (Producto y Categoria)
 * 4. Transformar entidades a DTOs (mapeo)
 *
 * ANOTACIONES:
 * - @Service: Marca esta clase como servicio de Spring (se autodetecta)
 * - @Transactional: Todas las operaciones van en transacción (rollback si hay error)
 * - @RequiredArgsConstructor: Lombok crea constructor con campos 'final' (inyección)
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProductoService {

    // ==========================================================================
    // DEPENDENCIAS (inyectadas por constructor - buena práctica)
    // ==========================================================================
    // 'final' asegura que no sean null y permite inyección por constructor

    /**
     * Repositorio de Producto.
     * Proporciona acceso a datos de productos (CRUD + consultas).
     */
    private final ProductoRepository productoRepository;

    /**
     * Repositorio de Categoría.
     * Proporciona acceso a datos de categorías.
     */
    private final CategoriaRepository categoriaRepository;

    // ==========================================================================
    // MÉTODOS DE CATEGORÍA
    // ==========================================================================
    // Gestión de categorías: listar, obtener, crear, actualizar, eliminar

    /**
     * OBTENER TODAS LAS CATEGORÍAS
     * ============================
     *
     * Obtiene todas las categorías activas ordenadas por el campo 'orden'.
     *
     * @return Lista de DTOs con información de categorías
     *
     * ANOTACIONES:
     * - @Transactional(readOnly = true): Solo lectura, más eficiente
     *   - No hay bloqueo de base de datos
     *   - Hibernate no trackea cambios (mejor rendimiento)
     *
     * FLUJO:
     * 1. Repository consulta categorías activas ordenadas
     * 2. Stream convierte cada entidad a DTO
     * 3. Collectors.toList() agrupa resultados
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerCategorias() {
        return categoriaRepository.findAllActivasOrderByOrden()
                .stream()
                .map(this::convertirCategoriaAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * OBTENER CATEGORÍA POR ID
     * ========================
     *
     * Obtiene una categoría específica por su identificador.
     *
     * @param id ID de la categoría a buscar
     * @return DTO con la información de la categoría
     * @throws RuntimeException si no existe la categoría
     *
     * FLUJO:
     * 1. Repository busca por ID (retorna Optional)
     * 2. orElseThrow lanza excepción si está vacío
     * 3. Convierte entidad a DTO y retorna
     */
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {
        // Busca la categoría o lanza excepción si no existe
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return convertirCategoriaAResponseDTO(categoria);
    }

    /**
     * CREAR CATEGORÍA
     * ===============
     *
     * Crea una nueva categoría después de validar los datos.
     *
     * @param dto Datos de la categoría a crear
     * @return DTO con la categoría creada
     * @throws RuntimeException si ya existe una categoría con ese nombre
     *
     * VALIDACIONES:
     * 1. Verifica que el nombre no exista (unicidad)
     * 2. Asigna orden = 0 si no se proporciona
     *
     * FLUJO:
     * 1. Valida unicidad del nombre
     * 2. Crea nueva entidad Categoria
     * 3. Establece los campos desde el DTO
     * 4. Guarda en la base de datos
     * 5. Convierte a DTO y retorna
     */
    public CategoriaResponseDTO crearCategoria(CrearCategoriaDTO dto) {
        // ==========================================================================
        // VALIDACIÓN DE UNICIDAD
        // ==========================================================================
        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con nombre: " + dto.getNombre());
        }

        // ==========================================================================
        // CREAR Y GUARDAR LA CATEGORÍA
        // ==========================================================================
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setImagenUrl(dto.getImagenUrl());
        // Si orden es null, usa 0 por defecto
        categoria.setOrden(dto.getOrden() != null ? dto.getOrden() : 0);

        categoria = categoriaRepository.save(categoria);
        return convertirCategoriaAResponseDTO(categoria);
    }

    /**
     * ACTUALIZAR CATEGORÍA
     * ====================
     *
     * Actualiza los campos de una categoría existente.
     * Solo actualiza los campos que vienen en el DTO (no null).
     *
     * @param id ID de la categoría a actualizar
     * @param dto Datos a actualizar
     * @return DTO con la categoría actualizada
     * @throws RuntimeException si no existe la categoría o el nombre está duplicado
     *
     * LÓGICA DE ACTUALIZACIÓN:
     * - Nombre: Solo si es diferente y no está duplicado
     * - Otros campos: Si son no null, se actualizan
     */
    public CategoriaResponseDTO actualizarCategoria(Long id, ActualizarCategoriaDTO dto) {
        // ==========================================================================
        // BUSCAR LA CATEGORÍA EXISTENTE
        // ==========================================================================
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        // ==========================================================================
        // ACTUALIZAR NOMBRE (CON VALIDACIÓN DE UNICIDAD)
        // ==========================================================================
        if (dto.getNombre() != null && !dto.getNombre().equals(categoria.getNombre())) {
            // El nombre cambió → verificar que no exista otro con ese nombre
            if (categoriaRepository.existsByNombre(dto.getNombre())) {
                throw new RuntimeException("Ya existe una categoría con nombre: " + dto.getNombre());
            }
            categoria.setNombre(dto.getNombre());
        }

        // ==========================================================================
        // ACTUALIZAR CAMPOS OPCIONALES
        // ==========================================================================
        if (dto.getDescripcion() != null) {
            categoria.setDescripcion(dto.getDescripcion());
        }
        if (dto.getImagenUrl() != null) {
            categoria.setImagenUrl(dto.getImagenUrl());
        }
        if (dto.getOrden() != null) {
            categoria.setOrden(dto.getOrden());
        }

        categoria = categoriaRepository.save(categoria);
        return convertirCategoriaAResponseDTO(categoria);
    }

    /**
     * ELIMINAR CATEGORÍA (BORRADO LÓGICO)
     * ====================================
     *
     * Elimina una categoría marcándola como inactiva.
     * Valida que no tenga productos activos asociados.
     *
     * @param id ID de la categoría a eliminar
     * @throws RuntimeException si no existe la categoría o tiene productos activos
     *
     * ¿POR QUÉ VALIDAR PRODUCTOS?
     * - Una categoría con productos activos no debería eliminarse
     * - Evita dejar productos huérfanos o sin categoría válida
     *
     * BORRADO LÓGICO:
     * - categoria.eliminar() → marca activo = false
     * - No se elimina físicamente de la base de datos
     */
    public void eliminarCategoria(Long id) {
        // ==========================================================================
        // BUSCAR LA CATEGORÍA
        // ==========================================================================
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        // ==========================================================================
        // VALIDAR QUE NO TENGA PRODUCTOS ACTIVOS
        // ==========================================================================
        List<Producto> productos = productoRepository.findByCategoriaId(id);
        long productosActivos = productos.stream()
                .filter(p -> p.getActivo())  // Solo contar productos activos
                .count();

        if (productosActivos > 0) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene " +
                    productosActivos + " productos activos");
        }

        // ==========================================================================
        // REALIZAR BORRADO LÓGICO
        // ==========================================================================
        categoria.eliminar();  // Marca activo = false
        categoriaRepository.save(categoria);
    }

    // ==========================================================================
    // MÉTODOS DE PRODUCTO
    // ==========================================================================
    // Gestión de productos: listar, obtener, buscar, filtrar

    /**
     * OBTENER TODOS LOS PRODUCTOS
     * ==========================
     *
     * Obtiene todos los productos activos del sistema.
     *
     * @return Lista de DTOs con información de productos
     *
     * FILTRADO:
     * - .filter(p -> p.getActivo()): Solo productos no eliminados
     * - El borrado es lógico (activo = false), no físico
     *
     * FLUJO:
     * 1. Repository obtiene todos los productos
     * 2. Stream filtra solo los activos
     * 3. Mapea cada producto a DTO
     * 4. Colecta resultados en una lista
     */
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductos() {
        return productoRepository.findAll()
                .stream()
                .filter(p -> p.getActivo())  // Solo productos activos
                .map(this::convertirProductoAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * OBTENER PRODUCTO POR ID
     * =======================
     *
     * Obtiene un producto específico por su identificador.
     *
     * @param id ID del producto a buscar
     * @return DTO con la información del producto
     * @throws RuntimeException si no existe el producto
     *
     * FLUJO:
     * 1. Repository busca por ID (retorna Optional)
     * 2. orElseThrow lanza excepción si no existe
     * 3. Convierte entidad a DTO
     */
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return convertirProductoAResponseDTO(producto);
    }

    /**
     * OBTENER PRODUCTOS POR CATEGORÍA
     * ===============================
     *
     * Obtiene todos los productos activos de una categoría específica.
     *
     * @param categoriaId ID de la categoría
     * @return Lista de productos de esa categoría
     *
     * CONSULTA USADA:
     * - findActivosByCategoriaId: Trae productos activos de la categoría
     * - Filtra por categoria_id AND activo = true
     */
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosPorCategoria(Long categoriaId) {
        return productoRepository.findActivosByCategoriaId(categoriaId)
                .stream()
                .map(this::convertirProductoAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * BUSCAR PRODUCTOS (BÚSQUEDA TEXTUAL)
     * ===================================
     *
     * Busca productos por nombre o código usando búsqueda parcial.
     *
     * @param busqueda Texto a buscar
     * @return Lista de productos que coinciden
     *
     * COINCIDENCIAS:
     * - Busca en nombre: LIKE %busqueda% (insensible a mayúsculas)
     * - Busca en código: LIKE %busqueda% (insensible a mayúsculas)
     *
     * EJEMPLO:
     * buscarProductos("coca") → encuentra "Coca-Cola 500ml", "Coca-Cola 1.5L"
     */
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarProductos(String busqueda) {
        return productoRepository.buscarPorNombreOCodigo(busqueda)
                .stream()
                .filter(p -> p.getActivo())  // Solo activos
                .map(this::convertirProductoAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos disponibles para la venta.
     */
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosDisponibles() {
        return productoRepository.findAllDisponibles()
                .stream()
                .map(this::convertirProductoAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo producto.
     */
    public ProductoResponseDTO crearProducto(CrearProductoDTO dto) {
        // Validar código único si se proporciona
        if (dto.getCodigo() != null && !dto.getCodigo().isEmpty()) {
            if (productoRepository.existsByCodigo(dto.getCodigo())) {
                throw new RuntimeException("Ya existe un producto con código: " + dto.getCodigo());
            }
        }

        // Buscar categoría
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getCategoriaId()));

        Producto producto = new Producto();
        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCosto(dto.getCosto());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        producto.setRequierePreparacion(dto.getRequierePreparacion() != null ? dto.getRequierePreparacion() : true);
        producto.setTiempoPreparacion(dto.getTiempoPreparacion());
        producto.setCategoria(categoria);

        producto = productoRepository.save(producto);
        return convertirProductoAResponseDTO(producto);
    }

    /**
     * Actualiza un producto existente.
     */
    public ProductoResponseDTO actualizarProducto(Long id, ActualizarProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        // Actualizar código si se proporciona y es diferente
        if (dto.getCodigo() != null && !dto.getCodigo().equals(producto.getCodigo())) {
            if (dto.getCodigo().isEmpty()) {
                producto.setCodigo(null);
            } else {
                if (productoRepository.existsByCodigo(dto.getCodigo())) {
                    throw new RuntimeException("Ya existe un producto con código: " + dto.getCodigo());
                }
                producto.setCodigo(dto.getCodigo());
            }
        }

        if (dto.getNombre() != null) {
            producto.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            producto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getPrecio() != null) {
            producto.setPrecio(dto.getPrecio());
        }
        if (dto.getCosto() != null) {
            producto.setCosto(dto.getCosto());
        }
        if (dto.getImagenUrl() != null) {
            producto.setImagenUrl(dto.getImagenUrl());
        }
        if (dto.getDisponible() != null) {
            producto.setDisponible(dto.getDisponible());
        }
        if (dto.getRequierePreparacion() != null) {
            producto.setRequierePreparacion(dto.getRequierePreparacion());
        }
        if (dto.getTiempoPreparacion() != null) {
            producto.setTiempoPreparacion(dto.getTiempoPreparacion());
        }
        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        producto = productoRepository.save(producto);
        return convertirProductoAResponseDTO(producto);
    }

    /**
     * Elimina un producto (borrado lógico).
     */
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        producto.eliminar();
        productoRepository.save(producto);
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE CONVERSIÓN
    // ==========================================

    private CategoriaResponseDTO convertirCategoriaAResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setImagenUrl(categoria.getImagenUrl());
        dto.setOrden(categoria.getOrden());
        dto.setActivo(categoria.getActivo());

        // Contar productos activos
        int total = (int) categoria.getProductos().stream()
                .filter(p -> p.getActivo())
                .count();
        dto.setTotalProductos(total);

        return dto;
    }

    private ProductoResponseDTO convertirProductoAResponseDTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCosto(producto.getCosto());
        dto.setMargenGanancia(producto.getMargenGanancia());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setDisponible(producto.getDisponible());
        dto.setRequierePreparacion(producto.getRequierePreparacion());
        dto.setTiempoPreparacion(producto.getTiempoPreparacion());
        dto.setActivo(producto.getActivo());

        // Convertir categoría
        if (producto.getCategoria() != null) {
            CategoriaSimpleDTO catDto = new CategoriaSimpleDTO();
            catDto.setId(producto.getCategoria().getId());
            catDto.setNombre(producto.getCategoria().getNombre());
            dto.setCategoria(catDto);
        }

        return dto;
    }
}