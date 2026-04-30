package com.restaurante.pos.inventario.entity;

import com.restaurante.pos.common.BaseEntity;
import com.restaurante.pos.proveedor.entity.FacturaProveedor;
import com.restaurante.pos.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * ENTIDAD MOVIMIENTO DE INVENTARIO
 * ================================
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "movimiento_inventario" y registrar
 * cada cambio de stock que ocurre en el sistema.
 *
 * QUÉ REPRESENTA:
 * Cada fila es UN evento de cambio de inventario: una entrada, una salida o un ajuste.
 * Juntos, todos los movimientos de un producto conforman su historial completo (kardex).
 *
 * TIPOS DE MOVIMIENTO:
 * - ENTRADA: Incrementa el stock (compras, devoluciones, ajustes positivos)
 * - SALIDA: Decrementa el stock (ventas, mermas, ajustes negativos)
 *
 * MOTIVOS DE MOVIMIENTO:
 * - COMPRA: Ingreso por factura de proveedor
 * - VENTA: Salida por venta al público
 * - MERMA: Pérdida por producto dañado o vencido
 * - AJUSTE: Corrección de inventario tras conteo físico
 * - DEVOLUCION: Retorno de producto al proveedor o del cliente
 * - TRANSFERENCIA: Movimiento entre ubicaciones/almacenes
 *
 * AUDITORÍA:
 * Cada movimiento registra obligatoriamente:
 * - Usuario que lo realizó (si está disponible)
 * - Fecha y hora exacta del movimiento
 * - Motivo del movimiento
 * - Stock anterior y posterior (para verificación de consistencia)
 * - Referencia al documento origen (factura de compra o venta)
 *
 * ANOTACIONES JPA:
 * - @Entity: Entidad gestionada por JPA/Hibernate.
 * - @Table(name = "movimiento_inventario"): Nombre exacto de la tabla.
 * - @Getter / @Setter: Lombok genera getters y setters.
 */
@Entity
@Table(name = "movimiento_inventario")
@Getter
@Setter
public class MovimientoInventario extends BaseEntity {

    /**
     * Tipo de movimiento.
     * Determina si el movimiento suma o resta del stock total.
     *
     * Valores posibles: ENTRADA, SALIDA.
     *
     * ANOTACIÓN JPA:
     * - @Enumerated(EnumType.STRING): Almacena el nombre del enum como VARCHAR
     *   en lugar de su ordinal numérico. Esto hace la base de datos más legible
     *   y evita problemas si se reordenan los valores del enum.
     */
    @Column(name = "tipo", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    /**
     * Motivo del movimiento.
     * Explica la razón específica por la que ocurrió el cambio de stock.
     *
     * Valores posibles: COMPRA, VENTA, MERMA, AJUSTE, DEVOLUCION, TRANSFERENCIA.
     */
    @Column(name = "motivo", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MotivoMovimiento motivo;

    /**
     * Cantidad del movimiento.
     * Siempre se almacena como valor positivo. El campo 'tipo' indica
     * si esta cantidad suma o resta del inventario.
     */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /**
     * Stock anterior al movimiento.
     * Registra la cantidad que había antes de aplicar este cambio.
     * Es esencial para auditoría y posibles rollback.
     */
    @Column(name = "stock_anterior")
    private Integer stockAnterior;

    /**
     * Stock posterior al movimiento.
     * Registra la cantidad resultante después de aplicar el cambio.
     * Permite verificar consistencia: stockPosterior debe coincidir
     * con stockAnterior + cantidad (entrada) o stockAnterior - cantidad (salida).
     */
    @Column(name = "stock_posterior")
    private Integer stockPosterior;

    /**
     * Observaciones adicionales del movimiento.
     * Texto libre para describir detalles específicos.
     * Ejemplo: "Producto dañado por humedad en bodega",
     *          "Devolución por defecto de fábrica".
     *
     * ANOTACIÓN JPA:
     * - columnDefinition = "TEXT": Usa el tipo TEXT de la base de datos
     *   para permitir descripciones largas sin límite estricto de caracteres.
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Fecha y hora exacta en que se registró el movimiento.
     * Generalmente se establece con LocalDateTime.now() en el momento de la creación.
     */
    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    // ==========================================
    // RELACIONES JPA
    // ==========================================

    /**
     * RELACIÓN CON INVENTARIO
     * =======================
     * Cada movimiento afecta exactamente un registro de inventario (un producto).
     *
     * ANOTACIONES JPA:
     * - @ManyToOne: Muchos movimientos pueden pertenecer a un mismo inventario.
     * - fetch = FetchType.EAGER: El inventario (y su producto) se carga junto con el movimiento
     *   porque casi siempre se necesita saber de qué producto se trata.
     * - nullable = false: Todo movimiento debe estar asociado a un inventario.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inventario_id", nullable = false)
    private Inventario inventario;

    /**
     * RELACIÓN CON USUARIO
     * ====================
     * Usuario del sistema que realizó o autorizó el movimiento.
     *
     * ANOTACIÓN JPA:
     * - nullable = true: Los movimientos automáticos (por ejemplo, sincronizaciones)
     *   podrían no tener un usuario asociado.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * RELACIÓN CON FACTURA PROVEEDOR
     * ==============================
     * Si el movimiento es una entrada por compra, referencia la factura del proveedor.
     * Esto crea trazabilidad completa: producto -> movimiento -> factura -> proveedor.
     *
     * ANOTACIÓN JPA:
     * - fetch = FetchType.LAZY: La factura completa no siempre se necesita al consultar
     *   movimientos, por lo que se carga bajo demanda para mejorar rendimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_proveedor_id")
    private FacturaProveedor facturaProveedor;

    /**
     * RELACIÓN CON VENTA
     * ==================
     * Si el movimiento es una salida por venta, guarda el ID de la venta.
     * Usamos Long en lugar de @ManyToOne para evitar dependencia circular
     * entre los módulos de inventario y ventas.
     */
    @Column(name = "venta_id")
    private Long ventaId;

    // ==========================================
    // MÉTODOS DE FÁBRICA
    // ==========================================

    /**
     * Método de fábrica (factory method) para crear movimientos de forma segura.
     *
     * POR QUÉ EXISTE: Centraliza la inicialización de campos obligatorios
     * (tipo, motivo, cantidad, inventario, usuario, fecha) evitando
     * olvidar algún campo en múltiples lugares del código.
     *
     * @param tipo       Tipo de movimiento (ENTRADA o SALIDA).
     * @param motivo     Motivo del movimiento.
     * @param cantidad   Cantidad positiva del movimiento.
     * @param inventario Inventario afectado.
     * @param usuario    Usuario que realiza el movimiento (puede ser null).
     * @return Nueva instancia de MovimientoInventario lista para guardar.
     */
    public static MovimientoInventario crear(TipoMovimiento tipo, MotivoMovimiento motivo,
                                             Integer cantidad, Inventario inventario, Usuario usuario) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipo(tipo);
        movimiento.setMotivo(motivo);
        movimiento.setCantidad(cantidad);
        movimiento.setInventario(inventario);
        movimiento.setUsuario(usuario);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setStockAnterior(inventario.getCantidad());
        return movimiento;
    }
}
