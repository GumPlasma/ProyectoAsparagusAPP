package com.restaurante.pos.inventario.service;

import com.restaurante.pos.inventario.dto.*;
import com.restaurante.pos.inventario.entity.Inventario;
import com.restaurante.pos.inventario.entity.MotivoMovimiento;
import com.restaurante.pos.inventario.entity.MovimientoInventario;
import com.restaurante.pos.inventario.entity.TipoMovimiento;
import com.restaurante.pos.inventario.repository.InventarioRepository;
import com.restaurante.pos.inventario.repository.MovimientoInventarioRepository;
import com.restaurante.pos.producto.entity.Producto;
import com.restaurante.pos.producto.repository.ProductoRepository;
import com.restaurante.pos.usuario.entity.Usuario;
import com.restaurante.pos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICIO DE INVENTARIO
 * ======================
 *
 * CAPA: Service (Servicio de Negocio)
 * RESPONSABILIDAD: Contener toda la lógica de negocio del módulo de inventario.
 * Actúa como intermediario entre el Controller y los Repositorios.
 *
 * QUÉ HACE:
 * - Orquesta operaciones de lectura y escritura en la base de datos.
 * - Aplica reglas de negocio (validación de stock, cálculo de precios promedio).
 * - Convierte entidades JPA a DTOs para desacoplar la capa de presentación.
 *
 * ANOTACIONES SPRING:
 * - @Service: Marca esta clase como un componente de servicio de Spring,
 *   permitiendo su detección automática e inyección de dependencias.
 * - @Transactional: Indica que los métodos de esta clase ejecutan dentro de
 *   una transacción de base de datos. Si ocurre una excepción no controlada,
 *   se realiza rollback automático garantizando consistencia.
 * - @RequiredArgsConstructor: Lombok genera un constructor con todos los campos final,
 *   facilitando la inyección de dependencias por constructor.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class InventarioService {

    // Repositorio para acceder a la tabla de inventario.
    private final InventarioRepository inventarioRepository;

    // Repositorio para acceder a la tabla de movimientos de inventario.
    private final MovimientoInventarioRepository movimientoRepository;

    // Repositorio del módulo de productos (dependencia cruzada permitida para validar existencia).
    private final ProductoRepository productoRepository;

    // Repositorio del módulo de usuarios para asociar quién realizó cada movimiento.
    private final UsuarioRepository usuarioRepository;

    // ==========================================
    // MÉTODOS DE INVENTARIO
    // ==========================================
    // Esta sección contiene operaciones de consulta y gestión del estado
    // actual de stock de los productos.

    /**
     * Obtiene todo el inventario activo con información de su producto asociado.
     *
     * POR QUÉ readOnly = true: Las operaciones de solo lectura no necesitan
     * bloquear recursos de escritura en la BD, mejorando el rendimiento.
     *
     * @return Lista de DTOs con el inventario completo.
     */
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerTodo() {
        // findAllActivosWithProducto usa JOIN FETCH para evitar consultas N+1.
        return inventarioRepository.findAllActivosWithProducto()
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un inventario específico por su ID.
     *
     * @param id Identificador del registro de inventario.
     * @return DTO con los datos del inventario encontrado.
     * @throws RuntimeException si no existe el inventario.
     */
    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerPorId(Long id) {
        // orElseThrow convierte el Optional en el objeto o lanza excepción si está vacío.
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + id));
        return convertirAResponseDTO(inventario);
    }

    /**
     * Obtiene el inventario asociado a un producto específico.
     *
     * @param productoId Identificador del producto.
     * @return DTO con el inventario del producto.
     * @throws RuntimeException si el producto no tiene inventario registrado.
     */
    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerPorProducto(Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No existe inventario para el producto ID: " + productoId));
        return convertirAResponseDTO(inventario);
    }

    /**
     * Obtiene productos cuyo stock actual es menor o igual al mínimo configurado.
     *
     * POR QUÉ EXISTE: Alerta temprana para que el área de compras reponga productos
     * antes de que se agoten por completo.
     *
     * @return Lista de alertas de stock bajo.
     */
    @Transactional(readOnly = true)
    public List<AlertaStockDTO> obtenerStockBajo() {
        return inventarioRepository.findConStockBajo()
                .stream()
                .map(this::convertirAAlertaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos con cantidad igual a cero.
     *
     * @return Lista de productos agotados.
     */
    @Transactional(readOnly = true)
    public List<AlertaStockDTO> obtenerAgotados() {
        return inventarioRepository.findAgotados()
                .stream()
                .map(this::convertirAAlertaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Genera un resumen estadístico del inventario.
     *
     * POR QUÉ EXISTE: Los paneles de administración necesitan métricas agregadas
     * sin tener que procesar toda la lista de productos en el frontend.
     *
     * @return DTO con totales, conteos y valorización del inventario.
     */
    @Transactional(readOnly = true)
    public ResumenInventarioDTO obtenerResumen() {
        ResumenInventarioDTO resumen = new ResumenInventarioDTO();

        // Obtenemos todos los inventarios activos una sola vez para calcular métricas.
        List<Inventario> todos = inventarioRepository.findAllActivosWithProducto();

        // Contamos productos totales, con stock, con stock bajo y agotados.
        resumen.setTotalProductos(todos.size());
        resumen.setProductosConStock((int) todos.stream().filter(i -> i.getCantidad() > 0).count());
        resumen.setProductosStockBajo((int) inventarioRepository.countConStockBajo());
        resumen.setProductosAgotados((int) inventarioRepository.countAgotados());

        // Calculamos el valor total del inventario usando precio promedio ponderado.
        Double valorTotal = inventarioRepository.calcularValorTotal();
        resumen.setValorTotalInventario(valorTotal != null ? new BigDecimal(valorTotal) : BigDecimal.ZERO);

        return resumen;
    }

    /**
     * Actualiza la configuración de límites de stock y ubicación de un inventario.
     *
     * POR QUÉ EXISTE: Permite ajustar los parámetros de control sin modificar
     * la cantidad real de productos en stock.
     *
     * @param id  Identificador del inventario.
     * @param dto Datos de configuración a aplicar.
     * @return Inventario actualizado.
     * @throws RuntimeException si el stock mínimo es mayor que el máximo.
     */
    public InventarioResponseDTO actualizarConfiguracion(Long id, ActualizarInventarioDTO dto) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + id));

        // Solo actualizamos los campos que vienen en el DTO (parcial).
        if (dto.getStockMinimo() != null) {
            inventario.setStockMinimo(dto.getStockMinimo());
        }
        if (dto.getStockMaximo() != null) {
            inventario.setStockMaximo(dto.getStockMaximo());
        }
        if (dto.getUbicacion() != null) {
            inventario.setUbicacion(dto.getUbicacion());
        }

        // Regla de negocio: el límite mínimo no puede superar al máximo.
        if (inventario.getStockMinimo() > inventario.getStockMaximo()) {
            throw new RuntimeException("El stock mínimo no puede ser mayor que el stock máximo");
        }

        inventario = inventarioRepository.save(inventario);
        return convertirAResponseDTO(inventario);
    }

    /**
     * Inicializa el inventario para un producto recién creado.
     *
     * POR QUÉ EXISTE: Los productos del catálogo no tienen inventario automáticamente.
     * Este método crea el registro con valores por defecto seguros.
     *
     * @param productoId ID del producto a inicializar.
     * @return Inventario creado.
     * @throws RuntimeException si el producto no existe o ya tiene inventario.
     */
    public InventarioResponseDTO inicializarInventario(Long productoId) {
        // Verificar que el producto existe en el catálogo.
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // Evitar duplicados: un producto solo debe tener un inventario.
        if (inventarioRepository.existsByProductoId(productoId)) {
            throw new RuntimeException("El producto ya tiene inventario registrado");
        }

        // Crear el registro con valores por defecto.
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(0);
        inventario.setStockMinimo(5);
        inventario.setStockMaximo(100);

        inventario = inventarioRepository.save(inventario);
        return convertirAResponseDTO(inventario);
    }

    // ==========================================
    // MÉTODOS DE MOVIMIENTOS
    // ==========================================
    // Esta sección gestiona el historial de cambios de stock:
    // consultas, filtros, kardex y registros manuales.

    /**
     * Obtiene los movimientos de un producto específico ordenados por fecha descendente.
     *
     * @param productoId ID del producto.
     * @return Lista de movimientos del producto.
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerMovimientosPorProducto(Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No existe inventario para el producto"));

        return movimientoRepository.findByInventarioIdOrderByFechaMovimientoDesc(inventario.getId())
                .stream()
                .map(this::convertirMovimientoAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los movimientos del sistema.
     *
     * @return Lista completa de movimientos.
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerTodosMovimientos() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::convertirMovimientoAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca movimientos aplicando filtros opcionales.
     *
     * POR QUÉ EXISTE: Los usuarios necesitan filtrar por producto, tipo, motivo,
     * usuario o rango de fechas para auditorías y reportes.
     *
     * @param filtro DTO con los criterios de búsqueda.
     * @return Lista de movimientos que coinciden con los filtros.
     */
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> buscarMovimientos(FiltroMovimientoDTO filtro) {
        // Resolvemos el productoId al inventarioId porque los movimientos
        // están ligados al inventario, no directamente al producto.
        Long inventarioId = null;
        if (filtro.getProductoId() != null) {
            inventarioId = inventarioRepository.findByProductoId(filtro.getProductoId())
                    .map(Inventario::getId)
                    .orElse(null);
        }

        // Convertimos los Strings del filtro a los enums correspondientes.
        TipoMovimiento tipo = filtro.getTipo() != null ?
                TipoMovimiento.valueOf(filtro.getTipo()) : null;
        MotivoMovimiento motivo = filtro.getMotivo() != null ?
                MotivoMovimiento.valueOf(filtro.getMotivo()) : null;

        return movimientoRepository.buscarConFiltros(
                        inventarioId, tipo, motivo,
                        filtro.getUsuarioId(),
                        filtro.getFechaDesde(),
                        filtro.getFechaHasta()
                ).stream()
                .map(this::convertirMovimientoAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Construye el kardex de un producto.
     *
     * POR QUÉ EXISTE: El kardex es un reporte que agrupa la información
     * del producto, su stock actual, su valorización y todo su historial
     * de movimientos. Es un documento esencial para auditorías contables.
     *
     * @param productoId ID del producto.
     * @return DTO con la información completa del kardex.
     */
    @Transactional(readOnly = true)
    public KardexDTO obtenerKardex(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No existe inventario para el producto"));

        // Obtenemos los movimientos ordenados del más reciente al más antiguo.
        List<MovimientoResponseDTO> movimientos = movimientoRepository
                .findByInventarioIdOrderByFechaMovimientoDesc(inventario.getId())
                .stream()
                .map(this::convertirMovimientoAResponseDTO)
                .collect(Collectors.toList());

        // Armamos el DTO del kardex con toda la información consolidada.
        KardexDTO kardex = new KardexDTO();
        kardex.setProducto(convertirAProductoInventarioDTO(producto));
        kardex.setMovimientos(movimientos);
        kardex.setStockActual(inventario.getCantidad());
        kardex.setValorStock(inventario.getPrecioPromedio().multiply(new BigDecimal(inventario.getCantidad())));

        return kardex;
    }

    /**
     * Registra un movimiento manual de entrada o salida.
     *
     * POR QUÉ EXISTE: Permite ajustar el stock por motivos diversos (mermas,
     * devoluciones, donaciones, etc.) que no provienen del flujo normal de compra/venta.
     *
     * FLUJO:
     * 1. Valida existencia del producto y su inventario.
     * 2. Valida que haya stock suficiente si es una salida.
     * 3. Crea el movimiento y actualiza la cantidad del inventario.
     * 4. Guarda ambos dentro de la misma transacción.
     *
     * @param dto       Datos del movimiento.
     * @param usuarioId ID del usuario que realiza la operación (puede ser null).
     * @return DTO del movimiento registrado.
     * @throws RuntimeException si no hay stock suficiente para una salida.
     */
    public MovimientoResponseDTO registrarMovimiento(RegistrarMovimientoDTO dto, Long usuarioId) {
        // Validamos que el producto exista en el catálogo.
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validamos que el producto ya tenga inventario inicializado.
        Inventario inventario = inventarioRepository.findByProductoId(dto.getProductoId())
                .orElseThrow(() -> new RuntimeException("El producto no tiene inventario. Inicializar primero."));

        // Resolvemos el usuario si se proporcionó un ID.
        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                    .orElse(null);
        }

        // Convertimos los strings a enums para garantizar valores válidos.
        TipoMovimiento tipo = TipoMovimiento.valueOf(dto.getTipo().toUpperCase());
        MotivoMovimiento motivo = MotivoMovimiento.valueOf(dto.getMotivo().toUpperCase());

        // Regla de negocio: no permitir salidas si no hay stock suficiente.
        if (tipo == TipoMovimiento.SALIDA && !inventario.hayStock(dto.getCantidad())) {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + inventario.getCantidad());
        }

        // Creamos el movimiento usando el factory method que inicializa campos comunes.
        MovimientoInventario movimiento = MovimientoInventario.crear(
                tipo, motivo, dto.getCantidad(), inventario, usuario
        );
        movimiento.setObservaciones(dto.getObservaciones());
        movimiento.setStockAnterior(inventario.getCantidad());

        // Actualizamos la cantidad del inventario según el tipo de movimiento.
        if (tipo == TipoMovimiento.ENTRADA) {
            inventario.setCantidad(inventario.getCantidad() + dto.getCantidad());
        } else {
            inventario.setCantidad(inventario.getCantidad() - dto.getCantidad());
        }

        // Registramos el stock resultante y la fecha del último movimiento.
        movimiento.setStockPosterior(inventario.getCantidad());
        inventario.setUltimoMovimiento(LocalDateTime.now());

        // Guardamos ambos objetos. Al estar dentro de @Transactional, ambos
        // commits se hacen atómicamente: si uno falla, se revierte todo.
        inventarioRepository.save(inventario);
        movimiento = movimientoRepository.save(movimiento);

        return convertirMovimientoAResponseDTO(movimiento);
    }

    /**
     * Realiza un ajuste de inventario (positivo o negativo).
     *
     * POR QUÉ EXISTE: Los ajustes surgen de diferencias entre el stock
     * del sistema y el conteo físico real. La cantidad puede ser positiva
     * (sobrante) o negativa (faltante).
     *
     * @param inventarioId ID del inventario a ajustar.
     * @param dto          Datos del ajuste (cantidad positiva o negativa).
     * @param usuarioId    ID del usuario que realiza el ajuste.
     * @return Movimiento de ajuste registrado.
     */
    public MovimientoResponseDTO realizarAjuste(Long inventarioId, AjusteInventarioDTO dto, Long usuarioId) {
        Inventario inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

        Usuario usuario = usuarioId != null ?
                usuarioRepository.findById(usuarioId).orElse(null) : null;

        int cantidadAjuste = dto.getCantidad();
        TipoMovimiento tipo;

        // Determinamos el tipo de movimiento según el signo de la cantidad.
        if (cantidadAjuste > 0) {
            tipo = TipoMovimiento.ENTRADA;
        } else {
            tipo = TipoMovimiento.SALIDA;
            cantidadAjuste = Math.abs(cantidadAjuste);

            // Validamos stock antes de registrar una salida por ajuste.
            if (!inventario.hayStock(cantidadAjuste)) {
                throw new RuntimeException("Stock insuficiente para el ajuste");
            }
        }

        MotivoMovimiento motivo = MotivoMovimiento.valueOf(dto.getMotivo().toUpperCase());

        MovimientoInventario movimiento = MovimientoInventario.crear(
                tipo, motivo, cantidadAjuste, inventario, usuario
        );
        movimiento.setObservaciones(dto.getObservaciones());
        movimiento.setStockAnterior(inventario.getCantidad());

        // Aplicamos el cambio al stock del inventario.
        if (tipo == TipoMovimiento.ENTRADA) {
            inventario.setCantidad(inventario.getCantidad() + cantidadAjuste);
        } else {
            inventario.setCantidad(inventario.getCantidad() - cantidadAjuste);
        }

        movimiento.setStockPosterior(inventario.getCantidad());
        inventario.setUltimoMovimiento(LocalDateTime.now());

        inventarioRepository.save(inventario);
        movimiento = movimientoRepository.save(movimiento);

        return convertirMovimientoAResponseDTO(movimiento);
    }

    // ==========================================
    // MÉTODOS INTERNOS (usados por otros servicios)
    // ==========================================
    // Estos métodos son invocados desde otros módulos (Ventas, Proveedores)
    // para mantener el stock sincronizado con las operaciones del negocio.

    /**
     * Registra una entrada de inventario originada por una compra a proveedor.
     *
     * POR QUÉ EXISTE: Desacopla el módulo de proveedores del módulo de inventario.
     * Cuando se recibe una factura de compra, este método actualiza el stock
     * y recalcula el precio promedio ponderado del producto.
     *
     * @param productoId        ID del producto comprado.
     * @param cantidad          Cantidad recibida.
     * @param precioUnitario    Precio unitario de la compra.
     * @param facturaProveedorId ID de la factura del proveedor (para trazabilidad).
     * @param usuarioId         ID del usuario que registra la compra.
     */
    public void registrarEntradaCompra(Long productoId, Integer cantidad, BigDecimal precioUnitario,
                                       Long facturaProveedorId, Long usuarioId) {

        // Si el producto no tiene inventario, lo creamos automáticamente.
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseGet(() -> {
                    Producto producto = productoRepository.findById(productoId)
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    Inventario inv = new Inventario();
                    inv.setProducto(producto);
                    inv.setCantidad(0);
                    return inventarioRepository.save(inv);
                });

        Usuario usuario = usuarioId != null ?
                usuarioRepository.findById(usuarioId).orElse(null) : null;

        MovimientoInventario movimiento = MovimientoInventario.crear(
                TipoMovimiento.ENTRADA,
                MotivoMovimiento.COMPRA,
                cantidad,
                inventario,
                usuario
        );
        movimiento.setStockAnterior(inventario.getCantidad());

        // Cálculo de precio promedio ponderado:
        // NuevoPromedio = (ValorActual + ValorNuevo) / (CantidadActual + CantidadNueva)
        BigDecimal valorActual = inventario.getPrecioPromedio()
                .multiply(new BigDecimal(inventario.getCantidad()));
        BigDecimal valorNuevo = precioUnitario.multiply(new BigDecimal(cantidad));
        int nuevaCantidad = inventario.getCantidad() + cantidad;

        if (nuevaCantidad > 0) {
            BigDecimal nuevoPrecioPromedio = valorActual.add(valorNuevo)
                    .divide(new BigDecimal(nuevaCantidad), 2, BigDecimal.ROUND_HALF_UP);
            inventario.setPrecioPromedio(nuevoPrecioPromedio);
        }

        inventario.setCantidad(nuevaCantidad);
        movimiento.setStockPosterior(inventario.getCantidad());
        inventario.setUltimoMovimiento(LocalDateTime.now());

        // TODO: Asociar con factura cuando la relación JPA esté completa.

        inventarioRepository.save(inventario);
        movimientoRepository.save(movimiento);
    }

    /**
     * Registra una salida de inventario originada por una venta.
     *
     * POR QUÉ EXISTE: Desacopla el módulo de ventas del módulo de inventario.
     * Cada vez que se confirma una venta, se descuenta del stock disponible.
     *
     * @param productoId ID del producto vendido.
     * @param cantidad   Cantidad vendida.
     * @param ventaId    ID de la venta (para trazabilidad).
     * @param usuarioId  ID del usuario que registra la venta.
     * @throws RuntimeException si no hay stock suficiente.
     */
    public void registrarSalidaVenta(Long productoId, Integer cantidad, Long ventaId, Long usuarioId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No existe inventario para el producto"));

        // Regla crítica de negocio: no vender si no hay stock.
        if (!inventario.hayStock(cantidad)) {
            throw new RuntimeException("Stock insuficiente para el producto: " + productoId);
        }

        Usuario usuario = usuarioId != null ?
                usuarioRepository.findById(usuarioId).orElse(null) : null;

        MovimientoInventario movimiento = MovimientoInventario.crear(
                TipoMovimiento.SALIDA,
                MotivoMovimiento.VENTA,
                cantidad,
                inventario,
                usuario
        );
        movimiento.setStockAnterior(inventario.getCantidad());
        movimiento.setVentaId(ventaId);

        // Decrementamos el stock y registramos la trazabilidad.
        inventario.setCantidad(inventario.getCantidad() - cantidad);
        movimiento.setStockPosterior(inventario.getCantidad());
        inventario.setUltimoMovimiento(LocalDateTime.now());

        inventarioRepository.save(inventario);
        movimientoRepository.save(movimiento);
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE CONVERSIÓN
    // ==========================================
    // Estos métodos transforman entidades JPA en DTOs para desacoplar
    // la capa de persistencia de la capa de presentación/API.

    /**
     * Convierte una entidad Inventario en InventarioResponseDTO.
     *
     * @param inventario Entidad a convertir.
     * @return DTO poblado con los datos de la entidad.
     */
    private InventarioResponseDTO convertirAResponseDTO(Inventario inventario) {
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(inventario.getId());
        dto.setCantidad(inventario.getCantidad());
        dto.setStockMinimo(inventario.getStockMinimo());
        dto.setStockMaximo(inventario.getStockMaximo());
        dto.setUbicacion(inventario.getUbicacion());
        dto.setUltimoMovimiento(inventario.getUltimoMovimiento());
        dto.setPrecioPromedio(inventario.getPrecioPromedio());
        dto.setStockBajo(inventario.isStockBajo());
        dto.setStockAgotado(inventario.getCantidad() == 0);

        // Incluimos la información resumida del producto si está disponible.
        if (inventario.getProducto() != null) {
            dto.setProducto(convertirAProductoInventarioDTO(inventario.getProducto()));
        }

        return dto;
    }

    /**
     * Convierte una entidad Inventario en AlertaStockDTO.
     *
     * @param inventario Entidad en alerta de stock.
     * @return DTO con la información de la alerta.
     */
    private AlertaStockDTO convertirAAlertaDTO(Inventario inventario) {
        AlertaStockDTO dto = new AlertaStockDTO();
        dto.setProductoId(inventario.getProducto().getId());
        dto.setCodigo(inventario.getProducto().getCodigo());
        dto.setNombre(inventario.getProducto().getNombre());
        dto.setStockActual(inventario.getCantidad());
        dto.setStockMinimo(inventario.getStockMinimo());
        dto.setMensaje(inventario.getCantidad() == 0 ? "Sin stock" : "Stock bajo");
        return dto;
    }

    /**
     * Convierte una entidad Producto en ProductoInventarioDTO.
     *
     * @param producto Entidad de producto del catálogo.
     * @return DTO resumido con los datos relevantes para vistas de inventario.
     */
    private ProductoInventarioDTO convertirAProductoInventarioDTO(Producto producto) {
        ProductoInventarioDTO dto = new ProductoInventarioDTO();
        dto.setId(producto.getId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setCosto(producto.getCosto());
        dto.setDisponible(producto.getDisponible());
        return dto;
    }

    /**
     * Convierte una entidad MovimientoInventario en MovimientoResponseDTO.
     *
     * @param movimiento Entidad de movimiento.
     * @return DTO completo con información del movimiento, producto, usuario y referencias.
     */
    private MovimientoResponseDTO convertirMovimientoAResponseDTO(MovimientoInventario movimiento) {
        MovimientoResponseDTO dto = new MovimientoResponseDTO();
        dto.setId(movimiento.getId());
        dto.setTipo(movimiento.getTipo().name());
        dto.setMotivo(movimiento.getMotivo().name());
        dto.setCantidad(movimiento.getCantidad());
        dto.setStockAnterior(movimiento.getStockAnterior());
        dto.setStockPosterior(movimiento.getStockPosterior());
        dto.setObservaciones(movimiento.getObservaciones());
        dto.setFechaMovimiento(movimiento.getFechaMovimiento());

        // Producto asociado al movimiento (a través del inventario).
        if (movimiento.getInventario() != null && movimiento.getInventario().getProducto() != null) {
            dto.setProducto(convertirAProductoInventarioDTO(movimiento.getInventario().getProducto()));
        }

        // Usuario que realizó el movimiento.
        if (movimiento.getUsuario() != null) {
            UsuarioMovimientoDTO usuarioDto = new UsuarioMovimientoDTO();
            usuarioDto.setId(movimiento.getUsuario().getId());
            usuarioDto.setUsername(movimiento.getUsuario().getUsername());
            usuarioDto.setNombreCompleto(movimiento.getUsuario().getNombreCompleto());
            dto.setUsuario(usuarioDto);
        }

        // Referencia al documento origen (factura de compra o venta).
        if (movimiento.getFacturaProveedor() != null) {
            dto.setReferenciaDocumento("Factura ID: " + movimiento.getFacturaProveedor().getId());
        } else if (movimiento.getVentaId() != null) {
            dto.setReferenciaDocumento("Venta ID: " + movimiento.getVentaId());
        }

        return dto;
    }
}
