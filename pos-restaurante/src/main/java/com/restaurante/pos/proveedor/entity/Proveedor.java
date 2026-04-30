package com.restaurante.pos.proveedor.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ENTIDAD PROVEEDOR
 * =================
 *
 * CAPA: Entity (Entidad JPA)
 * RESPONSABILIDAD: Mapear la tabla "proveedores" de la base de datos.
 *
 * ¿QUÉ REPRESENTA?
 * Cada instancia de esta clase corresponde a UN proveedor que suministra productos al restaurante.
 * Ejemplos: proveedores de carnes, verduras, bebidas, insumos de cocina.
 *
 * RELACIONES:
 * - Un proveedor puede tener muchas facturas de compra (1:N con FacturaProveedor).
 *
 * SOFT DELETE:
 * El campo 'activo' permite realizar borrado lógico para preservar el historial de compras.
 *
 * ANOTACIONES JPA:
 * - @Entity: Declara esta clase como entidad gestionada por JPA/Hibernate.
 * - @Table(name = "proveedores"): Especifica el nombre exacto de la tabla en la BD.
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals y hashCode.
 * - @NoArgsConstructor: Constructor sin argumentos (requerido por JPA).
 * - @AllArgsConstructor: Constructor con todos los argumentos.
 * - @Builder: Habilita el patrón Builder.
 */
@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    // ============================================================
    // IDENTIFICADOR PRIMARIO
    // ============================================================

    /**
     * Identificador único del proveedor (clave primaria autoincremental).
     *
     * @Id: Marca este campo como la clave primaria.
     * @GeneratedValue(strategy = GenerationType.IDENTITY): Auto-incremento de la BD.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================================
    // DATOS DEL PROVEEDOR
    // ============================================================

    /**
     * Nombre comercial o razón social del proveedor.
     *
     * @Column(nullable = false): Campo obligatorio en la base de datos.
     * Ejemplo: "Carnes del Valle S.A.S.", "Distribuidora de Bebidas del Norte".
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Registro Único de Contribuyente (RUC) del proveedor.
     *
     * @Column(unique = true, length = 11):
     *   - unique=true: Garantiza que no haya dos proveedores con el mismo RUC.
     *   - length=11: Limita el tamaño para formatos estándar de RUC (11 dígitos en Perú, por ejemplo).
     */
    @Column(unique = true, length = 11)
    private String ruc;

    /**
     * Nombre de la persona de contacto dentro del proveedor.
     * Ejemplo: "Carlos Mendoza", "María López".
     */
    private String contacto;

    /**
     * Número de teléfono del proveedor o contacto.
     */
    private String telefono;

    /**
     * Correo electrónico del proveedor.
     *
     * @Column(unique = true): Garantiza unicidad de email en toda la tabla.
     */
    @Column(unique = true)
    private String email;

    /**
     * Dirección física del proveedor o del punto de entrega.
     */
    private String direccion;

    /**
     * Categoría del proveedor para clasificación.
     * Ejemplos: "CARNES", "VERDURAS", "BEBIDAS", "LÁCTEOS", "INSUMOS".
     *
     * @Column(length = 20): Limita el tamaño del texto.
     */
    @Column(length = 20)
    private String categoria;

    /**
     * Notas adicionales sobre el proveedor.
     * Puede incluir condiciones de pago, horarios de entrega, observaciones, etc.
     *
     * @Column(columnDefinition = "TEXT"): Permite almacenar texto de longitud extensa.
     */
    @Column(columnDefinition = "TEXT")
    private String notas;

    // ============================================================
    // CAMPOS ADMINISTRATIVOS
    // ============================================================

    /**
     * Indica si el proveedor está activo en el sistema.
     *
     * true  = proveedor vigente y disponible para nuevas compras.
     * false = proveedor inactivo (soft delete), preserva historial de facturas.
     *
     * @Column(nullable = false): Campo obligatorio.
     * @Builder.Default: Valor por defecto true al crear un nuevo proveedor.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
