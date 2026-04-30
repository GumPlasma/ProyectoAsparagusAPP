package com.restaurante.pos.cliente.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * CLIENTE DTO (DATA TRANSFER OBJECT)
 * ==================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar datos del cliente entre capas y a través de la API REST.
 *
 * ¿QUÉ ES UN DTO?
 * - Objeto plano que solo contiene datos (sin lógica de negocio).
 * - Se usa para desacoplar el modelo de persistencia (Entity) de la interfaz de la aplicación.
 * - Evita exponer detalles internos de la base de datos (anotaciones JPA, relaciones lazy).
 * - Controla exactamente qué información se envía y recibe en la API REST.
 *
 * DIFERENCIA CLAVE CON ENTITY:
 *   Entity  → representa una fila de la base de datos (tiene @Entity, @Table, @Id).
 *   DTO     → representa un paquete de datos para transferencia (sin anotaciones JPA).
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode automáticamente.
 * - @NoArgsConstructor: Constructor vacío necesario para la deserialización JSON de Jackson.
 * - @AllArgsConstructor: Constructor completo para crear instancias con todos los campos.
 * - @Builder: Habilita el patrón Builder para construcción fluida de objetos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {

    // ============================================================
    // CAMPOS DE IDENTIFICACIÓN
    // ============================================================

    /**
     * Identificador único del cliente.
     * Se incluye en respuestas de consulta y actualización;
     * puede ser nulo en peticiones de creación porque lo genera la BD.
     */
    private Long id;

    // ============================================================
    // DATOS PERSONALES
    // ============================================================

    /**
     * Nombre del cliente.
     * Campo obligatorio en la lógica de negocio (validado en Service).
     */
    private String nombre;

    /**
     * Apellido del cliente.
     * Campo obligatorio en la lógica de negocio (validado en Service).
     */
    private String apellido;

    /**
     * Documento Nacional de Identidad (DNI) del cliente.
     * Debe ser único en el sistema; la validación se realiza en la capa Service.
     */
    private String dni;

    /**
     * Número de teléfono de contacto.
     * Opcional; puede ser nulo si el cliente no proporciona teléfono.
     */
    private String telefono;

    /**
     * Correo electrónico del cliente.
     * Debe ser único en el sistema; la validación se realiza en la capa Service.
     */
    private String email;

    /**
     * Dirección física del cliente (domicilio).
     * Campo opcional para uso en envíos o facturación.
     */
    private String direccion;

    /**
     * Notas adicionales sobre el cliente.
     * Puede incluir preferencias, alergias alimentarias, observaciones del personal, etc.
     */
    private String notas;

    // ============================================================
    // CAMPOS ADMINISTRATIVOS
    // ============================================================

    /**
     * Monto acumulado de todas las compras realizadas por el cliente.
     * Se representa con BigDecimal para garantizar precisión en cálculos monetarios.
     * Generalmente se actualiza por procesos de facturación/venta, no directamente por el usuario.
     */
    private BigDecimal totalCompras;

    /**
     * Estado activo del cliente en el sistema.
     * true  = cliente vigente.
     * false = cliente eliminado lógicamente (no se muestra en listados normales).
     */
    private Boolean activo;
}
