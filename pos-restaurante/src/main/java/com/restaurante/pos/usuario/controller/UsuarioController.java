package com.restaurante.pos.usuario.controller;

import com.restaurante.pos.common.ApiResponse;
import com.restaurante.pos.usuario.dto.*;
import com.restaurante.pos.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLADOR DE USUARIO
 * ======================
 *
 * El Controller es la capa que expone los endpoints de la API REST.
 * Recibe las peticiones HTTP y delega al Service.
 *
 * ANOTACIONES:
 * - @RestController: Combina @Controller + @ResponseBody
 *   (todas las respuestas son JSON automáticamente)
 * - @RequestMapping: Define la URL base para todos los métodos
 * - @RequiredArgsConstructor: Inyección por constructor
 *
 * ENDPOINTS DISPONIBLES:
 * - GET    /usuarios      → Listar todos
 * - GET    /usuarios/{id} → Obtener por ID
 * - POST   /usuarios      → Crear nuevo
 * - PUT    /usuarios/{id} → Actualizar
 * - DELETE /usuarios/{id} → Eliminar
 * - GET    /roles         → Listar roles
 * - POST   /roles         → Crear rol
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios y roles")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ==========================================
    // ENDPOINTS DE USUARIO
    // ==========================================

    /**
     * GET /api/usuarios
     * Obtiene todos los usuarios activos.
     */
    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios activos del sistema")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> obtenerTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(ApiResponse.exito(usuarios, "Usuarios obtenidos correctamente"));
    }

    /**
     * GET /api/usuarios/{id}
     * Obtiene un usuario por su ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario", description = "Obtiene un usuario por su ID")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito(usuario));
    }

    /**
     * POST /api/usuarios
     * Crea un nuevo usuario.
     *
     * @Valid activa las validaciones del DTO.
     */
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> crear(
            @Valid @RequestBody CrearUsuarioDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.crear(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito(usuario, "Usuario creado exitosamente"));
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualiza un usuario existente.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.actualizar(id, dto);
        return ResponseEntity.ok(ApiResponse.exito(usuario, "Usuario actualizado exitosamente"));
    }

    /**
     * DELETE /api/usuarios/{id}
     * Elimina un usuario (borrado lógico).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (borrado lógico)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.exito("Usuario eliminado exitosamente"));
    }

    // ==========================================
    // ENDPOINTS DE ROL
    // ==========================================

    /**
     * GET /api/usuarios/roles
     * Obtiene todos los roles disponibles.
     */
    @GetMapping("/roles")
    @Operation(summary = "Listar roles", description = "Obtiene todos los roles del sistema")
    public ResponseEntity<ApiResponse<List<RolDTO>>> obtenerRoles() {
        List<RolDTO> roles = usuarioService.obtenerRoles();
        return ResponseEntity.ok(ApiResponse.exito(roles, "Roles obtenidos correctamente"));
    }

    /**
     * POST /api/usuarios/roles
     * Crea un nuevo rol.
     */
    @PostMapping("/roles")
    @Operation(summary = "Crear rol", description = "Crea un nuevo rol en el sistema")
    public ResponseEntity<ApiResponse<RolDTO>> crearRol(@Valid @RequestBody CrearRolDTO dto) {
        RolDTO rol = usuarioService.crearRol(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.exito(rol, "Rol creado exitosamente"));
    }
}
