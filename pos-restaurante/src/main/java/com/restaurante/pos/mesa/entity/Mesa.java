package com.restaurante.pos.mesa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD MESA
 * ============
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "mesas" de la base de datos a un objeto Java.
 * Cada instancia de esta clase representa una fila en la tabla.
 *
 * ¿QUÉ REPRESENTA?
 * Representa una mesa física del restaurante donde se atienden clientes.
 * Contiene información sobre su número, capacidad, estado actual, posición visual
 * en el mapa del restaurante, y los pedidos asociados.
 *
 * ESTADOS POSIBLES:
 * - LIBRE:    La mesa está disponible para nuevos clientes.
 * - OCUPADA:  La mesa tiene clientes siendo atendidos.
 * - RESERVADA: La mesa fue reservada previamente.
 * - PAGADA:   La mesa fue pagada pero aún no liberada (estado transitorio).
 *
 * ANOTACIONES JPA:
 * - @Entity: Indica que esta clase es una entidad gestionada por JPA/Hibernate.
 * - @Table(name = "mesas"): Especifica el nombre exacto de la tabla en la BD.
 * - @Id + @GeneratedValue: Define la clave primaria autoincremental.
 * - @Column: Configura propiedades de cada columna (nullable, unique, precision, etc.).
 * - @OneToMany: Define la relación con los pedidos de la mesa.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode automáticamente.
 * - @NoArgsConstructor: Genera un constructor sin argumentos (requerido por JPA).
 * - @AllArgsConstructor: Genera un constructor con todos los argumentos.
 * - @Builder: Habilita el patrón Builder para crear objetos de forma fluida.
 */
@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {

    /**
     * ID único de la mesa (clave primaria).
     * GenerationType.IDENTITY usa la columna autoincremental de la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número visible de la mesa (ej: 1, 2, 3...).
     * Es único y obligatorio para evitar duplicados.
     */
    @Column(unique = true, nullable = false)
    private Integer numero;

    /**
     * Capacidad máxima de personas que pueden sentarse en la mesa.
     * Obligatorio para controlar la distribución de clientes.
     */
    @Column(nullable = false)
    private Integer capacidad;

    /**
     * Estado actual de la mesa.
     * Valores posibles: LIBRE, OCUPADA, RESERVADA, PAGADA.
     * Longitud máxima de 20 caracteres.
     */
    @Column(length = 20)
    private String estado; // LIBRE, OCUPADA, RESERVADA, PAGADA

    /**
     * Coordenada X de la mesa en el mapa visual del restaurante.
     * Permite posicionar la mesa gráficamente en la interfaz del POS.
     */
    private Integer posicionX;

    /**
     * Coordenada Y de la mesa en el mapa visual del restaurante.
     * Se usa junto con posicionX para ubicar la mesa en el plano.
     */
    private Integer posicionY;

    /**
     * Total acumulado de todos los pedidos actuales de la mesa.
     * precision = 10, scale = 2 significa hasta 10 dígitos en total, 2 decimales.
     * Se reinicia a cero cada vez que la mesa se libera.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal totalPedido;

    /**
     * Monto de propina registrado para el pedido actual.
     * Se guarda en la mesa para mostrarlo antes de procesar el pago.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal propina;

    /**
     * Fecha y hora en que se abrió la mesa (estado cambió a OCUPADA).
     * Útil para calcular el tiempo de atención y generar reportes.
     */
    private LocalDateTime horaApertura;

    /**
     * Fecha y hora en que se cerró la mesa (después del pago).
     * Se usa para auditoría y reportes de tiempos de atención.
     */
    private LocalDateTime horaCierre;

    /**
     * RELACIÓN CON PEDIDOS
     * ====================
     * Una mesa puede tener muchos pedidos (productos solicitados).
     * mappedBy = "mesa" indica que la entidad PedidoMesa tiene el campo 'mesa'
     * que posee la clave foránea en la base de datos.
     * cascade = CascadeType.ALL: cualquier operación sobre la mesa se propaga a sus pedidos.
     * orphanRemoval = true: si se elimina un pedido de esta lista, se borra de la BD.
     * @Builder.Default: inicializa la lista vacía para evitar NullPointerException.
     */
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoMesa> pedidos = new ArrayList<>();
}
