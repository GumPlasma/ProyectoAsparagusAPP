package com.restaurante.pos.usuario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO PARA CREAR USUARIO
 * ======================
 *
 * DTO (Data Transfer Object) = Objeto para transferir datos entre capas.
 *
 * ¿POR QUÉ USAR DTO EN VEZ DE LA ENTIDAD DIRECTAMENTE?
 * 1. Seguridad: No exponemos todos los campos de la entidad
 * 2. Validación: Centralizamos las reglas de validación aquí
 * 3. Desacoplamiento: Cambios en la BD no afectan la API directamente
 *
 * ANOTACIONES DE VALIDACIÓN (@Valid en el controller activa estas):
 * - @NotBlank: El campo no puede estar vacío o null
 * - @Size: Longitud mínima/máxima del string
 * - @Email: Valida formato de email
 * - @NotNull: El campo no puede ser null (para objetos como Long)
 */
@Data  // Lombok genera getters, setters, toString, equals, hashCode
public class CrearUsuarioDTO {

    // ==========================================================================
    // CAMPOS DE AUTENTICACIÓN
    // ==========================================================================

    /**
     * Nombre de usuario para login.
     * Debe ser único en el sistema.
     *
     * VALIDACIONES:
     * - @NotBlank: No puede estar vacío
     * - @Size: Entre 3 y 50 caracteres (mínimo para evitar usernames muy cortos)
     */
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;

    /**
     * Contraseña del usuario.
     * Se encriptará con BCrypt antes de guardar.
     *
     * VALIDACIONES:
     * - @NotBlank: Obligatoria para crear usuario
     * - @Size: Mínimo 6 caracteres (seguridad básica)
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // ==========================================================================
    // CAMPOS DE INFORMACIÓN PERSONAL
    // ==========================================================================

    /**
     * Nombre real del usuario.
     *
     * VALIDACIONES:
     * - @NotBlank: Obligatorio
     * - @Size: Máximo 100 caracteres (según BD)
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    /**
     * Apellido del usuario.
     *
     * VALIDACIONES:
     * - @NotBlank: Obligatorio
     * - @Size: Máximo 100 caracteres
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;

    // ==========================================================================
    // CAMPOS DE CONTACTO (OPCIONALES)
    // ==========================================================================

    /**
     * Email del usuario.
     *
     * VALIDACIONES:
     * - @Email: Valida formato (ej: usuario@ejemplo.com)
     * - No tiene @NotBlank → es opcional
     */
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Teléfono del usuario.
     *
     * SIN VALIDACIONES:
     * - Campo completamente opcional
     * - Se aceptará cualquier string (o null)
     */
    private String telefono;

    // ==========================================================================
    // CAMPOS DE CONFIGURACIÓN
    // ==========================================================================

    /**
     * ID del rol asignado al usuario.
     *
     * VALIDACIONES:
     * - @NotNull: Obligatorio (todo usuario debe tener un rol)
     * - El servicio validará que el rol exista en la BD
     */
    @NotNull(message = "El rol es obligatorio")
    private Long rolId;
}