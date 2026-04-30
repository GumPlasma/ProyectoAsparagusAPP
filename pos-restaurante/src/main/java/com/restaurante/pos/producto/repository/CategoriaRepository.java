package com.restaurante.pos.producto.repository;

import com.restaurante.pos.producto.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO DE CATEGORÍA
 * ========================
 *
 * Proporciona acceso a los datos de categorías en la base de datos.
 *
 * ¿QUÉ ES UN REPOSITORY?
 * - Es una interfaz que extiende JpaRepository
 * - Spring Data JPA genera automáticamente la implementación
 * - Proporciona métodos CRUD sin escribir código SQL
 *
 * MÉTODOS HEREDADOS DE JpaRepository:
 * - save(categoria) → Guarda o actualiza
 * - findById(id) → Busca por ID (retorna Optional)
 * - findAll() → Obtiene todas las categorías
 * - deleteById(id) → Elimina por ID
 * - count() → Cuenta el total de registros
 * - existsById(id) → Verifica si existe por ID
 */
@Repository  // Marca esta interfaz como repositorio de Spring
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // ==========================================================================
    // MÉTODOS DE BÚSQUEDA PERSONALIZADOS
    // ==========================================================================
    // Spring Data JPA genera la consulta automáticamente según el nombre del método

    /**
     * Busca una categoría por su nombre exacto.
     *
     * CONVENCION DE NOMBRES:
     * - findByNombre → Spring genera: SELECT c FROM Categoria c WHERE c.nombre = ?
     * - El nombre del método define la consulta automáticamente
     *
     * @param nombre Nombre exacto de la categoría
     * @return Optional con la categoría si existe, vacío si no
     *
     * EJEMPLO DE USO:
     * Optional<Categoria> result = repo.findByNombre("Bebidas");
     * result.ifPresent(c -> System.out.println(c.getDescripcion()));
     */
    Optional<Categoria> findByNombre(String nombre);

    /**
     * Verifica si existe una categoría con el nombre dado.
     *
     * CONVENCION DE NOMBRES:
     * - existsByNombre → Spring genera: SELECT COUNT(c) > 0 FROM Categoria c WHERE c.nombre = ?
     *
     * @param nombre Nombre de la categoría a verificar
     * @return true si existe, false si no
     *
     * EJEMPLO DE USO:
     * if (repo.existsByNombre("Bebidas")) { ... }
     */
    boolean existsByNombre(String nombre);

    /**
     * Obtiene todas las categorías ordenadas por el campo 'orden'.
     *
     * CONVENCION DE NOMBRES:
     * - findAllByOrderByOrdenAsc → Orden ascendente por campo 'orden'
     *
     * @return Lista de categorías ordenadas de menor a mayor orden
     *
     * EJEMPLO DE USO:
     * List<Categoria> categorias = repo.findAllByOrderByOrdenAsc();
     * // Retorna: [Entradas (orden=1), Platos (orden=2), Postres (orden=3)]
     */
    List<Categoria> findAllByOrderByOrdenAsc();

    // ==========================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ==========================================================================
    // Usamos JPQL (Java Persistence Query Language) en vez de SQL
    // JPQL trabaja con entidades, no con tablas

    /**
     * Obtiene todas las categorías activas ordenadas.
     *
     * CONSULTA JPQL:
     * "SELECT c FROM Categoria c WHERE c.activo = true ORDER BY c.orden ASC"
     * - Selecciona solo categorías con activo = true
     * - Ordena por el campo 'orden' de forma ascendente
     *
     * @return Lista de categorías activas ordenadas
     */
    @Query("SELECT c FROM Categoria c WHERE c.activo = true ORDER BY c.orden ASC")
    List<Categoria> findAllActivasOrderByOrden();

    /**
     * Obtiene categorías que tienen al menos un producto activo.
     *
     * CONSULTA JPQL:
     * "SELECT DISTINCT c FROM Categoria c JOIN c.productos p
     *  WHERE c.activo = true AND p.activo = true ORDER BY c.orden ASC"
     *
     * - JOIN c.productos p: Une categoría con sus productos
     * - DISTINCT: Evita duplicados si hay múltiples productos
     * - Solo retorna categorías con productos activos
     *
     * @return Lista de categorías con productos activos, ordenadas
     */
    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.productos p WHERE c.activo = true AND p.activo = true ORDER BY c.orden ASC")
    List<Categoria> findCategoriasConProductosActivos();
}