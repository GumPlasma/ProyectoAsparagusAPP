package com.restaurante.pos.usuario.entity;

import com.restaurante.pos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ENTIDAD ROL
 * ===========
 *
 * Representa los roles de usuario en el sistema (ADMIN, VENDEDOR, AUDITOR).
 *
 * ROLES DEL SISTEMA:
 * - ADMIN: Acceso total, puede gestionar usuarios, productos, inventario, ver reportes
 * - VENDEDOR: Puede realizar ventas, consultar productos y precios
 * - AUDITOR: Solo puede consultar y generar reportes, no puede modificar datos
 *
 * RELACIÓN CON USUARIO:
 * - Un rol puede tener muchos usuarios (1:N)
 * - Ejemplo: El rol "VENDEDOR" puede tener 10 usuarios asignados
 */
@Entity
@Table(name = "rol")  // Nombre de la tabla en la base de datos
@Getter
@Setter
public class Rol extends BaseEntity {

    // ==========================================================================
    // CAMPOS DE LA ENTIDAD
    // ==========================================================================

    /**
     * Nombre del rol.
     * Usado para identificar el rol en el código.
     * Ejemplos: "ADMIN", "VENDEDOR", "AUDITOR"
     *
     * ANOTACIONES:
     * - @Column: Define la columna en la tabla 'rol'
     * - nullable = false: No puede ser nulo (todo rol debe tener nombre)
     * - unique = true: No puede haber dos roles con el mismo nombre
     * - length = 50: Máximo 50 caracteres en la base de datos
     */
    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    /**
     * Descripción del rol y sus permisos.
     * Información para el administrador sobre qué puede hacer este rol.
     * Ejemplo: "Administrador del sistema con acceso completo a todas las funcionalidades"
     *
     * ANOTACIONES:
     * - length = 255: Máximo 255 caracteres
     * - No es obligatorio (puede ser null)
     */
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // ==========================================================================
    // MÉTODOS HEREDADOS DE BaseEntity
    // ==========================================================================
    // - getId(): Obtiene el identificador único del rol
    // - getFechaCreacion(): Fecha cuando se creó el rol
    // - getFechaActualizacion(): Fecha de última modificación
    // - getActivo(): Indica si el rol está activo (borrado lógico)
    // - eliminar(): Marca el rol como inactivo
    // - restaurar(): Restaura un rol eliminado
}