package com.restaurante.pos.mesa.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (DATA TRANSFER OBJECT) - MESA
 * =================================
 *
 * CAPA: DTO (Objeto de Transferencia de Datos)
 * RESPONSABILIDAD: Transportar datos entre la capa de servicio y el controlador REST,
 * y finalmente al cliente (frontend). NO contiene lógica de negocio.
 *
 * ¿QUÉ REPRESENTA?
 * Es la versión "plana" y segura de la entidad Mesa, lista para ser serializada a JSON.
 * Incluye tanto los datos básicos de la mesa como la lista de pedidos asociados.
 *
 * ¿POR QUÉ EXISTE?
 * - Evita exponer directamente las entidades JPA (buena práctica de seguridad y desacoplamiento).
 * - Permite personalizar qué datos se envían al cliente.
 * - Facilita el versionado de la API sin modificar las entidades de base de datos.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 * - @NoArgsConstructor: Constructor vacío (necesario para deserialización JSON).
 * - @AllArgsConstructor: Constructor con todos los campos.
 * - @Builder: Permite construir objetos de forma fluida y legible.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MesaDTO {

    /** Identificador único de la mesa. */
    private Long id;

    /** Número visible de la mesa en el restaurante. */
    private Integer numero;

    /** Cantidad máxima de personas que caben en la mesa. */
    private Integer capacidad;

    /** Estado actual: LIBRE, OCUPADA, RESERVADA, PAGADA. */
    private String estado;

    /** Coordenada X para posicionamiento visual en el mapa. */
    private Integer posicionX;

    /** Coordenada Y para posicionamiento visual en el mapa. */
    private Integer posicionY;

    /** Total acumulado de todos los pedidos actuales. */
    private BigDecimal totalPedido;

    /** Monto de propina registrado para el pedido actual. */
    private BigDecimal propina;

    /** Fecha y hora en que se abrió la mesa. */
    private LocalDateTime horaApertura;

    /** Fecha y hora en que se cerró la mesa (pagó). */
    private LocalDateTime horaCierre;

    /** Lista de pedidos (productos) asociados a esta mesa. */
    private List<PedidoMesaDTO> pedidos;
}
