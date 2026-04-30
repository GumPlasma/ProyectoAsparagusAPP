package com.restaurante.pos.cliente.service;

import com.restaurante.pos.cliente.dto.ClienteDTO;
import com.restaurante.pos.cliente.entity.Cliente;
import com.restaurante.pos.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICIO DE CLIENTE
 * ===================
 *
 * CAPA: Service (Lógica de Negocio)
 * RESPONSABILIDAD: Contener la lógica de negocio del módulo de clientes.
 *
 * ¿QUÉ HACE?
 * - Orquesta las operaciones de negocio (validaciones, reglas, transformaciones).
 * - Garantiza la consistencia de datos mediante transacciones.
 * - Actúa como intermediario entre el Controller y el Repository.
 * - Convierte entidades a DTOs para no exponer directamente el modelo de persistencia.
 *
 * ANOTACIONES SPRING:
 * - @Service: Marca la clase como componente de servicio de Spring, detectable por escaneo de componentes.
 * - @RequiredArgsConstructor: Genera un constructor con los campos final para inyección de dependencias.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================
    // Repositorio que abstrae el acceso a la base de datos de clientes.
    // Se inyecta por constructor gracias a @RequiredArgsConstructor.
    private final ClienteRepository clienteRepository;

    // ============================================================
    // CONSULTAS (READ-ONLY)
    // ============================================================

    /**
     * Recupera todos los clientes activos registrados en el sistema.
     *
     * @return Lista de ClienteDTO con los datos de cada cliente activo.
     *
     * Filtra por activo=true para evitar devolver registros eliminados lógicamente.
     * Usa Streams de Java 8 para transformar cada entidad en su DTO equivalente.
     */
    public List<ClienteDTO> obtenerTodos() {
        // Consulta solo clientes activos; luego convierte cada entidad a DTO.
        return clienteRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un cliente por su identificador único.
     *
     * @param id Identificador numérico del cliente.
     * @return ClienteDTO con la información del cliente.
     * @throws RuntimeException si no existe un cliente con ese id.
     *
     * Usa Optional de Java para manejar la posible ausencia del registro.
     */
    public ClienteDTO obtenerPorId(Long id) {
        // findById devuelve Optional; si está vacío se lanza excepción controlada.
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return convertirADTO(cliente);
    }

    /**
     * Realiza una búsqueda difusa de clientes por un término de texto.
     *
     * @param termino Texto a buscar (coincide con nombre, apellido, dni o teléfono).
     * @return Lista de ClienteDTO que cumplen el criterio.
     *
     * La consulta JPQL en el Repository usa LOWER y LIKE para búsqueda insensible a mayúsculas.
     */
    public List<ClienteDTO> buscar(String termino) {
        // Obtiene entidades coincidentes y las mapea a DTOs antes de devolverlas.
        return clienteRepository.buscar(termino).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // ============================================================
    // OPERACIONES DE ESCRITURA
    // ============================================================

    /**
     * Crea un nuevo cliente validando previamente la unicidad de DNI y email.
     *
     * @param dto Datos del cliente a crear.
     * @return ClienteDTO del cliente persistido.
     * @throws RuntimeException si ya existe otro cliente con el mismo DNI o email.
     *
     * @Transactional asegura que la operación sea atómica: si falla algo,
     *                se hace rollback completo de la transacción de base de datos.
     */
    @Transactional
    public ClienteDTO crear(ClienteDTO dto) {
        // ---------- Validación de unicidad ----------
        // Verifica que el DNI no esté registrado por otro cliente.
        if (dto.getDni() != null && clienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese DNI");
        }

        // Verifica que el email no esté registrado por otro cliente.
        if (dto.getEmail() != null && clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese email");
        }

        // ---------- Construcción de la entidad ----------
        // Usa el patrón Builder de Lombok para crear la entidad de forma limpia.
        // El campo activo se inicializa en true para indicar que el registro está vigente.
        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .direccion(dto.getDireccion())
                .notas(dto.getNotas())
                .activo(true)
                .build();

        // Guarda la entidad en la base de datos y convierte el resultado a DTO.
        return convertirADTO(clienteRepository.save(cliente));
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id  Identificador del cliente a modificar.
     * @param dto Objeto con los nuevos valores.
     * @return ClienteDTO con la información actualizada.
     * @throws RuntimeException si el cliente no existe o si se violan restricciones de unicidad.
     *
     * @Transactional garantiza consistencia durante la modificación.
     */
    @Transactional
    public ClienteDTO actualizar(Long id, ClienteDTO dto) {
        // Localiza el cliente; si no existe lanza excepción.
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // ---------- Validación de unicidad al cambiar DNI ----------
        // Solo valida si el DNI fue modificado y es diferente al actual.
        if (dto.getDni() != null && !dto.getDni().equals(cliente.getDni())) {
            if (clienteRepository.findByDni(dto.getDni()).isPresent()) {
                throw new RuntimeException("Ya existe un cliente con ese DNI");
            }
        }

        // ---------- Validación de unicidad al cambiar email ----------
        // Solo valida si el email fue modificado y es diferente al actual.
        if (dto.getEmail() != null && !dto.getEmail().equals(cliente.getEmail())) {
            if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("Ya existe un cliente con ese email");
            }
        }

        // ---------- Aplicación de cambios ----------
        // Actualiza los campos directamente en la entidad gestionada por JPA.
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setDni(dto.getDni());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setDireccion(dto.getDireccion());
        cliente.setNotas(dto.getNotas());

        // Persiste los cambios y devuelve la representación en DTO.
        return convertirADTO(clienteRepository.save(cliente));
    }

    /**
     * Realiza una eliminación lógica del cliente.
     *
     * @param id Identificador del cliente a desactivar.
     * @throws RuntimeException si el cliente no existe.
     *
     * En lugar de borrar el registro físico, marca activo=false.
     * Esto preserva el historial y evita pérdida de integridad referencial.
     */
    @Transactional
    public void eliminar(Long id) {
        // Busca el cliente; si no existe aborta la operación.
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Desactiva el cliente en lugar de eliminarlo físicamente.
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    // ============================================================
    // CONVERSIÓN ENTIDAD ↔ DTO
    // ============================================================

    /**
     * Convierte una entidad Cliente en su correspondiente ClienteDTO.
     *
     * @param cliente Entidad persistente obtenida de la base de datos.
     * @return ClienteDTO con los mismos datos, listo para enviar al cliente HTTP.
     *
     * Este método privado centraliza la transformación y evita repetir código.
     * Usa el patrón Builder de Lombok para construir el DTO de forma declarativa.
     */
    private ClienteDTO convertirADTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .direccion(cliente.getDireccion())
                .notas(cliente.getNotas())
                .totalCompras(cliente.getTotalCompras())
                .activo(cliente.getActivo())
                .build();
    }
}
