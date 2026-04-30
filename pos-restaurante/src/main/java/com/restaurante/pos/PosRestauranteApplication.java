package com.restaurante.pos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CLASE PRINCIPAL DE LA APLICACIÓN
 * ================================

 * Esta es la clase de entrada (entry point) de la aplicación Spring Boot.

 * ANOTACIONES:
 * - @SpringBootApplication: Combina 3 anotaciones:
 *   1. @Configuration: Indica que es una clase de configuración
 *   2. @EnableAutoConfiguration: Configura automáticamente Spring
 *   3. @ComponentScan: Escanea componentes en este paquete y subpaquetes

 * AL EJECUTAR:
 * - Se inicia el servidor embebido Tomcat
 * - Se cargan todas las configuraciones
 * - Se conecta a la base de datos
 * - Se crean las tablas automáticamente
 */
@SpringBootApplication
public class PosRestauranteApplication {

	public static void main(String[] args) {
		// SpringApplication.run() hace lo siguiente:
		// 1. Crea el contexto de aplicación Spring
		// 2. Detecta y carga la configuración
		// 3. Inicia el servidor web embebido
		// 4. Ejecuta cualquier CommandLineRunner o ApplicationRunner
		SpringApplication.run(PosRestauranteApplication.class, args);

		System.out.println("\n========================================");
		System.out.println("  🚀 Sistema POS Restaurante INICIADO");
		System.out.println("  📖 Swagger UI: http://localhost:8080/api/swagger-ui.html");
		System.out.println("  🗄️  H2 Console: http://localhost:8080/api/h2-console");
		System.out.println("========================================\n");
	}

}