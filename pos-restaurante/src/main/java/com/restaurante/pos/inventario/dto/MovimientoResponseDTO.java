package com.restaurante.pos.inventario.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO DE RESPUESTA DE MOVIMIENTO DE INVENTARIO
 * ============================================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Transportar la información de un movimiento de stock
 * desde la capa de servicio hacia el cliente de la API REST.
 *
 * QUÉ REPRESENTA:
 * Cada instancia de esta clase contiene el detalle completo de un movimiento
 * de inventario: tipo, motivo, cantidad, stock anterior/posterior, fecha,
 * producto afectado, usuario responsable y referencias a documentos.
 *
 * POR QUÉ EXISTE:
 * - Evita exponer directamente la entidad JPA {@link com.restaurante.pos.inventario.entity.MovimientoInventario},
 *   la cual contiene relaciones que podrían causar serializaciones infinitas o
 *   exponer datos internos no deseados.
 * - Los campos 'tipo' y 'motivo' se exponen como String (nombre del enum)
 *   para facilitar su consumo por parte de clientes frontend.
 * - Incluye información enriquecida del producto y usuario sin requerir
 *   consultas adicionales por parte del cliente.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode automáticamente.
 */
@Data
public class MovimientoResponseDTO {

    /** Identificador único del movimiento. */
    private Long id;

    /** Tipo de movimiento como texto: "ENTRADA" o "SALIDA". */
    private String tipo;

    /** Motivo del movimiento como texto: "COMPRA", "VENTA", "MERMA", etc. */
    private String motivo;

    /** Cantidad movida (siempre positiva; el tipo indica si suma o resta). */
    private Integer cantidad;

    /** Stock que había antes de aplicar este movimiento. */
    private Integer stockAnterior;

    /** Stock resultante después de aplicar este movimiento. */
    private Integer stockPosterior;

    /** Notas u observaciones adicionales sobre el movimiento. */
    private String observaciones;

    /** Fecha y hora exacta en que se registró el movimiento. */
    private LocalDateTime fechaMovimiento;

    /** Información resumida del producto afectado por el movimiento. */
    private ProductoInventarioDTO producto;

    /** Información del usuario que realizó o autorizó el movimiento. */
    private UsuarioMovimientoDTO usuario;

    /** Referencia textual al documento origen (ej: "Venta ID: 45" o "Factura ID: 12"). */
    private String referenciaDocumento;
}
