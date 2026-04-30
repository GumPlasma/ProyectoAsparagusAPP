package com.restaurante.pos.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * CLASE BASE PARA TODAS LAS ENTIDADES
 * ===================================
 *
 * Esta clase abstracta proporciona campos comunes a todas las entidades del sistema.
 * Usamos herencia para no repetir código (principio DRY - Don't Repeat Yourself).
 *
 * HERENCIA EN JPA:
 * - @MappedSuperclass: Indica que esta clase no es una entidad, pero sus campos
 *   se heredarán a las entidades hijas. No crea una tabla para esta clase.
 *
 * CAMPOS COMUNES:
 * - id: Identificador único de cada registro
 * - fechaCreacion: Cuándo se creó el registro
 * - fechaActualizacion: Cuándo se modificó por última vez
 * - activo: Para "soft delete" (borrado lógico, no elimina físicamente)
 */
@MappedSuperclass  // Esta clase no crea tabla, solo hereda campos
@Getter            // Lombok genera getters automáticamente
@Setter            // Lombok genera setters automáticamente
public abstract class BaseEntity {

    /**
     * ID ÚNICO
     * ========
     * - @Id: Indica que es la clave primaria
     * - @GeneratedValue: Se genera automáticamente
     * - GenerationType.IDENTITY: Usa el auto-incremento de la base de datos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FECHA DE CREACIÓN
     * =================
     * - @Column: Define propiedades de la columna
     * - updatable = false: No se puede modificar después de crearse
     * - @PrePersist: Se ejecuta ANTES de insertar el registro
     */
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * FECHA DE ACTUALIZACIÓN
     * ======================
     * - @PreUpdate: Se ejecuta ANTES de actualizar el registro
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * SOFT DELETE (BORRADO LÓGICO)
     * ============================
     * En lugar de eliminar físicamente el registro, marcamos activo=false.
     * Esto permite recuperar datos si se borran por error.
     *
     * @SQLDelete: Modifica el DELETE para que haga UPDATE en su lugar
     * @Where: Filtra automáticamente para mostrar solo activos
     */
    @Column(name = "activo")
    private Boolean activo = true;

    // ==========================================
    // MÉTODOS DE CICLO DE VIDA JPA
    // ==========================================

    /**
     * Se ejecuta antes de insertar (INSERT) un nuevo registro.
     * Establece la fecha de creación y activo=true.
     */
    @PrePersist
    protected void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }

    /**
     * Se ejecuta antes de actualizar (UPDATE) un registro existente.
     * Actualiza la fecha de modificación.
     */
    @PreUpdate
    protected void alActualizar() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Método para realizar borrado lógico
     * En lugar de eliminar, marca como inactivo.
     */
    public void eliminar() {
        this.activo = false;
    }

    /**
     * Método para restaurar un registro eliminado lógicamente.
     */
    public void restaurar() {
        this.activo = true;
    }
}