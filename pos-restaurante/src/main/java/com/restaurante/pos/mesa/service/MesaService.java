package com.restaurante.pos.mesa.service;

import com.restaurante.pos.mesa.dto.*;
import com.restaurante.pos.mesa.entity.*;
import com.restaurante.pos.mesa.repository.*;
import com.restaurante.pos.venta.entity.Venta;
import com.restaurante.pos.venta.entity.DetalleVenta;
import com.restaurante.pos.venta.repository.VentaRepository;
import com.restaurante.pos.producto.repository.ProductoRepository;
import com.restaurante.pos.producto.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICIO DEL MÓDULO MESA
 * ========================
 *
 * CAPA: Service (Lógica de Negocio)
 * RESPONSABILIDAD: Contener toda la lógica de negocio relacionada con la gestión
 * de mesas, pedidos y pagos en el restaurante.
 *
 * ¿QUÉ HACE?
 * - Orquesta las operaciones entre el controlador y los repositorios.
 * - Aplica reglas de negocio (ej: no eliminar mesas ocupadas, validar productos activos).
 * - Gestiona el ciclo de vida completo de una mesa: creación → apertura → pedidos → pago → cierre.
 * - Realiza conversiones entre entidades (Entity) y objetos de transferencia (DTO).
 *
 * ANOTACIONES SPRING:
 * - @Service: Marca esta clase como un componente de servicio de Spring, permitiendo
 *   que Spring la detecte e inyecte automáticamente en otros componentes.
 * - @RequiredArgsConstructor: Genera un constructor con todos los campos 'final',
 *   facilitando la inyección de dependencias por constructor (buena práctica en Spring).
 * - @Transactional: Se aplica en métodos que modifican datos. Garantiza que todas las
 *   operaciones de base de datos dentro del método se ejecuten como una única transacción.
 *   Si ocurre un error, se hace rollback automático y no quedan datos inconsistentes.
 */
@Service
@RequiredArgsConstructor
public class MesaService {

    // ============================================
    // DEPENDENCIAS (REPOSITORIOS)
    // ============================================
    // La inyección por constructor asegura que estas dependencias siempre estén disponibles.

    /** Repositorio para operaciones CRUD sobre la entidad Mesa. */
    private final MesaRepository mesaRepository;

    /** Repositorio para gestionar los pedidos (productos) asociados a una mesa. */
    private final PedidoMesaRepository pedidoMesaRepository;

    /** Repositorio para verificar la existencia y disponibilidad de productos. */
    private final ProductoRepository productoRepository;

    /** Repositorio para persistir las ventas generadas al pagar una mesa. */
    private final VentaRepository ventaRepository;

    // ============================================
    // OPERACIONES DE CONSULTA (READ-ONLY)
    // ============================================

    /**
     * Obtiene todas las mesas registradas en el sistema.
     * @return Lista de {@link MesaDTO} con información completa de cada mesa.
     */
    public List<MesaDTO> obtenerTodas() {
        return mesaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca una mesa específica por su ID.
     * @param id Identificador único de la mesa.
     * @return {@link MesaDTO} con los datos de la mesa encontrada.
     * @throws RuntimeException si la mesa no existe.
     */
    public MesaDTO obtenerPorId(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        return convertirADTO(mesa);
    }

    // ============================================
    // OPERACIONES CRUD DE MESAS
    // ============================================

    /**
     * Crea una nueva mesa en el sistema.
     * REGLA DE NEGOCIO: No puede existir otra mesa con el mismo número.
     * La mesa se crea con estado "LIBRE" y posición por defecto (100, 100).
     *
     * @param dto Datos de la mesa a crear.
     * @return {@link MesaDTO} de la mesa creada.
     */
    @Transactional
    public MesaDTO crear(MesaDTO dto) {
        // Validación: verificar que el número de mesa no esté duplicado.
        if (mesaRepository.findByNumero(dto.getNumero()).isPresent()) {
            throw new RuntimeException("Ya existe una mesa con ese número");
        }

        // Construcción de la entidad Mesa usando el patrón Builder.
        Mesa mesa = Mesa.builder()
                .numero(dto.getNumero())
                .capacidad(dto.getCapacidad())
                .estado("LIBRE")
                .posicionX(dto.getPosicionX() != null ? dto.getPosicionX() : 100)
                .posicionY(dto.getPosicionY() != null ? dto.getPosicionY() : 100)
                .totalPedido(BigDecimal.ZERO)
                .propina(BigDecimal.ZERO)
                .build();

        return convertirADTO(mesaRepository.save(mesa));
    }

    /**
     * Actualiza los datos básicos de una mesa existente (capacidad y posición).
     * @param id  Identificador de la mesa a actualizar.
     * @param dto Nuevos datos de la mesa.
     * @return {@link MesaDTO} actualizado.
     */
    @Transactional
    public MesaDTO actualizar(Long id, MesaDTO dto) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        mesa.setCapacidad(dto.getCapacidad());
        if (dto.getPosicionX() != null) mesa.setPosicionX(dto.getPosicionX());
        if (dto.getPosicionY() != null) mesa.setPosicionY(dto.getPosicionY());

        return convertirADTO(mesaRepository.save(mesa));
    }

