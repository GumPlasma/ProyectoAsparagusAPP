package com.restaurante.pos.usuario.repository;

import com.restaurante.pos.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE USUARIO
 * ======================
 *
 * Proporciona acceso a los datos de usuarios en la base de datos.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su username (para login).
     *
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica si ya existe un username.
     * Útil para validar antes de crear.
     *
     * @param username Nombre de usuario a verificar
     * @return true si ya existe
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si ya existe un email.
     *
     * @param email Email a verificar
     * @return true si ya existe
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por rol.
     *
     * @param rolId ID del rol
     * @return Lista de usuarios con ese rol
     */
    List<Usuario> findByRolId(Long rolId);

    /**
     * Busca usuarios activos por rol.
     * Consulta personalizada con @Query.
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol.id = :rolId AND u.activo = true")
    List<Usuario> findActivosByRolId(@Param("rolId") Long rolId);

    /**
     * Busca usuarios por nombre o apellido (búsqueda parcial).
     * Ignora mayúsculas/minúsculas.
     *
     * @param nombre Texto a buscar
     * @return Lista de usuarios que coinciden
     */
    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido);
}