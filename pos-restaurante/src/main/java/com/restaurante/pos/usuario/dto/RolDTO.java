package com.restaurante.pos.usuario.dto;

import lombok.Data;

/**
 * DTO PARA RESPUESTA DE ROL
 * =========================
 *
 * PROPÓSITO:
 * - Define qué datos del rol se envían al cliente en la respuesta JSON
 * - Se usa como objeto anidado dentro de UsuarioResponseDTO
 *
 * EJEMPLO DE RESPUESTA JSON:
 * {
 *   "id": 1,
 *   "nombre": "ADMIN",
 *   "descripcion": "Administrador con acceso completo"
 * }
 */
@Data
public class RolDTO {

    // ==========================================================================
    // DATOS DEL ROL
    // ==========================================================================

    /**
     * ID único del rol.
     */
    private Long id;

    /**
     * Nombre del rol.
     * Ejemplos: "ADMIN", "VENDEDOR", "AUDITOR"
     */
    private String nombre;

    /**
     * Descripción del rol y sus permisos.
     * Explica qué puede hacer este rol en el sistema.
     */
    private String descripcion;
}
