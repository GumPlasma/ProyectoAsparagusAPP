package com.restaurante.pos.usuario.entity;

import com.restaurante.pos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ENTIDAD USUARIO
 * ===============
 *
 * Representa a los usuarios del sistema POS.
 * Cada usuario tiene un rol que determina sus permisos.
 *
 * SEGURIDAD:
 * - La contraseña se almacena hasheada (BCrypt)
 * - Nunca almacenar contraseñas en texto plano
 *
 * RELACIONES:
 * - Muchos usuarios pueden tener el mismo rol (N:1)
 * - Un usuario puede realizar muchas ventas (1:N con Venta)
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
public class Usuario extends BaseEntity {

    // ==========================================================================
    // CAMPOS DE IDENTIFICACIÓN Y AUTENTICACIÓN
    // ==========================================================================

    /**
     * Nombre de usuario único para login.
     * No puede repetirse entre usuarios.
     *
     * ANOTACIONES:
     * - @Column: Define la columna en la tabla 'usuario'
     * - nullable = false: Campo obligatorio
     * - unique = true: No puede haber dos usuarios con el mismo username
     * - length = 50: Máximo 50 caracteres
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Contraseña hasheada del usuario.
     * NUNCA almacenar en texto plano.
     * Se encripta con BCrypt en el servicio antes de guardar.
     *
     * ANOTACIONES:
     * - length = 255: Suficiente para hash BCrypt (60 chars) + margen
     * - La validación de fortaleza se hace en el DTO, no aquí
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // ==========================================================================
    // CAMPOS DE INFORMACIÓN PERSONAL
    // ==========================================================================

    /**
     * Nombre real del usuario.
     * Ejemplo: "Juan"
     *
     * ANOTACIONES:
     * - nullable = false: Obligatorio
     * - length = 100: Máximo 100 caracteres
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Apellido del usuario.
     * Ejemplo: "Pérez"
     *
     * ANOTACIONES:
     * - nullable = false: Obligatorio
     * - length = 100: Máximo 100 caracteres
     */
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    /**
     * Email del usuario.
     * Puede usarse para recuperación de contraseña o notificaciones.
     *
     * ANOTACIONES:
     * - length = 150: Suficiente para emails largos
     * - No es único (podría cambiarse si se necesita)
     */
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Teléfono del usuario.
     * Útil para contacto o recuperación de cuenta.
     *
     * ANOTACIONES:
     * - length = 20: Permite formatos internacionales (+57 300 123 4567)
     * - Campo opcional (puede ser null)
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    // ==========================================================================
    // RELACIONES CON OTRAS ENTIDADES
    // ==========================================================================

    /**
     * RELACIÓN CON ROL
     * ===============
     * @ManyToOne: Muchos usuarios pueden tener el mismo rol
     * @JoinColumn: Foreign key 'rol_id' en la tabla usuario
     * fetch = FetchType.EAGER: Carga el rol inmediatamente (útil para permisos)
     *
     * EJEMPLO DE USO:
     * - usuario.getRol().getNombre() → "ADMIN"
     *
     * EN LA BASE DE DATOS:
     * La tabla 'usuario' tendrá una columna 'rol_id' que referencia a 'rol.id'
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // ==========================================================================
    // MÉTODOS AUXILIARES
    // ==========================================================================

    /**
     * Obtiene el nombre completo del usuario.
     * Útil para mostrar en la interfaz (ej: "Juan Pérez" en vez de "jperez").
     *
     * @return Nombre y apellido concatenados
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    // ==========================================================================
    // MÉTODOS HEREDADOS DE BaseEntity
    // ==========================================================================
    // - getId(): ID único del usuario
    // - getFechaCreacion(): Fecha de registro del usuario
    // - getFechaActualizacion(): Última modificación
    // - getActivo(): true si el usuario está activo
    // - eliminar(): Desactiva usuario (soft delete)
    // - restaurar(): Reactiva usuario
}