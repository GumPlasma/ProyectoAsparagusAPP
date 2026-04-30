package com.restaurante.pos.proveedor.dto;

import lombok.Data;

/**
 * DTO SIMPLIFICADO DE PROVEEDOR
 * =============================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar una vista mínima de un proveedor para anidar en otras respuestas.
 *
 * ¿QUÉ ES?
 * - Versión reducida del proveedor que solo incluye los datos esenciales de identificación.
 * - Se usa para evitar enviar información innecesaria cuando solo se necesita identificar al proveedor.
 *
 * ¿DÓNDE SE USA?
 * - Anidado dentro de {@link FacturaResponseDTO} para mostrar a qué proveedor pertenece la factura.
 * - En listados desplegables (combos/selects) donde solo se necesita ID y nombre.
 * - En respuestas donde incluir el proveedor completo sería redundante.
 *
 * DIFERENCIA CON ProveedorDTO / ProveedorResponseDTO:
 *   ProveedorDTO/ResponseDTO → Datos completos del proveedor (contacto, dirección, notas, etc.).
 *   ProveedorSimpleDTO       → Solo identificación básica (id, RUC, razón social).
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class ProveedorSimpleDTO {

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
     * Ejemplo: "Carnes del Valle S.A.S.".
     */
    private String razonSocial;
}