    /**
     * Actualiza únicamente la posición visual (coordenadas X, Y) de una mesa.
     * Se usa cuando el usuario arrastra la mesa en el mapa interactivo del restaurante.
     *
     * @param id        Identificador de la mesa.
     * @param posicionX Nueva coordenada horizontal.
     * @param posicionY Nueva coordenada vertical.
     * @return {@link MesaDTO} con la posición actualizada.
     */
    @Transactional
    public MesaDTO actualizarPosicion(Long id, Integer posicionX, Integer posicionY) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        mesa.setPosicionX(posicionX);
        mesa.setPosicionY(posicionY);

        return convertirADTO(mesaRepository.save(mesa));
    }

    /**
     * Elimina una mesa del sistema.
     * REGLA DE NEGOCIO: Solo se pueden eliminar mesas que NO estén ocupadas,
     * para evitar borrar datos de ventas en curso.
     *
     * @param id Identificador de la mesa a eliminar.
     */
    @Transactional
    public void eliminar(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        if ("OCUPADA".equals(mesa.getEstado())) {
            throw new RuntimeException("No se puede eliminar una mesa ocupada");
        }

        mesaRepository.delete(mesa);
    }

    // ============================================
    // CICLO DE VIDA DE UNA MESA (APERTURA → PEDIDOS → PAGO)
    // ============================================

    /**
     * Abre una mesa para iniciar la atención a clientes.
     * REGLA DE NEGOCIO: Solo se puede abrir una mesa que esté "LIBRE" o "RESERVADA".
     * Al abrir, se registra la hora de apertura y se reinicia el total del pedido.
     *
     * @param id Identificador de la mesa a abrir.
     * @return {@link MesaDTO} de la mesa abierta.
     */
    @Transactional
    public MesaDTO abrirMesa(Long id) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        // Validar que la mesa esté disponible para ser abierta.
        if (!"LIBRE".equals(mesa.getEstado()) && !"RESERVADA".equals(mesa.getEstado())) {
            throw new RuntimeException("La mesa no está disponible");
        }

        mesa.setEstado("OCUPADA");
        mesa.setHoraApertura(LocalDateTime.now());
        mesa.setTotalPedido(BigDecimal.ZERO);

        return convertirADTO(mesaRepository.save(mesa));
    }

    /**
     * Agrega un producto al pedido activo de una mesa.
     * REGLAS DE NEGOCIO:
     * - La mesa debe existir.
     * - El producto debe existir y estar activo.
     * - Se calcula el subtotal (precio × cantidad) y se suma al total de la mesa.
     *
     * @param mesaId    Identificador de la mesa.
     * @param pedidoDTO Datos del producto y cantidad a agregar.
     * @return {@link MesaDTO} actualizado con el nuevo total.
     */
    @Transactional
    public MesaDTO agregarProducto(Long mesaId, PedidoMesaDTO pedidoDTO) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        Producto producto = productoRepository.findById(pedidoDTO.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Validar que el producto esté disponible para la venta.
        if (!producto.getActivo()) {
            throw new RuntimeException("El producto no está disponible");
        }

        // Calcular el subtotal de esta línea de pedido.
        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(pedidoDTO.getCantidad()));

        // Crear el pedido asociado a la mesa.
        PedidoMesa pedido = PedidoMesa.builder()
                .mesa(mesa)
                .producto(producto)
                .cantidad(pedidoDTO.getCantidad())
                .precioUnitario(producto.getPrecio())
                .subtotal(subtotal)
                .notas(pedidoDTO.getNotas())
                .fechaHora(LocalDateTime.now())
                .build();

        pedidoMesaRepository.save(pedido);

        // Actualizar el total acumulado de la mesa.
        BigDecimal totalActual = mesa.getTotalPedido() != null ? mesa.getTotalPedido() : BigDecimal.ZERO;
        mesa.setTotalPedido(totalActual.add(subtotal));
        mesaRepository.save(mesa);

        return convertirADTO(mesa);
    }

    /**
     * Elimina un producto del pedido de una mesa.
     * REGLA DE NEGOCIO: El pedido debe pertenecer realmente a la mesa indicada.
     * Se resta el subtotal del pedido eliminado del total de la mesa.
     *
     * @param mesaId   Identificador de la mesa.
     * @param pedidoId Identificador del pedido a eliminar.
     * @return {@link MesaDTO} actualizado.
     */
    @Transactional
    public MesaDTO eliminarProducto(Long mesaId, Long pedidoId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        PedidoMesa pedido = pedidoMesaRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Seguridad: verificar que el pedido realmente pertenezca a esta mesa.
        if (!pedido.getMesa().getId().equals(mesaId)) {
            throw new RuntimeException("El pedido no pertenece a esta mesa");
        }

        // Restar el subtotal del pedido eliminado del total de la mesa.
        BigDecimal totalActual = mesa.getTotalPedido() != null ? mesa.getTotalPedido() : BigDecimal.ZERO;
        mesa.setTotalPedido(totalActual.subtract(pedido.getSubtotal()));
        mesaRepository.save(mesa);

        pedidoMesaRepository.deleteById(pedidoId);

        return convertirADTO(mesa);
    }

    /**
     * Actualiza el monto de propina para el pedido actual de una mesa.
     * @param mesaId Identificador de la mesa.
     * @param propina Nuevo monto de propina.
     * @return {@link MesaDTO} actualizado.
     */
    @Transactional
    public MesaDTO actualizarPropina(Long mesaId, BigDecimal propina) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        mesa.setPropina(propina);
        return convertirADTO(mesaRepository.save(mesa));
    }

    /**
     * PROCESAR EL PAGO DE UNA MESA Y GENERAR LA VENTA
     * ------------------------------------------------
     * Este es el método más importante del ciclo de vida de una mesa.
     * Pasos que realiza:
     * 1. Valida que la mesa esté ocupada y tenga pedidos.
     * 2. Si el pago es en efectivo, valida que el monto recibido sea suficiente.
     * 3. Crea una entidad {@link Venta} con todos los datos del pago.
     * 4. Convierte cada {@link PedidoMesa} en un {@link DetalleVenta}.
     * 5. Persiste la venta en la base de datos.
     * 6. Limpia la mesa: elimina pedidos, cambia estado a "LIBRE", reinicia totales.
     *
     * @param pagoDTO Datos del pago (método, monto recibido, propina).
     * @return La {@link Venta} generada y persistida.
     */
    @Transactional
    public Venta procesarPago(PagoMesaDTO pagoDTO) {
        Mesa mesa = mesaRepository.findById(pagoDTO.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        // Validar que la mesa tenga un pedido activo.
        if (!"OCUPADA".equals(mesa.getEstado())) {
            throw new RuntimeException("La mesa no tiene pedidos activos");
        }

        List<PedidoMesa> pedidos = pedidoMesaRepository.findByMesaIdOrderByFechaHoraDesc(mesa.getId());

        // No permitir pagar una mesa sin productos.
        if (pedidos.isEmpty()) {
            throw new RuntimeException("No hay productos en el pedido");
        }

        // Calcular totales del pago.
        BigDecimal subtotal = mesa.getTotalPedido();
        BigDecimal propina = pagoDTO.getPropina() != null ? pagoDTO.getPropina() : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(propina);

        // Validación específica para pagos en efectivo.
        if ("EFECTIVO".equals(pagoDTO.getMetodoPago())) {
            if (pagoDTO.getMontoRecibido() == null || pagoDTO.getMontoRecibido().compareTo(total) < 0) {
                throw new RuntimeException("El monto recibido es insuficiente");
            }
        }

        // ========== CREAR LA VENTA ==========
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setHora(LocalDateTime.now());
        venta.setTipoVenta("MESA");           // Indica que la venta proviene de una mesa.
        venta.setEstado("COMPLETADA");        // La venta se completa inmediatamente al pagar.
        venta.setSubtotal(subtotal);
        venta.setPorcentajeImpuesto(BigDecimal.ZERO);
        venta.setMontoImpuesto(BigDecimal.ZERO);
        venta.setDescuento(BigDecimal.ZERO);
        venta.setTotal(total);
        venta.setMetodoPago(pagoDTO.getMetodoPago());
        venta.setMontoRecibido(pagoDTO.getMontoRecibido() != null ? pagoDTO.getMontoRecibido() : BigDecimal.ZERO);
        venta.setVuelto("EFECTIVO".equals(pagoDTO.getMetodoPago()) ?
                pagoDTO.getMontoRecibido().subtract(total) : BigDecimal.ZERO);
        venta.setPropina(propina);
        venta.setMesaNumero(mesa.getNumero());
        venta.setTipoComprobante("TICKET");

        // ========== CREAR DETALLES DE VENTA ==========
        // Cada pedido de la mesa se convierte en un detalle de la venta.
        List<DetalleVenta> detalles = new ArrayList<>();
        for (PedidoMesa p : pedidos) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(p.getProducto());
            detalle.setCantidad(p.getCantidad());
            detalle.setPrecioUnitario(p.getPrecioUnitario());
            detalle.setSubtotal(p.getSubtotal());
            detalles.add(detalle);
        }
        venta.setDetalles(detalles);

        // Persistir la venta completa (cascade guarda también los detalles).
        venta = ventaRepository.save(venta);

        // ========== LIMPIAR LA MESA PARA LA PRÓXIMA ATENCIÓN ==========
        pedidoMesaRepository.deleteByMesaId(mesa.getId());
        mesa.setEstado("LIBRE");
        mesa.setTotalPedido(BigDecimal.ZERO);
        mesa.setPropina(BigDecimal.ZERO);
        mesa.setHoraCierre(LocalDateTime.now());
        mesaRepository.save(mesa);

        return venta;
    }

    // ============================================
    // ESTADÍSTICAS Y REPORTES
    // ============================================

    /**
     * Genera un resumen estadístico del estado actual de las mesas.
     * @return Mapa con conteos de mesas libres, ocupadas, reservadas, total de mesas
     *         y suma de ventas activas.
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("libres", mesaRepository.countLibres());
        stats.put("ocupadas", mesaRepository.countOcupadas());
        stats.put("reservadas", mesaRepository.countReservadas());
        stats.put("totalMesas", mesaRepository.count());
        stats.put("ventasActivas", mesaRepository.sumVentasActivas());
        return stats;
    }

    // ============================================
    // MÉTODO PRIVADO: CONVERSIÓN ENTIDAD → DTO
    // ============================================

    /**
     * Convierte una entidad {@link Mesa} en un {@link MesaDTO}.
     * Este método también carga todos los pedidos asociados a la mesa
     * y los convierte en una lista de {@link PedidoMesaDTO}.
     *
     * @param mesa Entidad Mesa obtenida de la base de datos.
     * @return Objeto DTO listo para ser enviado al cliente.
     */
    private MesaDTO convertirADTO(Mesa mesa) {
        // Obtener todos los pedidos de la mesa, ordenados del más reciente al más antiguo.
        List<PedidoMesaDTO> pedidosDTO = pedidoMesaRepository.findByMesaIdOrderByFechaHoraDesc(mesa.getId())
                .stream()
                .map(p -> PedidoMesaDTO.builder()
                        .id(p.getId())
                        .mesaId(mesa.getId())
                        .productoId(p.getProducto().getId())
                        .productoNombre(p.getProducto().getNombre())
                        .cantidad(p.getCantidad())
                        .precioUnitario(p.getPrecioUnitario())
                        .subtotal(p.getSubtotal())
                        .notas(p.getNotas())
                        .fechaHora(p.getFechaHora())
                        .build())
                .collect(Collectors.toList());

        // Construir el DTO final con todos los datos de la mesa y sus pedidos.
        return MesaDTO.builder()
                .id(mesa.getId())
                .numero(mesa.getNumero())
                .capacidad(mesa.getCapacidad())
                .estado(mesa.getEstado())
                .posicionX(mesa.getPosicionX())
                .posicionY(mesa.getPosicionY())
                .totalPedido(mesa.getTotalPedido())
                .propina(mesa.getPropina())
                .horaApertura(mesa.getHoraApertura())
                .horaCierre(mesa.getHoraCierre())
                .pedidos(pedidosDTO)
                .build();
    }
}
