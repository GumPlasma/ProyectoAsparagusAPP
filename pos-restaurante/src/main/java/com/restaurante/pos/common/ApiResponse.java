package com.restaurante.pos.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RESPUESTA API GENÉRICA
 * ======================
 *
 * Esta clase envuelve todas las respuestas de la API para mantener un formato
 * consistente. Todos los endpoints devolverán este formato.
 *
 * EJEMPLO DE RESPUESTA EXITOSA:
 * {
 *   "exitoso": true,
 *   "mensaje": "Operación completada",
 *   "datos": { ... objeto ... },
 *   "fecha": "2024-01-15T10:30:00"
 * }
 *
 * EJEMPLO DE RESPUESTA CON ERROR:
 * {
 *   "exitoso": false,
 *   "mensaje": "No se encontró el registro",
 *   "datos": null,
 *   "fecha": "2024-01-15T10:30:00"
 * }
 *
 * @param <T> Tipo de datos que contiene la respuesta (puede ser cualquier clase)
 */
@Data                               // Lombok genera getters, setters, toString, etc.
@NoArgsConstructor                  // Constructor vacío (necesario para JSON)
@AllArgsConstructor                 // Constructor con todos los argumentos
public class ApiResponse<T> {

    /**
     * Indica si la operación fue exitosa o no.
     * true = operación completada correctamente
     * false = ocurrió un error
     */
    private boolean exitoso;

    /**
     * Mensaje descriptivo sobre el resultado de la operación.
     * Puede ser un mensaje de éxito o una descripción del error.
     */
    private String mensaje;

    /**
     * Los datos retornados por la operación.
     * Puede ser un objeto individual, una lista, o null si hay error.
     */
    private T datos;

    /**
     * Fecha y hora en que se generó la respuesta.
     */
    private LocalDateTime fecha;

    // ==========================================
    // MÉTODOS ESTÁTICOS PARA CREAR RESPUESTAS
    // ==========================================
    // Estos métodos facilitan crear respuestas sin repetir código

    /**
     * Crea una respuesta exitosa con datos.
     *
     * @param datos Los datos a retornar
     * @param mensaje Mensaje de éxito
     * @return ApiResponse configurada como exitosa
     */
    public static <T> ApiResponse<T> exito(T datos, String mensaje) {
        return new ApiResponse<>(true, mensaje, datos, LocalDateTime.now());
    }

    /**
     * Crea una respuesta exitosa con datos y mensaje por defecto.
     *
     * @param datos Los datos a retornar
     * @return ApiResponse configurada como exitosa
     */
    public static <T> ApiResponse<T> exito(T datos) {
        return exito(datos, "Operación completada exitosamente");
    }

    /**
     * Crea una respuesta exitosa sin datos (solo mensaje).
     * Útil para operaciones de eliminación o actualización.
     *
     * @param mensaje Mensaje de éxito
     * @return ApiResponse configurada como exitosa
     */
    public static <T> ApiResponse<T> exito(String mensaje) {
        return exito(null, mensaje);
    }

    /**
     * Crea una respuesta de error.
     *
     * @param mensaje Descripción del error
     * @return ApiResponse configurada como error
     */
    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(false, mensaje, null, LocalDateTime.now());
    }

    /**
     * Crea una respuesta de error con datos adicionales.
     * Útil para enviar detalles de validación.
     *
     * @param mensaje Descripción del error
     * @param datos Datos adicionales del error
     * @return ApiResponse configurada como error
     */
    public static <T> ApiResponse<T> error(String mensaje, T datos) {
        return new ApiResponse<>(false, mensaje, datos, LocalDateTime.now());
    }
}