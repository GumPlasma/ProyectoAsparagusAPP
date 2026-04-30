package com.restaurante.pos.proveedor.dto;

import lombok.Data;

/**
 * DTO DE RESPUESTA ENRIQUECIDA DE PROVEEDOR
 * =========================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar los datos completos de un proveedor incluyendo información calculada.
 *
 * ¿QUÉ ES?
 * - Versión enriquecida del proveedor que incluye datos calculados del sistema.
 * - A diferencia de ProveedorDTO, incluye métricas como el total de facturas.
 *
 * ¿DÓNDE SE USA?
 * - En respuestas detalladas de consulta de proveedores.
 * - En reportes y dashboards donde se necesita información agregada.
 *
 * DATOS INCLUIDOS:
 * - Todos los datos básicos del proveedor (RUC, razón social, contacto, etc.).
 * - totalFacturas: Cantidad de facturas de compra registradas a este proveedor.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class ProveedorResponseDTO {

    /**
     * Identificador único del proveedor.
     */
    private Long id;

    /**
     * RUC o NIT del proveedor.
     * Identificador fiscal.
     */
    private String rucNit;

    /**
     * Razón social o nombre comercial del proveedor.
     */
    private String razonSocial;

    /**
     * Nombre de la persona de contacto.
     */
    private String nombreContacto;

    /**
     * Teléfono del proveedor.
     */
    private String telefono;

    /**
     * Correo electrónico del proveedor.
     */
    private String email;

    /**
     * Dirección física del proveedor.
     */
    private String direccion;

    /**
     * Sitio web del proveedor.
     */
    private String sitioWeb;

    /**
     * Notas adicionales sobre el proveedor.
     */
    private String notas;

    /**
     * Estado activo del proveedor.
     * true = vigente; false = inactivo.
     */
    private Boolean activo;

    /**
     * Cantidad total de facturas de compra registradas a este proveedor.
     * Campo calculado por el sistema.
     */
    private Integer totalFacturas;
}
