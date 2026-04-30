package com.restaurante.pos.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA REALIZAR UN AJUSTE DE INVENTARIO
 * =========================================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Recibir y validar los datos necesarios para realizar
 * un ajuste de inventario tras un conteo físico.
 *
 * QUÉ REPRESENTA:
 * Un ajuste corrige la diferencia entre el stock registrado en el sistema
 * y el stock real contado físicamente.
 *
 * CÓMO FUNCIONA LA CANTIDAD:
 * - Valor positivo: indica sobrante (el sistema tenía menos de lo real).
 *   Se registra como ENTRADA.
 * - Valor negativo: indica faltante (el sistema tenía más de lo real).
 *   Se registra como SALIDA.
 *
 * VALIDACIONES:
 * - cantidad: obligatoria. Puede ser positiva o negativa.
 * - motivo: obligatorio. Ej: "AJUSTE", "MERMA".
 * - observaciones: opcional pero recomendada para auditoría.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class AjusteInventarioDTO {

    /**
     * Cantidad del ajuste.
     * Positiva = sobrante (suma al stock).
     * Negativa = faltante (resta del stock).
     * Obligatoria.
     */
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;

    /**
     * Motivo del ajuste.
     * Generalmente "AJUSTE" para correcciones por conteo físico,
     * o "MERMA" para pérdidas identificadas.
     * Obligatorio.
     */
    @NotBlank(message = "El motivo del ajuste es obligatorio")
    private String motivo;

    /** Observaciones detalladas sobre la razón del ajuste. Opcional. */
    private String observaciones;
}
