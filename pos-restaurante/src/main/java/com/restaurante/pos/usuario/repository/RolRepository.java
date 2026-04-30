package com.restaurante.pos.usuario.repository;

import com.restaurante.pos.usuario.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORIO DE ROL
 * ==================
 *
 * Un Repository es la capa de acceso a datos.
 * JpaRepository ya proporciona métodos CRUD básicos:
 * - save(rol) → Guardar o actualizar
 * - findById(id) → Buscar por ID
 * - findAll() → Obtener todos
 * - deleteById(id) → Eliminar por ID
 *
 * Podemos definir métodos personalizados siguiendo convenciones:
 * - findByNombre → Busca por campo nombre.
 * - Spring genera la consulta automáticamente
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * EJEMPLO DE USO:
     * Rol admin = rolRepository.findByNombre("ADMIN").orElse(null);
     *
     * @param nombre Nombre del rol a buscar
     * @return Optional con el rol si existe, vacío si no existe
     */
    Optional<Rol> findByNombre(String nombre);

    /**
     * Verifica si existe un rol con el nombre dado.
     *
     * @param nombre Nombre del rol
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);
}