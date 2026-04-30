package com.restaurante.pos.config;

import com.restaurante.pos.usuario.entity.Rol;
import com.restaurante.pos.usuario.entity.Usuario;
import com.restaurante.pos.usuario.repository.RolRepository;
import com.restaurante.pos.usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * INICIALIZADOR DE DATOS
 * ======================
 *
 * Esta clase inserta datos iniciales cuando la aplicación inicia.
 * Es muy útil para desarrollo y pruebas.
 *
 * CommandLineRunner:
 * - Es una interfaz que se ejecuta automáticamente al iniciar la app
 * - Se ejecuta DESPUÉS de que Spring cargue todos los beans
 * - Se ejecuta una sola vez al inicio
 *
 * DATOS QUE CREA:
 * - 3 Roles: ADMIN, VENDEDOR, AUDITOR
 * - 1 Usuario administrador por defecto
 * - 1 Usuario vendedor de prueba
 */
@Configuration  // Indica que esta clase contiene configuración de Spring
public class DataInitializer {

    /**
     * Bean que inicializa la base de datos con datos básicos.
     *
     * @param rolRepository Repositorio para guardar roles
     * @param usuarioRepository Repositorio para guardar usuarios
     * @param passwordEncoder Encriptador de contraseñas (BCrypt)
     * @return CommandLineRunner que se ejecuta al inicio
     */
    @Bean  // Marca este método como un bean de Spring
    CommandLineRunner initDatabase(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        // CommandLineRunner es una interfaz funcional que se ejecuta al inicio
        return args -> {
            // ==========================================================================
            // INICIO DEL PROCESO DE INICIALIZACIÓN
            // ==========================================================================

            System.out.println(">>> Inicializando datos...");

            // ==========================================================================
            // CREAR ROLES SI NO EXISTEN
            // ==========================================================================
            // Verificamos si cada rol existe antes de crearlo
            // Esto evita duplicados al reiniciar la aplicación

            // -------------------------------------------------------------------------
            // Rol ADMINISTRADOR
            // -------------------------------------------------------------------------
            // El rol ADMIN tiene acceso total al sistema
            if (rolRepository.findByNombre("ADMIN").isEmpty()) {
                Rol admin = new Rol();
                admin.setNombre("ADMIN");
                admin.setDescripcion("Administrador del sistema con acceso total");
                rolRepository.save(admin);  // Guarda en la tabla 'rol'
                System.out.println(">>> Rol ADMIN creado");
            }

            // -------------------------------------------------------------------------
            // Rol VENDEDOR
            // -------------------------------------------------------------------------
            // El rol VENDEDOR puede realizar ventas y consultar productos
            if (rolRepository.findByNombre("VENDEDOR").isEmpty()) {
                Rol vendedor = new Rol();
                vendedor.setNombre("VENDEDOR");
                vendedor.setDescripcion("Vendedor con acceso a ventas y consultas");
                rolRepository.save(vendedor);
                System.out.println(">>> Rol VENDEDOR creado");
            }

            // -------------------------------------------------------------------------
            // Rol AUDITOR
            // -------------------------------------------------------------------------
            // El rol AUDITOR solo puede consultar y generar reportes
            if (rolRepository.findByNombre("AUDITOR").isEmpty()) {
                Rol auditor = new Rol();
                auditor.setNombre("AUDITOR");
                auditor.setDescripcion("Auditor con acceso a reportes y consultas");
                rolRepository.save(auditor);
                System.out.println(">>> Rol AUDITOR creado");
            }

            // ==========================================================================
            // CREAR USUARIOS SI NO EXISTEN
            // ==========================================================================

            // -------------------------------------------------------------------------
            // Usuario ADMIN por defecto
            // -------------------------------------------------------------------------
            // Credenciales: admin / admin123
            // IMPORTANTE: Cambiar en producción
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                // Buscamos el rol ADMIN creado anteriormente
                Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

                Usuario admin = new Usuario();
                admin.setUsername("admin");
                // Encriptamos la contraseña con BCrypt antes de guardar
                // NUNCA guardar contraseñas en texto plano
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNombre("Administrador");
                admin.setApellido("Sistema");
                admin.setEmail("admin@restaurante.com");
                admin.setRol(rolAdmin);  // Asignamos el rol ADMIN
                usuarioRepository.save(admin);  // Guarda en la tabla 'usuario'
                System.out.println(">>> Usuario admin creado (password: admin123)");
            }

            // -------------------------------------------------------------------------
            // Usuario VENDEDOR de prueba
            // -------------------------------------------------------------------------
            // Credenciales: vendedor / vendedor123
            // Útil para probar funcionalidades de venta
            if (usuarioRepository.findByUsername("vendedor").isEmpty()) {
                Rol rolVendedor = rolRepository.findByNombre("VENDEDOR")
                        .orElseThrow(() -> new RuntimeException("Rol VENDEDOR no encontrado"));

                Usuario vendedor = new Usuario();
                vendedor.setUsername("vendedor");
                vendedor.setPassword(passwordEncoder.encode("vendedor123"));
                vendedor.setNombre("Juan");
                vendedor.setApellido("Pérez");
                vendedor.setEmail("vendedor@restaurante.com");
                vendedor.setRol(rolVendedor);  // Asignamos el rol VENDEDOR
                usuarioRepository.save(vendedor);
                System.out.println(">>> Usuario vendedor creado (password: vendedor123)");
            }

            // ==========================================================================
            // FIN DE LA INICIALIZACIÓN
            // ==========================================================================

            System.out.println(">>> Datos iniciales cargados correctamente");
        };
    }
}