package com.restaurante.pos.inventario.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO DE FILTRO PARA BÚSQUEDA DE MOVIMIENTOS
 * ==========================================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Recibir los criterios de búsqueda para filtrar
 * movimientos de inventario en los reportes y auditorías.
 *
 * QUÉ REPRESENTA:
 * Un conjunto de criterios opcionales que el usuario puede combinar
 * para encontrar movimientos específicos dentro del historial.
 *
 * CAMPOS FILTRABLES:
 * - Producto: filtra movimientos de un solo producto.
 * - Tipo: solo ENTRADA o solo SALIDA.
 * - Motivo: COMPRA, VENTA, MERMA, AJUSTE, etc.
 * - Usuario: quién realizó el movimiento.
 * - Rango de fechas: período de tiempo a consultar.
 *
 * POR QUÉ EXISTE:
 * - Permite construir consultas dinámicas donde cada filtro es opcional.
 * - Facilita la generación de reportes personalizados desde el frontend.
 * - Centraliza los parámetros de búsqueda en un solo objeto estructurado.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class FiltroMovimientoDTO {

    /** ID del producto cuyos movimientos se quieren consultar. Opcional. */
    private Long productoId;

    /** Tipo de movimiento: "ENTRADA" o "SALIDA". Opcional. */
    private String tipo;

    /** Motivo de movimiento: "COMPRA", "VENTA", "MERMA", etc. Opcional. */
    private String motivo;

    /** Fecha y hora inicial del rango de búsqueda. Opcional. */
    private LocalDateTime fechaDesde;

    /** Fecha y hora final del rango de búsqueda. Opcional. */
    private LocalDateTime fechaHasta;

    /** ID del usuario que realizó los movimientos. Opcional. */
    private Long usuarioId;
}
