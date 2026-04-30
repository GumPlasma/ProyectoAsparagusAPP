package com.restaurante.pos.proveedor.dto;

import lombok.*;

/**
 * PROVEEDOR DTO (DATA TRANSFER OBJECT)
 * ====================================
 *
 * CAPA: DTO (Transferencia de Datos)
 * RESPONSABILIDAD: Transportar datos de un proveedor entre capas y a través de la API REST.
 *
 * ¿QUÉ ES?
 * - Objeto plano que representa un proveedor en las operaciones de entrada y salida.
 * - Se usa tanto para crear como para actualizar y consultar proveedores.
 * - Evita exponer la entidad JPA directamente en la API.
 *
 * DIFERENCIA CON ENTITY:
 *   Entity (Proveedor)  → Tiene anotaciones JPA, relaciones, mapea tabla de BD.
 *   DTO (ProveedorDTO)  → Solo datos, sin anotaciones de persistencia.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 * - @NoArgsConstructor: Constructor vacío para deserialización JSON.
 * - @AllArgsConstructor: Constructor completo.
 * - @Builder: Habilita el patrón Builder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorDTO {

    /**
     * Identificador único del proveedor.
     * Generado automáticamente por la base de datos.
     */
    private Long id;

    /**
     * Nombre comercial o razón social del proveedor.
     * Ejemplo: "Carnes del Valle S.A.S.", "Distribuidora Norte".
     */
    private String nombre;

    /**
     * Registro Único de Contribuyente (RUC) o NIT del proveedor.
     * Identificador fiscal único.
     */
    private String ruc;

    /**
     * Nombre de la persona de contacto dentro de la empresa proveedora.
     * Ejemplo: "Carlos Mendoza".
     */
    private String contacto;

    /**
     * Número de teléfono del proveedor o contacto.
     */
    private String telefono;

    /**
     * Correo electrónico del proveedor.
     */
    private String email;

    /**
     * Dirección física del proveedor o punto de entrega.
     */
    private String direccion;

    /**
     * Categoría del proveedor para clasificación.
     * Ejemplos: "CARNES", "VERDURAS", "BEBIDAS", "INSUMOS".
     */
    private String categoria;

    /**
     * Notas adicionales sobre el proveedor.
     * Puede incluir condiciones de pago, horarios de entrega, observaciones.
     */
    private String notas;

    /**
     * Estado activo del proveedor en el sistema.
     * true  = proveedor vigente.
     * false = proveedor inactivo (soft delete).
     */
    private Boolean activo;
}
