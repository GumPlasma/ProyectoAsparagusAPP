package com.restaurante.pos.venta.service;

import com.restaurante.pos.venta.entity.Venta;
import com.restaurante.pos.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * SERVICIO DEL MÓDULO VENTA
 * =========================
 *
 * CAPA: Service (Lógica de Negocio)
 * RESPONSABILIDAD: Contener la lógica de negocio para la consulta, búsqueda,
 * estadísticas y anulación de ventas ya registradas en el sistema.
 *
 * ¿QUÉ HACE?
 * - Gestiona las operaciones de lectura sobre el historial de ventas.
 * - Aplica filtros combinados para búsquedas complejas.
 * - Genera estadísticas agregadas para reportes y dashboards.
 * - Permite anular ventas sin eliminarlas físicamente (soft delete lógico).
 *
 * NOTA IMPORTANTE:
 * La creación de ventas NO se realiza directamente desde este servicio.
 * Las ventas se generan automáticamente al procesar el pago de una mesa
 * (ver {@link com.restaurante.pos.mesa.service.MesaService#procesarPago}).
 *
 * ANOTACIONES SPRING:
 * - @Service: Componente de servicio detectado automáticamente por Spring.
 * - @RequiredArgsConstructor: Inyección de dependencias por constructor.
 * - @Transactional: Garantiza atomicidad en operaciones de modificación (anulación).
 */
@Service
@RequiredArgsConstructor
public class VentaService {

    // ============================================
    // DEPENDENCIAS
    // ============================================

    /** Repositorio para acceder al historial de ventas. */
    private final VentaRepository ventaRepository;

    // ============================================
    // OPERACIONES DE CONSULTA BÁSICA
    // ============================================

    /**
     * Obtiene todas las ventas registradas en el sistema.
     * @return Lista completa de entidades {@link Venta}.
     */
    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    /**
     * Busca una venta específica por su ID.
     * @param id Identificador único de la venta.
     * @return La entidad {@link Venta} encontrada.
     * @throws RuntimeException si la venta no existe.
     */
    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    // ============================================
    // BÚSQUEDAS Y FILTROS
    // ============================================

    /**
     * Busca ventas aplicando filtros opcionales combinados.
     * Los parámetros nulos son ignorados en la consulta.
     *
     * @param fechaInicio  Fecha inicial del rango (opcional).
     * @param fechaFin     Fecha final del rango (opcional).
     * @param metodoPago   Método de pago a filtrar (opcional).
     * @param mesaNumero   Número de mesa a filtrar (opcional).
     * @param estado       Estado de la venta a filtrar (opcional).
     * @return Lista de ventas que cumplen con los filtros.
     */
    public List<Venta> buscar(LocalDate fechaInicio, LocalDate fechaFin, String metodoPago, Integer mesaNumero, String estado) {
        return ventaRepository.buscarConFiltros(fechaInicio, fechaFin, metodoPago, mesaNumero, estado);
    }

    /**
     * Obtiene ventas dentro de un rango de fechas.
     * @param inicio Fecha inicial.
     * @param fin    Fecha final.
     * @return Lista de ventas en el rango especificado.
     */
    public List<Venta> obtenerPorFecha(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaBetween(inicio, fin);
    }

    /**
     * Obtiene ventas filtradas por estado.
     * @param estado Estado a buscar (PENDIENTE, COMPLETADA, ANULADA).
     * @return Lista de ventas en el estado indicado.
     */
    public List<Venta> obtenerPorEstado(String estado) {
        return ventaRepository.findByEstado(estado);
    }

    /**
     * Obtiene ventas filtradas por método de pago.
     * @param metodoPago Método de pago (EFECTIVO, TARJETA, TRANSFERENCIA).
     * @return Lista de ventas con ese método de pago.
     */
    public List<Venta> obtenerPorMetodoPago(String metodoPago) {
        return ventaRepository.findByMetodoPago(metodoPago);
    }

    /**
     * Obtiene el historial de ventas asociadas a una mesa específica.
     * @param mesaNumero Número de la mesa.
     * @return Lista de ventas realizadas en esa mesa.
     */
    public List<Venta> obtenerPorMesa(Integer mesaNumero) {
        return ventaRepository.findByMesaNumero(mesaNumero);
    }

    // ============================================
    // ESTADÍSTICAS Y REPORTES
    // ============================================

    /**
     * Genera estadísticas agregadas de ventas en un rango de fechas.
     * Incluye: total de ventas en monto, total de propinas, y cantidad de ventas.
     *
     * @param inicio Fecha inicial del período.
     * @param fin    Fecha final del período.
     * @return Mapa con las estadísticas calculadas.
     */
    public Map<String, Object> obtenerEstadisticas(LocalDate inicio, LocalDate fin) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVentas", ventaRepository.sumTotalByFechaBetween(inicio, fin));
        stats.put("totalPropinas", ventaRepository.sumPropinasByFechaBetween(inicio, fin));
        stats.put("cantidadVentas", ventaRepository.countByFechaBetween(inicio, fin));
        return stats;
    }

    /**
     * Genera un resumen simplificado de las ventas de un día específico.
     * Calcula el monto total sumando los totales de cada venta individualmente.
     *
     * @param fecha Día a resumir.
     * @return Mapa con total de ventas, monto total y fecha.
     */
    public Map<String, Object> obtenerResumenDia(LocalDate fecha) {
        List<Venta> ventas = ventaRepository.findByFecha(fecha);

        return Map.of(
                "totalVentas", ventas.size(),
                "montoTotal", ventas.stream()
                        .map(Venta::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .doubleValue(),
                "fecha", fecha.toString()
        );
    }

    // ============================================
    // OPERACIONES DE MODIFICACIÓN
    // ============================================

    /**
     * ANULA UNA VENTA (Soft Delete Lógico)
     * ------------------------------------
     * En lugar de eliminar físicamente el registro, cambia su estado a "ANULADA".
     * Esto preserva la información para auditoría y reportes históricos.
     * Si la venta ya está anulada, no realiza ninguna acción adicional.
     *
     * @param id Identificador de la venta a anular.
     */
    @Transactional
    public void eliminar(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (!"ANULADA".equals(venta.getEstado())) {
            venta.setEstado("ANULADA");
            ventaRepository.save(venta);
        }
    }
}
