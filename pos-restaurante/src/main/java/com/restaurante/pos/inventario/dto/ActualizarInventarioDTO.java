package com.restaurante.pos.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA ACTUALIZAR CONFIGURACIÓN DE INVENTARIO
 * ===============================================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Recibir y validar los datos para modificar la configuración
 * de un inventario existente (límites de stock y ubicación).
 *
 * QUÉ REPRESENTA:
 * Este DTO se usa en el endpoint PUT /inventario/{id} para actualizar
 * parámetros de control sin modificar la cantidad real de stock.
 *
 * CARACTERÍSTICAS:
 * - Todos los campos son opcionales (parcial update): solo se actualiza
 *   lo que el cliente envía.
 * - Aplica validaciones de rango para evitar valores inconsistentes.
 *
 * VALIDACIONES:
 * - stockMinimo: si se envía, debe ser >= 0.
 * - stockMaximo: si se envía, debe ser >= 1.
 * - ubicacion: máximo 100 caracteres.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class ActualizarInventarioDTO {

    /**
     * Nuevo valor para el stock mínimo.
     * Cuando el stock llega a este valor, se genera alerta de reposición.
     * Si es null, no se modifica el valor actual.
     */
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    /**
     * Nuevo valor para el stock máximo.
     * Representa el límite de almacenamiento del producto.
     * Si es null, no se modifica el valor actual.
     */
    @Min(value = 1, message = "El stock máximo debe ser al menos 1")
    private Integer stockMaximo;

    /**
     * Nueva ubicación física del producto.
     * Ejemplo: "Estante A-3", "Refrigerador 2".
     * Si es null, no se modifica la ubicación actual.
     */
    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String ubicacion;
}
