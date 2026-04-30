#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generador de PDF - Preguntas y Respuestas para Estudiar
POS Restaurante - Backend Spring Boot
"""

import os
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import cm
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle,
    PageBreak
)
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

# ============================================================
# CONFIGURACIÓN DE FUENTES
# ============================================================
FONT_PATHS = [
    r"C:\Windows\Fonts\arial.ttf",
    r"C:\Windows\Fonts\calibri.ttf",
    r"C:\Windows\Fonts\segoeui.ttf",
]

font_registered = False
for fp in FONT_PATHS:
    if os.path.exists(fp):
        font_name = os.path.basename(fp).replace(".ttf", "")
        try:
            pdfmetrics.registerFont(TTFont(font_name, fp))
            bold_fp = fp.replace(".ttf", "bd.ttf")
            if not os.path.exists(bold_fp):
                bold_fp = fp
            pdfmetrics.registerFont(TTFont(f"{font_name}-Bold", bold_fp))
            DEFAULT_FONT = font_name
            font_registered = True
            break
        except Exception:
            continue

if not font_registered:
    DEFAULT_FONT = "Helvetica"

BOLD_FONT = f"{DEFAULT_FONT}-Bold" if font_registered else "Helvetica-Bold"

# ============================================================
# ESTILOS
# ============================================================
style_title = ParagraphStyle(
    'CustomTitle',
    fontName=BOLD_FONT,
    fontSize=26,
    textColor=colors.HexColor('#1a5276'),
    spaceAfter=30,
    alignment=1,
)

style_h1 = ParagraphStyle(
    'CustomH1',
    fontName=BOLD_FONT,
    fontSize=18,
    textColor=colors.HexColor('#1a5276'),
    spaceAfter=14,
    spaceBefore=20,
)

style_h2 = ParagraphStyle(
    'CustomH2',
    fontName=BOLD_FONT,
    fontSize=14,
    textColor=colors.HexColor('#2874a6'),
    spaceAfter=10,
    spaceBefore=14,
)

style_h3 = ParagraphStyle(
    'CustomH3',
    fontName=BOLD_FONT,
    fontSize=12,
    textColor=colors.HexColor('#2e86c1'),
    spaceAfter=8,
    spaceBefore=10,
)

style_body = ParagraphStyle(
    'CustomBody',
    fontName=DEFAULT_FONT,
    fontSize=10,
    leading=14,
    spaceAfter=8,
)

style_question = ParagraphStyle(
    'QuestionStyle',
    fontName=BOLD_FONT,
    fontSize=11,
    textColor=colors.HexColor('#1a5276'),
    leading=16,
    spaceAfter=6,
    spaceBefore=14,
    leftIndent=0,
    borderWidth=0,
    borderColor=colors.HexColor('#aed6f1'),
    borderPadding=6,
    backColor=colors.HexColor('#eaf2f8'),
)

style_answer = ParagraphStyle(
    'AnswerStyle',
    fontName=DEFAULT_FONT,
    fontSize=10,
    textColor=colors.HexColor('#1c2833'),
    leading=14,
    spaceAfter=10,
    leftIndent=15,
    rightIndent=10,
)

style_code = ParagraphStyle(
    'CodeStyle',
    fontName=DEFAULT_FONT,
    fontSize=9,
    textColor=colors.HexColor('#1c2833'),
    backColor=colors.HexColor('#f4f6f7'),
    leftIndent=20,
    rightIndent=10,
    spaceAfter=8,
    leading=12,
)

style_tip = ParagraphStyle(
    'TipStyle',
    fontName=DEFAULT_FONT,
    fontSize=10,
    textColor=colors.HexColor('#1c2833'),
    backColor=colors.HexColor('#e8f8f5'),
    leftIndent=10,
    rightIndent=10,
    spaceAfter=10,
    leading=14,
    borderWidth=1,
    borderColor=colors.HexColor('#a9dfbf'),
    borderPadding=8,
)

# ============================================================
# FUNCIONES AUXILIARES
# ============================================================
def P(text, style=style_body):
    return Paragraph(text, style)


def H1(text):
    return Paragraph(text, style_h1)


def H2(text):
    return Paragraph(text, style_h2)


def H3(text):
    return Paragraph(text, style_h3)


def QUESTION(text):
    return Paragraph(f"❓ <b>PREGUNTA:</b> {text}", style_question)


def ANSWER(text):
    return Paragraph(f"✅ <b>RESPUESTA:</b> {text}", style_answer)


def CODE(text):
    return Paragraph(text, style_code)


def TIP(text):
    return Paragraph(f"💡 <b>TIP:</b> {text}", style_tip)


def spacer(height=0.3 * cm):
    return Spacer(1, height)


def page_break():
    return PageBreak()


def qa_block(question, answer, tip=None, code=None):
    """Crea un bloque completo de pregunta-respuesta opcional con tip y code."""
    items = [QUESTION(question), ANSWER(answer)]
    if code:
        items.append(CODE(code))
    if tip:
        items.append(TIP(tip))
    items.append(spacer(0.2 * cm))
    return items


# ============================================================
# CONTENIDO
# ============================================================
story = []

# ---------- PORTADA ----------
story.append(Spacer(1, 4 * cm))
story.append(P("<b>BANCO DE PREGUNTAS Y RESPUESTAS</b>", style_title))
story.append(spacer(0.5 * cm))
story.append(P("<b>POS Restaurante - Spring Boot</b>", ParagraphStyle('Sub', parent=style_title, fontSize=18, textColor=colors.HexColor('#5d6d7e'))))
story.append(spacer(1.5 * cm))
story.append(P("Documento de Estudio para Exposición Oral", ParagraphStyle('Sub2', parent=style_title, fontSize=14, textColor=colors.HexColor('#7f8c8d'))))
story.append(Spacer(1, 3 * cm))
story.append(TIP("Este documento complementa la Guía de Exposición. Úsalo para practicar respuestas orales antes de tu presentación. Intenta responder cada pregunta en voz alta antes de leer la respuesta."))
story.append(page_break())

# ---------- PARTE 1: CONCEPTOS BÁSICOS ----------
story.append(H1("PARTE 1: Conceptos Básicos de Spring Boot"))
story.append(P("Preguntas fundamentales que cualquier jurado puede hacer para evaluar tu comprensión del framework."))
story.append(spacer())

story.extend(qa_block(
    "¿Qué es Spring Boot y por qué lo usaron en este proyecto?",
    "Spring Boot es un framework de Java que simplifica la creación de aplicaciones empresariales basadas en Spring. \
Lo usamos porque <b>elimina la configuración manual compleja</b> (auto-configuración), incluye un servidor Tomcat embebido \
(no necesitamos desplegar en servidor externo), y tiene un ecosistema de starters que agregan dependencias comunes con una sola línea en el pom.xml. \
Además, integra fácilmente JPA, Security y Swagger.",
    "Menciona que con Spring Boot solo necesitas la anotación @SpringBootApplication y un main() para levantar toda la aplicación.",
    "@SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan"
))

story.extend(qa_block(
    "¿Qué significa que sea una API REST?",
    "REST (Representational State Transfer) es un estilo arquitectónico donde el cliente y servidor se comunican mediante \
<b>peticiones HTTP</b> (GET, POST, PUT, DELETE) intercambiando datos en formato JSON. \
Nuestra API es REST porque cada URL representa un recurso (/productos, /ventas, /usuarios) y usamos los métodos HTTP \
para operar sobre esos recursos. No mantenemos estado del cliente en el servidor (stateless).",
    "Menciona que el frontend puede ser cualquier tecnología (React, Angular, móvil) porque solo consume JSON.",
))

story.extend(qa_block(
    "¿Qué es la inyección de dependencias y cómo se usa en el proyecto?",
    "Es un patrón de diseño donde Spring crea y administra los objetos (beans) y los \
<b>inyecta automáticamente</b> donde se necesitan. En nuestro proyecto usamos \
<b>inyección por constructor</b>: declaramos los campos como 'final' y usamos @RequiredArgsConstructor de Lombok \
para generar el constructor. Spring detecta ese constructor y le pasa las dependencias automáticamente. \
Por ejemplo, UsuarioController recibe UsuarioService sin que nosotros hagamos 'new UsuarioService()'.",
    "La inyección por constructor es la mejor práctica porque permite crear mocks para testing y garantiza \
que las dependencias no sean nulas.",
    "private final UsuarioService usuarioService;  // Spring inyecta esto automáticamente"
))

story.extend(qa_block(
    "¿Qué es Maven y para qué sirve el pom.xml?",
    "Maven es una herramienta de gestión de proyectos Java. El archivo pom.xml (Project Object Model) define \
<b>dependencias</b> (qué bibliotecas usa el proyecto), <b>plugins</b> (cómo compilar y empaquetar), \
y <b>propiedades</b> (versión de Java, etc.). En nuestro pom.xml declaramos starters de Spring Boot como \
spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-security, etc. \
Maven descarga automáticamente estas bibliotecas de internet y las agrega al classpath.",
    "Menciona que el parent spring-boot-starter-parent define versiones compatibles de todas las dependencias."
))

story.extend(qa_block(
    "¿Qué es Lombok y qué problemas resuelve?",
    "Lombok es una biblioteca que genera código repetitivo en tiempo de compilación mediante anotaciones. \
En nuestro proyecto usamos:<br/>• <b>@Data</b> → getters, setters, toString, equals, hashCode<br/>• <b>@Builder</b> → patrón Builder<br/>• <b>@NoArgsConstructor / @AllArgsConstructor</b> → constructores<br/>• <b>@RequiredArgsConstructor</b> → constructor con campos final<br/>Esto reduce drásticamente el código boilerplate y hace las clases más legibles.",
    "Sin Lombok, una clase Entity con 10 campos tendría ~150 líneas solo de getters y setters. Con Lombok son 10 líneas.",
))

story.append(page_break())

# ---------- PARTE 2: ARQUITECTURA ----------
story.append(H1("PARTE 2: Arquitectura y Patrones de Diseño"))
story.append(spacer())

story.extend(qa_block(
    "Explícame el diagrama de capas de tu proyecto.",
    "Nuestro proyecto tiene 4 capas principales:<br/><br/>\
<b>1. Controller (Presentación):</b> Expone los endpoints REST. Recibe HTTP, extrae parámetros, \
valida con @Valid, y delega al Service. No tiene lógica de negocio.<br/><br/>\
<b>2. Service (Negocio):</b> Contiene las reglas de negocio. Valida datos, coordina repositories, \
aplica transacciones @Transactional, y convierte Entity ↔ DTO.<br/><br/>\
<b>3. Repository (Datos):</b> Interfaz que accede a la BD mediante Spring Data JPA. \
Define consultas por nombre (findByXxx) o JPQL (@Query). No tiene lógica de negocio.<br/><br/>\
<b>4. Entity (Modelo):</b> Clases JPA que mapean tablas de la base de datos. \
Definen columnas, relaciones y restricciones.",
    "Dibuja esto en el pizarrón durante la exposición. Es tu carta de triunfo.",
))

story.extend(qa_block(
    "¿Qué es un DTO y por qué no usan la Entity directamente en la API?",
    "DTO (Data Transfer Object) es un objeto plano que solo transporta datos entre capas. \
No usamos la Entity directamente por tres razones:<br/><br/>\
<b>1. Seguridad:</b> La Entity puede tener campos sensibles (password, relaciones internas) que no queremos exponer.<br/>\
<b>2. Desacoplamiento:</b> Si cambia la estructura de la BD, no rompemos la API del frontend.<br/>\
<b>3. Validación:</b> Los DTOs de entrada tienen @NotBlank, @Email, etc., mientras que las Entities no deberían tener validaciones de API.<br/><br/>\
Ejemplo: CrearUsuarioDTO valida que password tenga mínimo 6 caracteres, pero la Entity Usuario solo mapea la columna.",
    "Menciona que tenemos DTOs separados para entrada (CrearXxxDTO), salida (XxxResponseDTO) y filtros (FiltroXxxDTO).",
))

story.extend(qa_block(
    "¿Qué es el patrón Builder y dónde lo usan?",
    "El patrón Builder permite construir objetos complejos paso a paso, encadenando métodos. \
Lo usamos en <b>LoginResponse</b> y en las entidades con <b>@Builder</b> de Lombok. \
Por ejemplo, en AuthService al construir la respuesta de login:<br/><br/>\
return LoginResponse.builder()<br/>\
&nbsp;&nbsp;&nbsp;&nbsp;.success(true)<br/>\
&nbsp;&nbsp;&nbsp;&nbsp;.message('Login exitoso')<br/>\
&nbsp;&nbsp;&nbsp;&nbsp;.nombre(usuario.getNombreCompleto())<br/>\
&nbsp;&nbsp;&nbsp;&nbsp;.rol(rolNombre)<br/>\
&nbsp;&nbsp;&nbsp;&nbsp;.build();<br/><br/>\
Esto evita constructores con muchos parámetros y hace el código más legible.",
    "El Builder es especialmente útil cuando hay muchos campos opcionales.",
))

story.extend(qa_block(
    "¿Qué es el Soft Delete y cómo lo implementaron?",
    "Soft Delete (borrado lógico) consiste en <b>marcar un registro como inactivo</b> en vez de eliminarlo físicamente de la BD. \
Lo implementamos mediante la clase abstracta <b>BaseEntity</b>, que tiene un campo <b>activo = true</b> por defecto. \
Todas las entidades heredan de BaseEntity. Cuando 'eliminamos' un usuario, cliente o producto, \
llamamos a .eliminar() que pone activo = false. Las consultas del repository filtran solo los activos \
(findByActivoTrue). Esto preserva el historial y mantiene la integridad referencial.",
    "Compara con DELETE físico: si borras un cliente que tiene ventas, pierdes quién hizo esas ventas. Con soft delete no.",
))

story.extend(qa_block(
    "¿Qué es @Transactional y para qué sirve?",
    "Es una anotación de Spring que garantiza que un método se ejecute dentro de una <b>transacción de base de datos</b>. \
Si dentro del método ocurre cualquier error, se hace <b>rollback</b> automático: todas las operaciones de BD se deshacen \
como si nunca hubieran pasado. En nuestro proyecto la usamos en todos los métodos que modifican datos \
(crear, actualizar, eliminar) del Service. También usamos @Transactional(readOnly=true) en consultas \
para optimizar rendimiento porque indica a JPA que no hará seguimiento de cambios.",
    "Ejemplo práctico: si al crear una venta falla el último detalle, sin @Transactional tendrías una venta incompleta en la BD. Con @Transactional, se deshace TODO.",
))

story.append(page_break())

# ---------- PARTE 3: JPA Y BASE DE DATOS ----------
story.append(H1("PARTE 3: JPA, Hibernate y Base de Datos"))
story.append(spacer())

story.extend(qa_block(
    "¿Qué es JPA y qué relación tiene con Hibernate?",
    "JPA (Java Persistence API) es una <b>especificación</b> estándar de Java para mapear objetos a tablas de base de datos (ORM). \
Hibernate es una <b>implementación</b> de esa especificación. Spring Data JPA usa Hibernate internamente. \
Nosotros usamos anotaciones JPA (@Entity, @Table, @Column, @Id, @GeneratedValue, @OneToMany, etc.) \
y Spring Data JPA se encarga de traducir eso a SQL mediante Hibernate.",
    "Puedes decir que JPA es como una interfaz y Hibernate es la clase que la implementa.",
))

story.extend(qa_block(
    "¿Qué significa @Entity, @Table y @Column?",
    "<b>@Entity:</b> Le dice a JPA que esta clase debe mapearse a una tabla de la base de datos.<br/>\
<b>@Table(name='usuarios'):</b> Especifica el nombre exacto de la tabla. Si se omite, usa el nombre de la clase.<br/>\
<b>@Column:</b> Define propiedades de la columna: nombre, nullable, unique, length, precision, scale. \
Por ejemplo, @Column(nullable=false, unique=true, length=50) significa que la columna es obligatoria, \
no se repite y máximo 50 caracteres.",
    "Menciona que @Column(updatable=false) en fechaCreacion evita que se modifique después de insertar.",
))

story.extend(qa_block(
    "¿Cuál es la diferencia entre @OneToOne, @OneToMany y @ManyToOne?",
    "<b>@OneToOne:</b> Una instancia de A está relacionada con exactamente una instancia de B. \
Ejemplo: Producto (1) ↔ Inventario (1). Cada producto tiene exactamente un registro de inventario.<br/><br/>\
<b>@ManyToOne:</b> Muchas instancias de A pertenecen a una instancia de B. \
Ejemplo: Muchos Usuarios tienen el mismo Rol. La FK va en la tabla de A.<br/><br/>\
<b>@OneToMany:</b> Una instancia de A tiene muchas instancias de B. \
Ejemplo: Una FacturaProveedor tiene muchos DetalleFactura. Se usa mappedBy en el lado 'uno' \
y la FK está en el lado 'muchos'.",
    "Siempre hay que definir quién es el 'dueño' de la relación (quién tiene la FK).",
))

story.extend(qa_block(
    "¿Qué es FetchType.EAGER y FetchType.LAZY?",
    "Son estrategias de carga de relaciones en JPA:<br/><br/>\
<b>EAGER (ansioso):</b> Carga la relación automáticamente al consultar la entidad principal. \
Ejemplo: En Inventario usamos FetchType.EAGER con Producto porque siempre necesitamos saber el nombre del producto.<br/><br/>\
<b>LAZY (perezoso):</b> NO carga la relación hasta que se accede explícitamente a ella. \
Ejemplo: En FacturaProveedor usamos FetchType.LAZY con detalles porque una factura puede tener 50 productos \
y no siempre necesitamos todos al consultar la cabecera.<br/><br/>\
LAZY es más eficiente en memoria y rendimiento. EAGER es más cómodo pero puede causar problemas de rendimiento si se abusa.",
    "El problema de abusar de EAGER se llama 'N+1 query problem'. Menciona que lo evitas usando LAZY + JOIN FETCH cuando sea necesario.",
))

story.extend(qa_block(
    "¿Qué es GenerationType.IDENTITY?",
    "Es una estrategia de generación de claves primarias donde la <b>base de datos</b> se encarga de generar el valor automáticamente, \
típicamente mediante AUTO_INCREMENT (MySQL) o IDENTITY (SQL Server). \
Es la estrategia más simple y eficiente para bases de datos relacionales. \
En nuestro proyecto, todas las entidades usan @GeneratedValue(strategy = GenerationType.IDENTITY).",
    "Existen otras estrategias como SEQUENCE (usa secuencias de BD) y TABLE (usa una tabla especial), pero IDENTITY es la más común.",
))

story.extend(qa_block(
    "¿Qué son los Query Methods de Spring Data JPA?",
    "Son métodos de interfaz Repository cuyo nombre sigue una convención y Spring <b>genera automáticamente</b> la consulta SQL. \
No escribimos JPQL ni SQL manualmente. Ejemplos de nuestro proyecto:<br/><br/>\
• findByUsername(String username) → SELECT * FROM usuario WHERE username = ?<br/>\
• findByActivoTrue() → SELECT * FROM cliente WHERE activo = true<br/>\
• existsByEmail(String email) → verifica si existe<br/>\
• findByNombreContainingIgnoreCase(String nombre) → búsqueda parcial sin distinguir mayúsculas<br/><br/>\
Para consultas más complejas usamos @Query con JPQL.",
    "La magia está en el nombre del método. Spring Data lo parsea y genera la query.",
))

story.append(page_break())

# ---------- PARTE 4: SEGURIDAD ----------
story.append(H1("PARTE 4: Seguridad y Autenticación"))
story.append(spacer())

story.extend(qa_block(
    "¿Cómo se almacenan las contraseñas en el sistema?",
    "Las contraseñas <b>NUNCA</b> se almacenan en texto plano. Usamos <b>BCrypt</b>, un algoritmo de hashing adaptativo. \
El proceso es:<br/><br/>\
1. Al crear usuario: passwordEncoder.encode('admin123') → genera un hash de 60 caracteres<br/>\
2. Se guarda ese hash en la columna password de la BD<br/>\
3. Al login: passwordEncoder.matches('admin123', hashAlmacenado) → true/false<br/><br/>\
BCrypt incluye un salt único automáticamente, por lo que dos usuarios con la misma contraseña tendrán hashes diferentes.",
    "Nunca implementes tu propio algoritmo de hashing. BCrypt es el estándar de la industria.",
    "Hash de BCrypt ejemplo: $2a$10$N9qo8uLOickgx2ZMRZoMy.Mqr..."
))

story.extend(qa_block(
    "¿Qué es BCryptPasswordEncoder?",
    "Es una implementación de PasswordEncoder de Spring Security que usa el algoritmo BCrypt. \
Está configurado como un @Bean en SecurityConfig. \
Lo inyectamos en AuthService para verificar credenciales y en UsuarioService para hashear contraseñas nuevas. \
Internamente usa la librería jBCrypt que aplica el algoritmo de hashing Blowfish con un costo configurable (por defecto 10).",
    "El 'costo' determina cuántas iteraciones hace. Valores más altos = más seguro pero más lento.",
))

story.extend(qa_block(
    "¿Qué hace la clase SecurityConfig?",
    "Configura la cadena de filtros de seguridad de Spring. En nuestro proyecto define tres cosas:<br/><br/>\
<b>1. SecurityFilterChain:</b> Desactiva CSRF (no es necesario en APIs REST stateless), configura CORS, \
y permite todas las peticiones (modo desarrollo).<br/>\
<b>2. CorsConfigurationSource:</b> Define qué orígenes, métodos y headers están permitidos en peticiones cross-origin.<br/>\
<b>3. PasswordEncoder:</b> Expone BCryptPasswordEncoder como bean para inyección.<br/><br/>\
En producción se debería restringir el acceso por roles y agregar autenticación JWT.",
    "Menciona que actualmente está en modo 'permissive' para facilitar el desarrollo del frontend.",
))

story.extend(qa_block(
    "¿Qué es CORS y por qué lo necesitan?",
    "CORS (Cross-Origin Resource Sharing) es un mecanismo de seguridad de los navegadores que \
<b>bloquea peticiones entre dominios diferentes</b> por defecto. \
Nuestro backend corre en localhost:8080 y el frontend en localhost:5173 (Vite). \
Sin CORS, el navegador bloquearía las peticiones del frontend. \
En CorsConfig permitimos el origen del frontend, los métodos HTTP necesarios y los headers requeridos.",
    "Nunca uses allowedOrigins('*') con allowCredentials(true) en producción. Especifica siempre el dominio exacto.",
))

story.append(page_break())

# ---------- PARTE 5: MÓDULOS ESPECÍFICOS ----------
story.append(H1("PARTE 5: Preguntas por Módulo del Sistema"))
story.append(P("Preguntas específicas sobre la lógica de negocio de cada módulo."))
story.append(spacer())

story.append(H2("5.1 Inventario"))
story.extend(qa_block(
    "¿Cómo se calcula el precio promedio ponderado del inventario?",
    "Cuando llega una compra, recalculamos el precio promedio ponderado de ese producto:<br/><br/>\
<b>Fórmula:</b> nuevoPrecio = (valorActual + valorNuevo) / nuevaCantidadTotal<br/><br/>\
Donde:<br/>\
• valorActual = precioPromedioActual × cantidadActual<br/>\
• valorNuevo = precioUnitarioCompra × cantidadComprada<br/>\
• nuevaCantidadTotal = cantidadActual + cantidadComprada<br/><br/>\
Esto da un precio de inventario contablemente correcto que refleja el costo histórico promedio.",
    "Este precio es diferente del precio de venta. El precio de venta lo define el administrador; el precio promedio es solo para valorización de stock.",
))

story.extend(qa_block(
    "¿Qué es el Kardex y para qué sirve?",
    "El Kardex es un <b>reporte histórico completo</b> de todos los movimientos de un producto en el inventario. \
Muestra:<br/>\
• Stock actual del producto<br/>\
• Todos los movimientos ordenados por fecha (entradas, salidas, ajustes)<br/>\
• Stock anterior y posterior de cada movimiento<br/>\
• Usuario que realizó el movimiento<br/>\
• Documento de referencia (factura de compra o venta)<br/><br/>\
Es una herramienta de auditoría que permite trazar qué pasó con cada producto.",
    "El término 'Kardex' viene de la contabilidad tradicional de inventarios.",
))

story.append(H2("5.2 Mesa y Venta"))
story.extend(qa_block(
    "¿Cómo se relacionan Mesa, PedidoMesa y Venta?",
    "<b>Mesa</b> representa una mesa física del restaurante. Tiene estado (LIBRE, OCUPADA, RESERVADA), \
capacidad, posición visual (X,Y) y totales.<br/><br/>\
<b>PedidoMesa</b> representa UN producto pedido en UNA mesa. Tiene producto, cantidad, precio unitario y subtotal. \
Una mesa OCUPADA tiene muchos PedidoMesa.<br/><br/>\
<b>Venta</b> se crea al procesar el pago de una mesa. Es el registro histórico permanente. \
Cuando se paga, los PedidoMesa se convierten en DetalleVenta, se crea la Venta, \
y la mesa vuelve a estado LIBRE.<br/><br/>\
La Venta permanece para siempre; los PedidoMesa se eliminan al cerrar la mesa.",
    "La Venta es como la 'fotografía' del momento del pago. Los precios se guardan históricamente.",
))

story.extend(qa_block(
    "¿Por qué el precio en DetalleVenta se guarda explícitamente?",
    "Porque el precio de un producto puede cambiar con el tiempo. \
Si solo guardáramos la referencia al producto, al consultar una venta antigua veríamos el precio actual, \
no el que realmente pagó el cliente en ese momento. \
Al guardar el precioUnitario en DetalleVenta, preservamos el <b>valor histórico</b> de la transacción. \
Esto es fundamental para contabilidad y reportes.",
    "Lo mismo aplica al subtotal: se calcula y guarda, no se recalcula dinámicamente.",
))

story.append(H2("5.3 Proveedor"))
story.extend(qa_block(
    "¿Cuál es la diferencia entre FacturaProveedor y Venta?",
    "<b>FacturaProveedor:</b> Es una <b>entrada</b> de dinero/productos. Compramos al proveedor y pagamos nosotros. \
Aumenta el inventario. Estados: PENDIENTE, PROCESADA, ANULADA.<br/><br/>\
<b>Venta:</b> Es una <b>salida</b> de productos. El cliente nos paga a nosotros. \
Disminuye el inventario. Estados: COMPLETADA, ANULADA.<br/><br/>\
Son flujos opuestos pero complementarios: las compras abastecen el inventario y las ventas lo desabastecen.",
    "Ambas generan movimientos de inventario pero de tipos opuestos: ENTRADA vs SALIDA.",
))

story.append(page_break())

# ---------- PARTE 6: VALIDACIONES Y EXCEPCIONES ----------
story.append(H1("PARTE 6: Validaciones y Manejo de Errores"))
story.append(spacer())

story.extend(qa_block(
    "¿Cómo funcionan las validaciones de Bean Validation?",
    "Usamos anotaciones del paquete jakarta.validation.constraints en los DTOs de entrada:<br/><br/>\
• <b>@NotBlank</b> → El string no puede ser null ni vacío ni espacios<br/>\
• <b>@NotNull</b> → El objeto no puede ser null<br/>\
• <b>@Size(min, max)</b> → Longitud del string<br/>\
• <b>@Email</b> → Formato de correo válido<br/>\
• <b>@Min(1)</b> → Valor numérico mínimo<br/>\
• <b>@DecimalMin('0.01')</b> → Valor decimal mínimo<br/><br/>\
En el Controller activamos estas validaciones con <b>@Valid</b> antes de @RequestBody. \
Si alguna validación falla, Spring lanza MethodArgumentNotValidException, \
que es capturada por GlobalExceptionHandler y retorna un ApiResponse con los errores por campo.",
    "Esto evita que datos incorrectos lleguen al Service y a la base de datos.",
))

story.extend(qa_block(
    "¿Qué pasa si se lanza una excepción en un método @Transactional?",
    "Si ocurre una excepción NO capturada dentro de un método @Transactional, \
Spring hace automáticamente <b>rollback</b>: deshace TODAS las operaciones de base de datos \
que se hicieron dentro de ese método. Esto garantiza la <b>consistencia</b> de los datos. \
Por ejemplo, si al crear una factura con 5 detalles falla el detalle 4, \
se deshacen los detalles 1, 2 y 3 también. La BD queda exactamente como estaba antes.",
    "Si capturas la excepción con try-catch dentro del método, Spring NO hace rollback porque considera que tú la manejaste.",
))

story.append(page_break())

# ---------- PARTE 7: ESCENARIOS PRÁCTICOS ----------
story.append(H1("PARTE 7: Escenarios Prácticos (¿Qué pasaría si...?)"))
story.append(P("Estas preguntas evalúan tu comprensión profunda del sistema."))
story.append(spacer())

story.extend(qa_block(
    "¿Qué pasaría si un producto se elimina pero ya tiene ventas asociadas?",
    "No pasa nada gracias al <b>soft delete</b>. Al 'eliminar' el producto solo ponemos activo = false. \
Las ventas históricas siguen teniendo sus DetalleVenta con el precio guardado en ese momento. \
El producto ya no aparece en el menú (porque los listados filtran activo=true), \
pero el historial se preserva. Si usáramos DELETE físico, romperíamos la integridad referencial \
o perderíamos la información de qué producto se vendió.",
    "El soft delete es la clave. Nunca elimines físicamente registros con historial.",
))

story.extend(qa_block(
    "¿Qué pasaría si dos meseros intentan pagar la misma mesa al mismo tiempo?",
    "Gracias a <b>@Transactional</b>, la base de datos maneja la concurrencia mediante bloqueos. \
Si dos transacciones intentan modificar la misma fila de la tabla mesa simultáneamente, \
una esperará a que la otra termine. Además, MesaService verifica que la mesa esté OCUPADA \
antes de procesar el pago; si ya fue pagada por otro mesero, la segunda transacción fallaría \
porque la mesa ya no estaría en estado OCUPADA.",
    "En producción se podría agregar un versionado optimista (@Version) para manejar concurrencia más explícitamente.",
))

story.extend(qa_block(
    "¿Cómo manejarían un cambio de precio de un producto que ya está en una mesa abierta?",
    "En nuestro diseño actual, los PedidoMesa guardan el <b>precioUnitario</b> al momento de agregar el producto. \
Si el administrador cambia el precio del producto en el catálogo, \
los pedidos YA existentes en mesas abiertas mantienen su precio original. \
Solo los NUEVOS pedidos usarán el precio actualizado. \
Esto es correcto porque el cliente debe pagar el precio que se le mostró cuando pidió.",
    "Este comportamiento es intencional y correcto desde el punto de vista del negocio.",
))

story.extend(qa_block(
    "¿Qué pasaría si intentan crear un proveedor con un RUC que ya existe?",
    "El <b>ProveedorService.crear()</b> verifica explícitamente si ya existe un proveedor con ese RUC \
antes de guardar. Si existe, lanza una RuntimeException con mensaje \
'Ya existe un proveedor con ese RUC'. Esta excepción es capturada por \
GlobalExceptionHandler y retorna un ApiResponse de error con HTTP 400 Bad Request. \
La transacción se hace rollback, por lo que no queda ningún dato inconsistente.",
    "Lo mismo ocurre con el email y con el DNI de clientes, y el username de usuarios.",
))

story.append(page_break())

# ---------- PARTE 8: PREGUNTAS DEL JURADO TIPO ORAL ----------
story.append(H1("PARTE 8: Simulacro Oral - Preguntas Rápidas"))
story.append(P("Responde cada una en menos de 30 segundos. Practica en voz alta."))
story.append(spacer())

quick_qa = [
    ("¿Cuántas capas tiene el proyecto y cuáles son?", 
     "4 capas: Controller (API), Service (Negocio), Repository (Datos), Entity (Modelo)."),
    
    ("¿Qué anotación usas para que una clase sea un endpoint REST?", 
     "@RestController en la clase y @RequestMapping o @GetMapping/@PostMapping en los métodos."),
    
    ("¿Cómo evitas que se guarden contraseñas en texto plano?", 
     "Usando BCryptPasswordEncoder para hashearlas antes de persistir. Nunca almaceno texto plano."),
    
    ("¿Qué es un Repository en Spring Data JPA?", 
     "Es una interfaz que extiende JpaRepository. Define métodos de consulta y Spring genera la implementación automáticamente."),
    
    ("¿Para qué sirve @RequestBody?", 
     "Indica que Spring debe convertir el cuerpo JSON de la petición HTTP en un objeto Java."),
    
    ("¿Para qué sirve @PathVariable?", 
     "Extrae un valor de la URL. Ejemplo: /productos/{id} → @PathVariable Long id captura el número."),
    
    ("¿Para qué sirve @RequestParam?", 
     "Extrae un parámetro de la query string. Ejemplo: /buscar?q=hamburguesa → @RequestParam String q."),
    
    ("¿Qué diferencia hay entre @NotNull y @NotBlank?", 
     "@NotNull valida que no sea null. @NotBlank valida que no sea null, vacío ni espacios en blanco. Solo aplica a String."),
    
    ("¿Qué es JPQL?", 
     "Java Persistence Query Language. Es SQL orientado a objetos. En vez de tablas y columnas, usa clases y atributos."),
    
    ("¿Qué hace CascadeType.ALL en una relación?", 
     "Propaga todas las operaciones (persist, merge, remove) del padre al hijo. Si guardo una factura, se guardan sus detalles automáticamente."),
    
    ("¿Por qué usan DTOs en vez de devolver Entities directamente?", 
     "Por seguridad, desacoplamiento y control de datos. Los DTOs evitan exponer campos sensibles y permiten validaciones específicas de la API."),
    
    ("¿Qué es Swagger/OpenAPI y para qué sirve?", 
     "Es una herramienta que documenta automáticamente la API REST. Genera una interfaz web donde se pueden probar los endpoints."),
    
    ("¿Qué significa que una API sea stateless?", 
     "Que el servidor no guarda estado del cliente entre peticiones. Cada petición es independiente y debe llevar toda la información necesaria."),
    
    ("¿Cómo se inicializan los datos por defecto (roles y usuarios)?", 
     "Con DataInitializer, que implementa CommandLineRunner. Se ejecuta automáticamente al iniciar la aplicación."),
    
    ("¿Qué es el patrón Builder y dónde lo usaste?", 
     "Permite construir objetos complejos paso a paso. Lo usamos en LoginResponse y con la anotación @Builder de Lombok en las entidades."),
]

for q, a in quick_qa:
    story.append(QUESTION(q))
    story.append(ANSWER(a))
    story.append(spacer(0.2 * cm))

story.append(page_break())

# ---------- PARTE 9: CHECKLIST FINAL ----------
story.append(H1("PARTE 9: Checklist Pre-Exposición"))
story.append(P("Antes de entrar a tu exposición, verifica que puedes responder sin leer:"))
story.append(spacer())

checklist_items = [
    "Explicar el diagrama de capas dibujándolo",
    "Describir el flujo de una petición HTTP de principio a fin",
    "Explicar qué es un DTO y por qué se usa",
    "Describir el ciclo de vida completo de una mesa (apertura a pago)",
    "Explicar cómo funciona el login (AuthController → AuthService → Repository)",
    "Mencionar 3 anotaciones de validación y su propósito",
    "Explicar la diferencia entre EAGER y LAZY",
    "Explicar qué es el soft delete y cómo se implementa",
    "Describir cómo se calcula el precio promedio ponderado",
    "Explicar por qué usan BigDecimal para dinero",
    "Mencionar qué es @Transactional y para qué sirve",
    "Explicar la diferencia entre @OneToOne, @ManyToOne y @OneToMany",
    "Describir el flujo de compra a proveedor y su impacto en inventario",
    "Explicar qué es CORS y por qué lo necesitan",
    "Mostrar Swagger UI funcionando",
]

for item in checklist_items:
    story.append(P(f"☐ {item}", style_body))
story.append(spacer())

story.append(TIP("Recomendación final: practica la exposición completa una vez cronometrando el tiempo. \
Una buena exposición técnica dura entre 10 y 15 minutos, dejando 5-10 minutos para preguntas del jurado."))
story.append(Spacer(1, 2 * cm))
story.append(P("<b>¡Mucho éxito en tu exposición!</b>", ParagraphStyle('Final', parent=style_title, fontSize=16, textColor=colors.HexColor('#1a5276'))))

# ============================================================
# GENERAR PDF
# ============================================================
output_path = "Preguntas_Respuestas_POS_Restaurante.pdf"
doc = SimpleDocTemplate(
    output_path,
    pagesize=A4,
    rightMargin=2 * cm,
    leftMargin=2 * cm,
    topMargin=2 * cm,
    bottomMargin=2 * cm,
)

doc.build(story)
print(f"PDF generado exitosamente: {os.path.abspath(output_path)}")
