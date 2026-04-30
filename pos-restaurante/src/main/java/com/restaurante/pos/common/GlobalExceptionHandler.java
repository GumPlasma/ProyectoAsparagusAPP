package com.restaurante.pos.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * MANEJADOR GLOBAL DE EXCEPCIONES
 * ===============================
 *
 * Esta clase captura todas las excepciones que ocurren en la aplicación
 * y devuelve respuestas consistentes al cliente.
 *
 * @RestControllerAdvice:
 * - Es un componente especial que funciona como interceptor de excepciones
 * - Aplica a TODOS los controllers de la aplicación
 * - Nos permite centralizar el manejo de errores
 *
 * VENTAJAS:
 * 1. No hay que usar try-catch en cada método del controller
 * 2. Respuestas de error consistentes
 * 3. Código más limpio y mantenible
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * MANEJO DE EXCEPCIÓN: Recurso no encontrado
     * ==========================================
     * Captura RuntimeException cuando lanzamos errores de "no encontrado".
     *
     * EJEMPLO: Cuando buscamos un usuario por ID que no existe.
     *
     * @param ex La excepción capturada
     * @return ResponseEntity con el error formateado
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> manejarRuntimeException(RuntimeException ex) {
        // Creamos respuesta de error
        ApiResponse<Object> respuesta = ApiResponse.error(ex.getMessage());

        // Retornamos con código HTTP 404 (Not Found) o 400 (Bad Request)
        // según el mensaje de error
        HttpStatus status = ex.getMessage().contains("no encontrado")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(respuesta);
    }

    /**
     * MANEJO DE EXCEPCIÓN: Validación de datos
     * ========================================
     * Captura errores cuando los datos de entrada no pasan las validaciones.
     *
     * EJEMPLO: Cuando un campo @NotBlank está vacío.
     *
     * @param ex La excepción de validación
     * @return ResponseEntity con los errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarValidacionException(
            MethodArgumentNotValidException ex) {

        // Mapa para almacenar los errores por campo
        Map<String, String> errores = new HashMap<>();

        // Recorremos todos los errores de validación
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // error.getField() = nombre del campo con error
            // error.getDefaultMessage() = mensaje de validación
            errores.put(error.getField(), error.getDefaultMessage());
        });

        // Creamos respuesta con los errores
        ApiResponse<Map<String, String>> respuesta = ApiResponse.error(
                "Error de validación",
                errores
        );

        // Retornamos con código HTTP 400 (Bad Request)
        return ResponseEntity.badRequest().body(respuesta);
    }

    /**
     * MANEJO DE EXCEPCIÓN: Cualquier otra excepción
     * =============================================
     * Captura cualquier excepción no manejada específicamente.
     *
     * Es importante para evitar que se expongan detalles internos al cliente.
     *
     * @param ex La excepción capturada
     * @return ResponseEntity con mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> manejarExceptionGeneral(Exception ex) {
        // Log del error real (para debugging)
        ex.printStackTrace();

        // Mensaje genérico para el cliente
        ApiResponse<Object> respuesta = ApiResponse.error(
                "Ocurrió un error interno en el servidor"
        );

        // Retornamos con código HTTP 500 (Internal Server Error)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}