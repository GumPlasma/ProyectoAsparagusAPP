package com.restaurante.pos.cliente.controller;

import com.restaurante.pos.cliente.dto.ClienteDTO;
import com.restaurante.pos.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * CONTROLADOR REST DEL MÓDULO CLIENTE
 * ===================================
 *
 * CAPA: Controller (Controlador REST)
 * RESPONSABILIDAD: Exponer endpoints HTTP para la gestión de clientes del restaurante.
 *
 * ¿QUÉ HACE?
 * - Recibe peticiones HTTP relacionadas con clientes.
 * - Delega toda la lógica de negocio a la capa Service.
 * - Mapea rutas y parámetros de la URL a llamadas de servicio.
 * - NO contiene lógica de negocio; solo coordina entrada/salida.
 *
 * ANOTACIONES SPRING:
 * - @RestController: Combina @Controller y @ResponseBody, indica que todos los métodos
 *   devuelven datos serializados directamente (JSON por defecto).
 * - @RequestMapping("/clientes"): Define la ruta base para todos los endpoints de esta clase.
 * - @RequiredArgsConstructor: Genera un constructor con los campos final (inyección de dependencias).
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class ClienteController {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================
    // Se inyecta el servicio de clientes mediante constructor (final + @RequiredArgsConstructor).
    // Esto permite que el controller delegue la ejecución en la capa de negocio.
    private final ClienteService clienteService;

    // ============================================================
    // OPERACIONES CRUD Y BÚSQUEDA
    // ============================================================

    /**
     * Obtiene la lista completa de clientes activos.
     *
     * @return List<ClienteDTO> lista de clientes convertidos a DTO.
     *
     * @GetMapping mapea peticiones HTTP GET a la ruta base /clientes.
     * No requiere parámetros.
     */
    @GetMapping
    public List<ClienteDTO> obtenerTodos() {
        // Delega la consulta al servicio y devuelve el resultado como JSON.
        return clienteService.obtenerTodos();
    }

    /**
     * Obtiene un único cliente por su identificador numérico.
     *
     * @param id Identificador del cliente extraído de la URL.
     * @return ClienteDTO con los datos del cliente encontrado.
     *
     * @GetMapping("/{id}") captura el valor de la variable de ruta.
     * @PathVariable vincula el segmento {id} de la URL al parámetro del método.
     */
    @GetMapping("/{id}")
    public ClienteDTO obtenerPorId(@PathVariable Long id) {
        // Pasa el id al servicio para buscar el cliente correspondiente.
        return clienteService.obtenerPorId(id);
    }

    /**
     * Busca clientes que coincidan con un término de búsqueda.
     *
     * @param termino Texto a buscar (nombre, apellido, dni o teléfono).
     * @return List<ClienteDTO> con los clientes coincidentes.
     *
     * @GetMapping("/buscar") expone el endpoint /clientes/buscar.
     * @RequestParam extrae el valor del query string (?termino=...).
     */
    @GetMapping("/buscar")
    public List<ClienteDTO> buscar(@RequestParam String termino) {
        // Delega la búsqueda difusa al servicio.
        return clienteService.buscar(termino);
    }

    /**
     * Crea un nuevo cliente en el sistema.
     *
     * @param dto Objeto con los datos del cliente enviado en el cuerpo de la petición.
     * @return ClienteDTO del cliente recién creado.
     *
     * @PostMapping mapea peticiones HTTP POST a la ruta base /clientes.
     * @RequestBody indica que Spring debe deserializar el cuerpo JSON en un objeto ClienteDTO.
     */
    @PostMapping
    public ClienteDTO crear(@RequestBody ClienteDTO dto) {
        // Envía los datos al servicio para validar y persistir el nuevo cliente.
        return clienteService.crear(dto);
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id  Identificador del cliente a modificar (ruta).
     * @param dto Objeto con los nuevos datos (cuerpo de la petición).
     * @return ClienteDTO con la información actualizada.
     *
     * @PutMapping("/{id}") responde a peticiones PUT sobre /clientes/{id}.
     * Se combinan @PathVariable para el id y @RequestBody para el payload.
     */
    @PutMapping("/{id}")
    public ClienteDTO actualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
        // El servicio se encarga de validar unicidad y guardar los cambios.
        return clienteService.actualizar(id, dto);
    }

    /**
     * Elimina (lógicamente) un cliente del sistema.
     *
     * @param id Identificador del cliente a desactivar.
     * @return ResponseEntity<Void> con estado HTTP 200 (OK) si tuvo éxito.
     *
     * @DeleteMapping("/{id}") responde a peticiones DELETE sobre /clientes/{id}.
     * La eliminación es lógica: marca el campo activo como false.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Solicita al servicio la desactivación del cliente.
        clienteService.eliminar(id);
        // Construye una respuesta HTTP 200 sin contenido en el cuerpo.
        return ResponseEntity.ok().build();
    }
}
