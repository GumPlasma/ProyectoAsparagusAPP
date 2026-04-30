package com.restaurante.pos.usuario.service;

import com.restaurante.pos.usuario.dto.*;
import com.restaurante.pos.usuario.entity.Rol;
import com.restaurante.pos.usuario.entity.Usuario;
import com.restaurante.pos.usuario.repository.RolRepository;
import com.restaurante.pos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICIO DE USUARIO
 * ===================
 *
 * La capa Service contiene la LÓGICA DE NEGOCIO.
 *
 * RESPONSABILIDADES:
 * 1. Aplicar reglas de negocio
 * 2. Validar datos antes de guardar
 * 3. Coordinar entre repositories
 * 4. Transformar entidades a DTOs
 *
 * ANOTACIONES:
 * - @Service: Marca esta clase como un servicio de Spring
 * - @Transactional: Las operaciones se ejecutan en transacciones
 * - @RequiredArgsConstructor: Lombok crea constructor con campos final
 */
@Service
@Transactional
@RequiredArgsConstructor  // Inyección por constructor (mejor práctica)
public class UsuarioService {

    // ==========================================
    // DEPENDENCIAS (inyectadas por constructor)
    // ==========================================
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // ==========================================
    // MÉTODOS DE USUARIO
    // ==========================================

    /**
     * Obtener todos los usuarios activos.
     */
    @Transactional(readOnly = true)  // Solo lectura, más eficiente
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getActivo())  // Solo activos
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuario por ID.
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertirAResponseDTO(usuario);
    }

    /**
     * Crear nuevo usuario.
     */
    public UsuarioResponseDTO crear(CrearUsuarioDTO dto) {
        // Validar que username no exista
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con username: " + dto.getUsername());
        }

        // Validar que el rol exista
        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + dto.getRolId()));

        // Crear la entidad
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Encriptar
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);

        // Guardar y retornar
        usuario = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuario);
    }

    /**
     * Actualizar usuario existente.
     */
    public UsuarioResponseDTO actualizar(Long id, ActualizarUsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizar campos si vienen en el DTO
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getApellido() != null) {
            usuario.setApellido(dto.getApellido());
        }
        if (dto.getEmail() != null) {
            usuario.setEmail(dto.getEmail());
        }
        if (dto.getTelefono() != null) {
            usuario.setTelefono(dto.getTelefono());
        }
        if (dto.getRolId() != null) {
            Rol rol = rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            usuario.setRol(rol);
        }
        if (dto.getPasswordNuevo() != null && !dto.getPasswordNuevo().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPasswordNuevo()));
        }

        usuario = usuarioRepository.save(usuario);
        return convertirAResponseDTO(usuario);
    }

    /**
     * Eliminar usuario (borrado lógico).
     */
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.eliminar();  // Soft delete
        usuarioRepository.save(usuario);
    }

    // ==========================================
    // MÉTODOS DE ROL
    // ==========================================

    /**
     * Obtener todos los roles.
     */
    @Transactional(readOnly = true)
    public List<RolDTO> obtenerRoles() {
        return rolRepository.findAll()
                .stream()
                .map(this::convertirRolADTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear nuevo rol.
     */
    public RolDTO crearRol(CrearRolDTO dto) {
        if (rolRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe un rol con nombre: " + dto.getNombre());
        }

        Rol rol = new Rol();
        rol.setNombre(dto.getNombre().toUpperCase());
        rol.setDescripcion(dto.getDescripcion());

        rol = rolRepository.save(rol);
        return convertirRolADTO(rol);
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE CONVERSIÓN
    // ==========================================

    /**
     * Convierte entidad Usuario a DTO de respuesta.
     */
    private UsuarioResponseDTO convertirAResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setActivo(usuario.getActivo());

        // Convertir rol
        if (usuario.getRol() != null) {
            dto.setRol(convertirRolADTO(usuario.getRol()));
        }

        return dto;
    }

    /**
     * Convierte entidad Rol a DTO.
     */
    private RolDTO convertirRolADTO(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        return dto;
    }
}