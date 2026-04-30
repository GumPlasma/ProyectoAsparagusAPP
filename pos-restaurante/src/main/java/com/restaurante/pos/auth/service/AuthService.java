package com.restaurante.pos.auth.service;

import com.restaurante.pos.auth.dto.LoginRequest;
import com.restaurante.pos.auth.dto.LoginResponse;
import com.restaurante.pos.usuario.entity.Usuario;
import com.restaurante.pos.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * SERVICIO DE AUTENTICACIÓN
 * =========================
 *
 * CAPA: Service (Lógica de Negocio)
 * RESPONSABILIDAD: Contener toda la lógica de autenticación y validación de credenciales.
 *
 * ¿QUÉ HACE?
 * - Busca al usuario en la base de datos por su username/email.
 * - Verifica que el usuario exista y esté activo.
 * - Compara la contraseña proporcionada con el hash almacenado usando BCrypt.
 * - Construye y retorna una respuesta estructurada con el resultado del login.
 *
 * SEGURIDAD:
 * - NUNCA almacena ni compara contraseñas en texto plano.
 * - Usa PasswordEncoder.matches() para comparar de forma segura.
 * - No revela si el error fue usuario inexistente o contraseña incorrecta de forma explícita,
 *   aunque en este caso sí se distingue para facilitar la experiencia de usuario.
 *
 * ANOTACIONES SPRING:
 * - @Service: Marca esta clase como un componente de servicio gestionado por Spring.
 */
@Service
public class AuthService {

    // ============================================================
    // DEPENDENCIAS
    // ============================================================

    /**
     * Repositorio de usuarios para buscar credenciales en la base de datos.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Codificador de contraseñas (BCrypt) para comparar hashes de forma segura.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioRepository Repositorio de acceso a datos de usuarios.
     * @param passwordEncoder   Codificador BCrypt para verificación de contraseñas.
     */
    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    // LÓGICA DE AUTENTICACIÓN
    // ============================================================

    /**
     * PROCESAR LOGIN
     * --------------
     * Valida las credenciales de un usuario y retorna el resultado.
     *
     * FLUJO DE VALIDACIÓN:
     * 1. Busca el usuario por username (email).
     * 2. Si no existe → retorna error "Usuario no encontrado".
     * 3. Si el usuario está inactivo → retorna error "Usuario inactivo".
     * 4. Compara la contraseña con el hash BCrypt almacenado.
     * 5. Si no coincide → retorna error "Contraseña incorrecta".
     * 6. Si todo es correcto → retorna éxito con datos del usuario.
     *
     * @param request DTO con email/username y contraseña en texto plano.
     * @return LoginResponse indicando éxito/fracaso y datos del usuario o mensaje de error.
     */
    public LoginResponse login(LoginRequest request) {
        // ---------- PASO 1: Buscar usuario por username ----------
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getEmail());

        // ---------- PASO 2: Verificar existencia ----------
        if (usuarioOpt.isEmpty()) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Usuario no encontrado")
                    .build();
        }

        Usuario usuario = usuarioOpt.get();

        // ---------- PASO 3: Verificar que el usuario esté activo ----------
        // Los usuarios inactivos no pueden iniciar sesión.
        if (!usuario.getActivo()) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Usuario inactivo. Contacte al administrador.")
                    .build();
        }

        // ---------- PASO 4: Verificar contraseña ----------
        // passwordEncoder.matches() compara el texto plano con el hash BCrypt de forma segura.
        // BCrypt maneja automáticamente el salt, por lo que la comparación es segura contra timing attacks.
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Contraseña incorrecta")
                    .build();
        }

        // ---------- PASO 5: Login exitoso ----------
        // Obtiene el nombre del rol para incluirlo en la respuesta.
        // Si por algún motivo el rol es nulo, usa "SIN_ROL" como valor por defecto.
        String rolNombre = "SIN_ROL";
        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            rolNombre = usuario.getRol().getNombre();
        }

        // Construye la respuesta de éxito usando el patrón Builder.
        return LoginResponse.builder()
                .success(true)
                .message("Login exitoso")
                .id(usuario.getId())
                .nombre(usuario.getNombreCompleto())
                .email(usuario.getEmail() != null ? usuario.getEmail() : usuario.getUsername())
                .rol(rolNombre)
                .build();
    }
}
