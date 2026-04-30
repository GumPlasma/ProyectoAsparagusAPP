package com.restaurante.pos.proveedor.service;

import com.restaurante.pos.proveedor.dto.ProveedorDTO;
import com.restaurante.pos.proveedor.entity.Proveedor;
import com.restaurante.pos.proveedor.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICIO DE PROVEEDOR
 * =====================
 *
 * CAPA: Service (Lógica de Negocio)
 * RESPONSABILIDAD: Contener la lógica de negocio para la gestión de proveedores.
 *
 * ¿QUÉ HACE?
 * - Orquesta las operaciones de negocio (validaciones, reglas, transformaciones).
 * - Garantiza la consistencia de datos mediante transacciones.
 * - Actúa como intermediario entre el Controller y el Repository.
 * - Convierte entidades a DTOs para no exponer directamente el modelo de persistencia.
 *
 * REGLAS DE NEGOCIO:
 * - El RUC debe ser único entre proveedores.
 * - El email debe ser único entre proveedores.
 * - No se puede crear un proveedor con RUC o email duplicado.
 * - Al actualizar, solo se valida unicidad si el valor cambió.
 * - La eliminación es lógica (soft delete), no física.
 *
 * ANOTACIONES SPRING:
 * - @Service: Marca la clase como componente de servicio de Spring.
 * - @RequiredArgsConstructor: Genera un constructor con los campos final para inyección de dependencias.
 */
@Service
@RequiredArgsConstructor
public class ProveedorService {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================

    /** Repositorio que abstrae el acceso a la base de datos de proveedores. */
    private final ProveedorRepository proveedorRepository;

    // ============================================================
    // CONSULTAS (READ-ONLY)
    // ============================================================

    /**
     * Obtiene todos los proveedores activos ordenados alfabéticamente por nombre.
     *
     * @return Lista de ProveedorDTO con los proveedores activos.
     */
    public List<ProveedorDTO> obtenerTodos() {
        return proveedorRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un proveedor por su identificador único.
     *
     * @param id Identificador numérico del proveedor.
     * @return ProveedorDTO con la información del proveedor.
     * @throws RuntimeException si no existe un proveedor con ese id.
     */
    public ProveedorDTO obtenerPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        return convertirADTO(proveedor);
    }

    /**
     * Realiza una búsqueda difusa de proveedores por un término de texto.
     *
     * @param termino Texto a buscar (coincide con nombre, RUC o contacto).
     * @return Lista de ProveedorDTO que cumplen el criterio.
     */
    public List<ProveedorDTO> buscar(String termino) {
        return proveedorRepository.buscar(termino).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene proveedores filtrados por categoría.
     *
     * @param categoria Nombre de la categoría (ej: "CARNES", "BEBIDAS").
     * @return Lista de ProveedorDTO de esa categoría.
     */
    public List<ProveedorDTO> obtenerPorCategoria(String categoria) {
        return proveedorRepository.findByCategoria(categoria).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // OPERACIONES DE ESCRITURA
    // ============================================================

    /**
     * Crea un nuevo proveedor validando previamente la unicidad de RUC y email.
     *
     * @param dto Datos del proveedor a crear.
     * @return ProveedorDTO del proveedor persistido.
     * @throws RuntimeException si ya existe otro proveedor con el mismo RUC o email.
     *
     * @Transactional asegura atomicidad: si falla algo, se hace rollback completo.
     */
    @Transactional
    public ProveedorDTO crear(ProveedorDTO dto) {
        // ---------- Validación de unicidad ----------
        if (dto.getRuc() != null && proveedorRepository.findByRuc(dto.getRuc()).isPresent()) {
            throw new RuntimeException("Ya existe un proveedor con ese RUC");
        }

        if (dto.getEmail() != null && proveedorRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un proveedor con ese email");
        }

        // ---------- Construcción de la entidad ----------
        Proveedor proveedor = Proveedor.builder()
                .nombre(dto.getNombre())
                .ruc(dto.getRuc())
                .contacto(dto.getContacto())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .direccion(dto.getDireccion())
                .categoria(dto.getCategoria())
                .notas(dto.getNotas())
                .activo(true)
                .build();

        return convertirADTO(proveedorRepository.save(proveedor));
    }

    /**
     * Actualiza los datos de un proveedor existente.
     *
     * @param id  Identificador del proveedor a modificar.
     * @param dto Objeto con los nuevos valores.
     * @return ProveedorDTO con la información actualizada.
     * @throws RuntimeException si el proveedor no existe o si se violan restricciones de unicidad.
     *
     * @Transactional garantiza consistencia durante la modificación.
     */
    @Transactional
    public ProveedorDTO actualizar(Long id, ProveedorDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        // ---------- Validación de unicidad al cambiar RUC ----------
        if (dto.getRuc() != null && !dto.getRuc().equals(proveedor.getRuc())) {
            if (proveedorRepository.findByRuc(dto.getRuc()).isPresent()) {
                throw new RuntimeException("Ya existe un proveedor con ese RUC");
            }
        }

        // ---------- Validación de unicidad al cambiar email ----------
        if (dto.getEmail() != null && !dto.getEmail().equals(proveedor.getEmail())) {
            if (proveedorRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("Ya existe un proveedor con ese email");
            }
        }

        // ---------- Aplicación de cambios ----------
        proveedor.setNombre(dto.getNombre());
        proveedor.setRuc(dto.getRuc());
        proveedor.setContacto(dto.getContacto());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setCategoria(dto.getCategoria());
        proveedor.setNotas(dto.getNotas());

        return convertirADTO(proveedorRepository.save(proveedor));
    }

    /**
     * Realiza una eliminación lógica del proveedor.
     *
     * @param id Identificador del proveedor a desactivar.
     * @throws RuntimeException si el proveedor no existe.
     *
     * En lugar de borrar el registro físico, marca activo=false.
     * Esto preserva el historial de compras asociadas.
     */
    @Transactional
    public void eliminar(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    // ============================================================
    // CONVERSIÓN ENTIDAD ↔ DTO
    // ============================================================

    /**
     * Convierte una entidad Proveedor en su correspondiente ProveedorDTO.
     *
     * @param proveedor Entidad persistente obtenida de la base de datos.
     * @return ProveedorDTO listo para enviar al cliente HTTP.
     *
     * Este método privado centraliza la transformación y evita repetir código.
     */
    private ProveedorDTO convertirADTO(Proveedor proveedor) {
        return ProveedorDTO.builder()
                .id(proveedor.getId())
                .nombre(proveedor.getNombre())
                .ruc(proveedor.getRuc())
                .contacto(proveedor.getContacto())
                .telefono(proveedor.getTelefono())
                .email(proveedor.getEmail())
                .direccion(proveedor.getDireccion())
                .categoria(proveedor.getCategoria())
                .notas(proveedor.getNotas())
                .activo(proveedor.getActivo())
                .build();
    }
}
