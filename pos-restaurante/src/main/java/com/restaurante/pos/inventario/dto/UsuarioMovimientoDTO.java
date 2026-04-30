package com.restaurante.pos.inventario.dto;

import lombok.Data;

/**
 * DTO DE USUARIO PARA MOVIMIENTOS
 * ===============================
 *
 * CAPA: DTO (Data Transfer Object)
 * RESPONSABILIDAD: Proporcionar una vista resumida del usuario
 * dentro del contexto de un movimiento de inventario.
 *
 * QUÉ REPRESENTA:
 * La información mínima necesaria para identificar quién realizó
 * o autorizó un movimiento de stock, sin exponer datos sensibles
 * del usuario (contraseña, roles, permisos, etc.).
 *
 * POR QUÉ EXISTE:
 * - Desacopla el módulo de inventario del módulo de usuarios.
 * - Evita exponer la entidad Usuario completa en las respuestas de la API.
 * - Permite mostrar el nombre del responsable en reportes y kardex.
 *
 * ANOTACIÓN LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 */
@Data
public class UsuarioMovimientoDTO {

    /** Identificador único del usuario. */
    private Long id;

    /** Nombre de usuario (login) del responsable. */
    private String username;

    /** Nombre completo del usuario para mostrar en interfaces. */
    private String nombreCompleto;
}
