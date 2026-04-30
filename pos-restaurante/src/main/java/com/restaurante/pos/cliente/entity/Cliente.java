package com.restaurante.pos.cliente.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * ENTIDAD CLIENTE
 * ===============
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "clientes" de la base de datos.
 * Representa a los clientes del restaurante que realizan compras.
 *
 * ¿QUÉ REPRESENTA?
 * Cada instancia de esta clase corresponde a UN cliente registrado en el sistema.
 * Los clientes pueden acumular compras y ser consultados para facturación o programas de fidelización.
 *
 * SOFT DELETE:
 * El campo 'activo' permite realizar borrado lógico. En lugar de eliminar el registro,
 * se marca como inactivo para preservar el historial de ventas asociado.
 *
 * ANOTACIONES JPA:
 * - @Entity: Declara esta clase como entidad gestionada por JPA/Hibernate.
 * - @Table(name = "clientes"): Especifica el nombre exacto de la tabla en la BD.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode automáticamente.
 * - @NoArgsConstructor: Genera constructor sin argumentos (requerido por JPA).
 * - @AllArgsConstructor: Genera constructor con todos los argumentos.
 * - @Builder: Habilita el patrón Builder para construcción fluida de objetos.
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    // ============================================================
    // IDENTIFICADOR PRIMARIO
    // ============================================================

    /**
     * Identificador único del cliente (clave primaria autoincremental).
     *
     * @Id: Marca este campo como la clave primaria de la entidad.
     * @GeneratedValue(strategy = GenerationType.IDENTITY):
     *      Indica que la base de datos genera automáticamente el valor
     *      mediante una columna de identidad (AUTO_INCREMENT en MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================================
    // DATOS PERSONALES
    // ============================================================

    /**
     * Nombre del cliente.
     *
     * @Column(nullable = false): La base de datos no permite valores nulos en esta columna.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Apellido del cliente.
     *
     * @Column(nullable = false): Campo obligatorio en la base de datos.
     */
    @Column(nullable = false)
    private String apellido;

    /**
     * Documento Nacional de Identidad (DNI) del cliente.
     *
     * @Column(unique = true, length = 20):
     *      - unique=true garantiza que no haya dos clientes con el mismo DNI.
     *      - length=20 limita el tamaño máximo del texto en la columna.
     */
    @Column(unique = true, length = 20)
    private String dni;

    /**
     * Número de teléfono de contacto del cliente.
     *
     * Sin restricciones adicionales; permite valores nulos.
     */
    private String telefono;

    /**
     * Correo electrónico del cliente.
     *
     * @Column(unique = true): Garantiza unicidad de email en toda la tabla.
     */
    @Column(unique = true)
    private String email;

    /**
     * Dirección física del cliente (domicilio).
     *
     * Sin restricciones adicionales; permite valores nulos.
     */
    private String direccion;

    /**
     * Notas adicionales sobre el cliente (alergias, preferencias, etc.).
     *
     * @Column(columnDefinition = "TEXT"):
     *      Define la columna con tipo TEXT en la base de datos,
     *      permitiendo almacenar cadenas de longitud mayor que VARCHAR.
     */
    @Column(columnDefinition = "TEXT")
    private String notas;

    // ============================================================
    // CAMPOS ADMINISTRATIVOS
    // ============================================================

    /**
     * Monto acumulado de compras realizadas por el cliente.
     *
     * @Column(precision = 12, scale = 2):
     *      precision=12: Número total de dígitos significativos.
     *      scale=2: Dígitos reservados para la parte decimal.
     *      Ideal para almacenar valores monetarios con exactitud.
     *
     * @Builder.Default: Asigna un valor por defecto (BigDecimal.ZERO)
     *      cuando se usa el patrón Builder y no se especifica otro valor.
     */
    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalCompras = BigDecimal.ZERO;

    /**
     * Indica si el cliente está activo en el sistema.
     *
     * true  = cliente vigente y visible en operaciones normales.
     * false = cliente eliminado lógicamente (soft delete).
     *
     * @Column(nullable = false): Campo obligatorio.
     * @Builder.Default: Valor por defecto true al crear un nuevo cliente.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
