#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generador de PDF - Guía de Estudio para Exposición
POS Restaurante - Backend Spring Boot
"""

import os
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle,
    PageBreak, ListFlowable, ListItem, KeepTogether
)
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont

# ============================================================
# CONFIGURACIÓN DE FUENTES (usar fuentes Windows con soporte Unicode)
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
            pdfmetrics.registerFont(TTFont(f"{font_name}-Bold", fp.replace(".ttf", "bd.ttf") if os.path.exists(fp.replace(".ttf", "bd.ttf")) else fp))
            DEFAULT_FONT = font_name
            font_registered = True
            break
        except Exception:
            continue

if not font_registered:
    DEFAULT_FONT = "Helvetica"

BOLD_FONT = f"{DEFAULT_FONT}-Bold" if font_registered else "Helvetica-Bold"

# ============================================================
# ESTILOS PERSONALIZADOS
# ============================================================
styles = getSampleStyleSheet()

style_title = ParagraphStyle(
    'CustomTitle',
    parent=styles['Title'],
    fontName=BOLD_FONT,
    fontSize=26,
    textColor=colors.HexColor('#1a5276'),
    spaceAfter=30,
    alignment=1,  # centrado
)

style_heading1 = ParagraphStyle(
    'CustomH1',
    parent=styles['Heading1'],
    fontName=BOLD_FONT,
    fontSize=18,
    textColor=colors.HexColor('#1a5276'),
    spaceAfter=14,
    spaceBefore=20,
    borderWidth=0,
    borderColor=colors.HexColor('#1a5276'),
    borderPadding=5,
    leftIndent=0,
)

style_heading2 = ParagraphStyle(
    'CustomH2',
    parent=styles['Heading2'],
    fontName=BOLD_FONT,
    fontSize=14,
    textColor=colors.HexColor('#2874a6'),
    spaceAfter=10,
    spaceBefore=14,
)

style_heading3 = ParagraphStyle(
    'CustomH3',
    parent=styles['Heading3'],
    fontName=BOLD_FONT,
    fontSize=12,
    textColor=colors.HexColor('#2e86c1'),
    spaceAfter=8,
    spaceBefore=10,
)

style_body = ParagraphStyle(
    'CustomBody',
    parent=styles['BodyText'],
    fontName=DEFAULT_FONT,
    fontSize=10,
    leading=14,
    spaceAfter=8,
)

style_code = ParagraphStyle(
    'CustomCode',
    parent=styles['Code'],
    fontName=DEFAULT_FONT,
    fontSize=9,
    textColor=colors.HexColor('#1c2833'),
    backColor=colors.HexColor('#f4f6f7'),
    leftIndent=10,
    rightIndent=10,
    spaceAfter=8,
    leading=12,
)

style_box = ParagraphStyle(
    'CustomBox',
    parent=styles['BodyText'],
    fontName=DEFAULT_FONT,
    fontSize=10,
    textColor=colors.HexColor('#1c2833'),
    backColor=colors.HexColor('#eaf2f8'),
    leftIndent=10,
    rightIndent=10,
    spaceAfter=10,
    leading=14,
    borderWidth=1,
    borderColor=colors.HexColor('#aed6f1'),
    borderPadding=8,
)

# ============================================================
# FUNCIONES AUXILIARES
# ============================================================
def P(text, style=style_body):
    return Paragraph(text, style)


def H1(text):
    return Paragraph(text, style_heading1)


def H2(text):
    return Paragraph(text, style_heading2)


def H3(text):
    return Paragraph(text, style_heading3)


def CODE(text):
    return Paragraph(text, style_code)


def BOX(text):
    return Paragraph(text, style_box)


def spacer(height=0.3 * cm):
    return Spacer(1, height)


def page_break():
    return PageBreak()


def bullet_list(items):
    """Crea una lista con viñetas."""
    return ListFlowable(
        [ListItem(P(f"• {item}")) for item in items],
        bulletType='bullet',
        leftIndent=20,
        spaceAfter=10,
    )


def numbered_list(items):
    """Crea una lista numerada."""
    return ListFlowable(
        [ListItem(P(item)) for item in items],
        bulletType='1',
        leftIndent=20,
        spaceAfter=10,
    )


def info_table(data, col_widths=None):
    """Crea una tabla con estilo profesional."""
    if col_widths is None:
        col_widths = [6 * cm, 10 * cm]
    t = Table(data, colWidths=col_widths)
    t.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#2874a6')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('FONTNAME', (0, 0), (-1, 0), BOLD_FONT),
        ('FONTSIZE', (0, 0), (-1, 0), 11),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 10),
        ('BACKGROUND', (0, 1), (-1, -1), colors.HexColor('#eaf2f8')),
        ('TEXTCOLOR', (0, 1), (-1, -1), colors.HexColor('#1c2833')),
        ('FONTNAME', (0, 1), (-1, -1), DEFAULT_FONT),
        ('FONTSIZE', (0, 1), (-1, -1), 10),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#aed6f1')),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('LEFTPADDING', (0, 0), (-1, -1), 8),
        ('RIGHTPADDING', (0, 0), (-1, -1), 8),
        ('BOTTOMPADDING', (0, 1), (-1, -1), 8),
        ('TOPPADDING', (0, 1), (-1, -1), 8),
    ]))
    return t


# ============================================================
# CONTENIDO DEL PDF
# ============================================================
story = []

# ---------- PORTADA ----------
story.append(Spacer(1, 4 * cm))
story.append(P("<b>SISTEMA POS PARA RESTAURANTE</b>", style_title))
story.append(spacer(0.5 * cm))
story.append(P("<b>Backend con Spring Boot</b>", ParagraphStyle('SubTitle', parent=style_title, fontSize=18, textColor=colors.HexColor('#5d6d7e'))))
story.append(spacer(1.5 * cm))
story.append(P("Guía de Estudio para Exposición", ParagraphStyle('SubTitle2', parent=style_title, fontSize=14, textColor=colors.HexColor('#7f8c8d'))))
story.append(spacer(0.3 * cm))
story.append(P("Arquitectura por Capas · APIs REST · Gestión de Inventario", ParagraphStyle('SubTitle3', parent=style_title, fontSize=12, textColor=colors.HexColor('#95a5a6'))))
story.append(Spacer(1, 4 * cm))

# Tabla de información del proyecto
portada_data = [
    ["Tecnología", "Spring Boot 3.2 + Java 17"],
    ["Persistencia", "Spring Data JPA (H2 / SQL Server / MySQL)"],
    ["Seguridad", "Spring Security + BCrypt"],
    ["Documentación", "OpenAPI / Swagger UI"],
    ["Patrón Arquitectónico", "MVC por Capas (Controller-Service-Repository-Entity)"],
    ["Build Tool", "Maven"],
]
story.append(info_table(portada_data, col_widths=[7 * cm, 9 * cm]))
story.append(page_break())

# ---------- 1. INTRODUCCIÓN ----------
story.append(H1("1. Introducción al Proyecto"))
story.append(P("Este documento es una guía completa para comprender, estudiar y exponer el sistema POS (Point of Sale) desarrollado para restaurantes. El proyecto está construido con <b>Spring Boot</b> siguiendo una arquitectura por capas claramente definida."))
story.append(spacer())

story.append(H2("1.1 ¿Qué es un POS de Restaurante?"))
story.append(P("Un sistema POS (Punto de Venta) para restaurante es una aplicación que permite gestionar todas las operaciones del negocio: ventas, mesas, pedidos, inventario, clientes, proveedores y usuarios. Este backend expone una API REST que puede ser consumida por un frontend web o móvil."))
story.append(spacer())

story.append(H2("1.2 Objetivos del Sistema"))
story.append(bullet_list([
    "Gestionar el catálogo de productos y categorías del menú",
    "Controlar el estado de las mesas y sus pedidos en tiempo real",
    "Registrar ventas y generar reportes estadísticos",
    "Mantener control de inventario con movimientos de entrada y salida",
    "Gestionar compras a proveedores y facturas",
    "Administrar usuarios con roles y permisos",
    "Proveer una API REST documentada con Swagger",
]))
story.append(page_break())

# ---------- 2. ARQUITECTURA ----------
story.append(H1("2. Arquitectura del Sistema"))
story.append(P("El proyecto sigue el patrón <b>arquitectura por capas</b>, una estructura clásica en aplicaciones empresariales que separa responsabilidades y facilita el mantenimiento."))
story.append(spacer())

story.append(H2("2.1 Diagrama de Capas"))
story.append(BOX("""
<b>┌─────────────────────────────────────────────────────────┐</b>
<b>│  CAPA DE PRESENTACIÓN  (Controller + DTO)</b>              │
│  · Recibe peticiones HTTP                               │
│  · Valida datos de entrada (@Valid)                     │
│  · Retorna respuestas JSON                              │
<b>├─────────────────────────────────────────────────────────┤</b>
<b>│  CAPA DE NEGOCIO  (Service)</b>                            │
│  · Contiene la lógica de negocio                        │
│  · Aplica reglas y validaciones                         │
│  · Coordina entre repositories                          │
│  · Convierte Entity ↔ DTO                               │
<b>├─────────────────────────────────────────────────────────┤</b>
<b>│  CAPA DE ACCESO A DATOS  (Repository)</b>                  │
│  · Accede a la base de datos                            │
│  · Usa Spring Data JPA (query methods / JPQL)           │
│  · No contiene lógica de negocio                        │
<b>├─────────────────────────────────────────────────────────┤</b>
<b>│  CAPA DE MODELO  (Entity)</b>                              │
│  · Mapea tablas de la base de datos                     │
│  · Define relaciones JPA (@OneToMany, @ManyToOne)       │
│  · Hereda campos comunes de BaseEntity                  │
<b>└─────────────────────────────────────────────────────────┘</b>
"""))
story.append(spacer())

story.append(H2("2.2 Flujo de una Petición"))
story.append(numbered_list([
    "<b>Cliente (Frontend)</b> envía una petición HTTP (GET, POST, PUT, DELETE)",
    "<b>Controller</b> recibe la petición, extrae parámetros y cuerpo JSON",
    "<b>Controller</b> delega al <b>Service</b> pasando los DTOs de entrada",
    "<b>Service</b> aplica lógica de negocio, validaciones y consulta <b>Repositories</b>",
    "<b>Repository</b> ejecuta consultas SQL/JPQL y retorna <b>Entities</b>",
    "<b>Service</b> convierte Entities a <b>DTOs</b> de respuesta",
    "<b>Controller</b> envuelve la respuesta en <b>ApiResponse&lt;T&gt;</b> y retorna JSON",
]))
story.append(page_break())

# ---------- 3. TECNOLOGÍAS ----------
story.append(H1("3. Tecnologías y Dependencias"))
story.append(P("El proyecto utiliza el ecosistema Spring Boot con las siguientes dependencias clave:"))
story.append(spacer())

tech_data = [
    ["Dependencia", "Propósito"],
    ["Spring Boot Starter Web", "Crear APIs REST con Tomcat embebido"],
    ["Spring Boot Starter Data JPA", "Acceso a base de datos con ORM (Hibernate)"],
    ["Spring Boot Starter Security", "Autenticación y autorización"],
    ["Spring Boot Starter Validation", "Validación de datos (@NotNull, @Email, etc.)"],
    ["H2 Database", "Base de datos en memoria para desarrollo"],
    ["SQL Server / MySQL Driver", "Conexión a bases de datos de producción"],
    ["Lombok", "Reducción de código repetitivo (getters, setters, builders)"],
    ["SpringDoc OpenAPI", "Documentación automática Swagger UI"],
    ["Spring Boot DevTools", "Recarga automática en desarrollo"],
]
story.append(info_table(tech_data, col_widths=[7 * cm, 9 * cm]))
story.append(page_break())

# ---------- 4. ESTRUCTURA DEL PROYECTO ----------
story.append(H1("4. Estructura del Proyecto"))
story.append(P("El código fuente está organizado por <b>módulos funcionales</b> (dominios), cada uno con sus propias capas:"))
story.append(spacer())

story.append(BOX("""
<b>src/main/java/com/restaurante/pos/</b>
│
├── <b>config/</b>          → SecurityConfig, CorsConfig, DataInitializer
├── <b>common/</b>          → ApiResponse, BaseEntity, GlobalExceptionHandler
│
├── <b>auth/</b>            → Login (Controller, Service, DTOs)
├── <b>usuario/</b>         → Gestión de usuarios y roles
├── <b>producto/</b>        → Catálogo de productos y categorías
├── <b>cliente/</b>         → Registro de clientes
├── <b>inventario/</b>      → Control de stock y movimientos
├── <b>mesa/</b>            → Gestión de mesas y pedidos
├── <b>venta/</b>           → Registro de ventas y estadísticas
└── <b>proveedor/</b>       → Proveedores y facturas de compra
"""))
story.append(spacer())

story.append(H2("4.1 Paquete Common (Compartido)"))
story.append(H3("BaseEntity"))
story.append(P("Clase abstracta que proporciona campos comunes a todas las entidades: <b>id</b>, <b>fechaCreacion</b>, <b>fechaActualizacion</b> y <b>activo</b>. Implementa <b>soft delete</b> (borrado lógico): en lugar de eliminar el registro físico, marca <i>activo = false</i>. Usa <b>@MappedSuperclass</b> para que las entidades hijas hereden estos campos sin crear tabla propia."))
story.append(spacer())

story.append(H3("ApiResponse<T>"))
story.append(P("Wrapper genérico que envuelve todas las respuestas de la API para mantener un formato consistente. Campos: <b>exitoso</b> (boolean), <b>mensaje</b> (String), <b>datos</b> (T), <b>fecha</b> (LocalDateTime). Incluye métodos estáticos <i>exito()</i> y <i>error()</i> para construir respuestas fácilmente."))
story.append(spacer())

story.append(H3("GlobalExceptionHandler"))
story.append(P("Clase anotada con <b>@RestControllerAdvice</b> que captura excepciones de toda la aplicación y retorna respuestas estandarizadas. Maneja <b>RuntimeException</b> (404/400), <b>MethodArgumentNotValidException</b> (errores de validación) y <b>Exception</b> genérica (500)."))
story.append(page_break())

# ---------- 5. MÓDULOS ----------
story.append(H1("5. Módulos del Sistema"))
story.append(P("A continuación se describe cada módulo, sus responsabilidades y cómo interactúan sus capas."))
story.append(spacer())

# --- AUTH ---
story.append(H2("5.1 Módulo de Autenticación (Auth)"))
story.append(P("Gestiona el acceso de usuarios al sistema mediante validación de credenciales."))
story.append(spacer())

mod_auth = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "AuthController", "Expone POST /auth/login y GET /auth/health"],
    ["Service", "AuthService", "Busca usuario, verifica activo, compara contraseña BCrypt"],
    ["DTO Entrada", "LoginRequest", "Email/username y password con @NotBlank"],
    ["DTO Salida", "LoginResponse", "success, message, id, nombre, email, rol (patrón Builder)"],
]
story.append(info_table(mod_auth, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(spacer())
story.append(BOX("<b>Flujo de Login:</b><br/>"
                 "1. Frontend envía credentials → LoginRequest<br/>"
                 "2. AuthController recibe y valida (@Valid)<br/>"
                 "3. AuthService busca usuario por username<br/>"
                 "4. Verifica que esté activo y que la contraseña coincida con BCrypt<br/>"
                 "5. Retorna LoginResponse con éxito (200) o error (401)"))
story.append(page_break())

# --- USUARIO ---
story.append(H2("5.2 Módulo de Usuarios"))
story.append(P("Gestiona los usuarios del sistema y sus roles (ADMIN, VENDEDOR, AUDITOR)."))
story.append(spacer())

mod_user = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "UsuarioController", "CRUD de usuarios y roles (/usuarios, /usuarios/roles)"],
    ["Service", "UsuarioService", "Valida unicidad de username, encripta password con BCrypt"],
    ["Entity", "Usuario", "Campos: username, password, nombre, apellido, email, teléfono, rol"],
    ["Entity", "Rol", "Nombre y descripción del rol"],
    ["Repository", "UsuarioRepository", "findByUsername, existsByUsername, findByRolId, etc."],
    ["Repository", "RolRepository", "findByNombre, existsByNombre"],
]
story.append(info_table(mod_user, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(spacer())
story.append(BOX("<b>Seguridad:</b><br/>"
                 "· La contraseña NUNCA se almacena en texto plano<br/>"
                 "· Se usa BCryptPasswordEncoder para hashear antes de guardar<br/>"
                 "· El campo password tiene length=255 para almacenar el hash (60 chars)"))
story.append(page_break())

# --- PRODUCTO ---
story.append(H2("5.3 Módulo de Productos y Categorías"))
story.append(P("Administra el menú del restaurante: productos con precios, costos, imágenes y categorías."))
story.append(spacer())

mod_prod = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "ProductoController", "CRUD, disponibles, buscar, filtrar por categoría"],
    ["Controller", "CategoriaController", "CRUD de categorías"],
    ["Service", "ProductoService", "Lógica de negocio, conversiones Entity↔DTO"],
    ["Entity", "Producto", "Código, nombre, descripción, precio, costo, imagen, disponible, categoría"],
    ["Entity", "Categoria", "Nombre, descripción"],
]
story.append(info_table(mod_prod, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(spacer())
story.append(BOX("<b>Nota importante:</b><br/>"
                 "· Se usa <b>BigDecimal</b> para precios y costos (NUNCA double/float)<br/>"
                 "· precision=10, scale=2 define DECIMAL(10,2) en la BD<br/>"
                 "· Un producto puede requerir preparación en cocina o ser directo (bebidas)"))
story.append(page_break())

# --- CLIENTE ---
story.append(H2("5.4 Módulo de Clientes"))
story.append(P("Registro de clientes para fidelización y control de compras acumuladas."))
story.append(spacer())

mod_cli = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "ClienteController", "CRUD + búsqueda difusa (/clientes/buscar)"],
    ["Service", "ClienteService", "Valida DNI/email únicos, soft delete"],
    ["Entity", "Cliente", "Nombre, apellido, DNI (único), email (único), totalCompras"],
    ["Repository", "ClienteRepository", "findByDni, findByEmail, buscar JPQL con LIKE"],
]
story.append(info_table(mod_cli, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(page_break())

# --- INVENTARIO ---
story.append(H2("5.5 Módulo de Inventario"))
story.append(P("Controla el stock de productos mediante movimientos de entrada y salida. Es uno de los módulos más importantes del POS."))
story.append(spacer())

mod_inv = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "InventarioController", "Consultar stock, alertas, movimientos, kardex"],
    ["Service", "InventarioService", "Registra entradas/salidas, ajustes, precio promedio ponderado"],
    ["Entity", "Inventario", "Cantidad, stockMin/Max, ubicación, precioPromedio. Relación 1:1 con Producto"],
    ["Entity", "MovimientoInventario", "Tipo (ENTRADA/SALIDA), motivo, cantidad, stock anterior/posterior"],
    ["Entity", "TipoMovimiento", "Enum: ENTRADA, SALIDA"],
    ["Entity", "MotivoMovimiento", "Enum: COMPRA, VENTA, MERMA, AJUSTE, DEVOLUCIÓN, TRANSFERENCIA"],
]
story.append(info_table(mod_inv, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(spacer())

story.append(H3("Tipos de Movimiento"))
story.append(bullet_list([
    "<b>ENTRADA:</b> Aumenta el stock. Motivos: COMPRA, DEVOLUCIÓN, AJUSTE (+)",
    "<b>SALIDA:</b> Disminuye el stock. Motivos: VENTA, MERMA, AJUSTE (-)",
]))
story.append(spacer())

story.append(H3("Precio Promedio Ponderado"))
story.append(P("Cuando llega una compra, se recalcula el precio promedio del inventario usando la fórmula de promedio ponderado. Esto permite valorizar el stock de forma contablemente correcta."))
story.append(CODE("nuevoPrecio = (valorActual + valorNuevo) / nuevaCantidad<br/>"
                  "valorActual = precioPromedioActual * cantidadActual<br/>"
                  "valorNuevo  = precioUnitarioCompra * cantidadComprada"))
story.append(page_break())

# --- MESA ---
story.append(H2("5.6 Módulo de Mesas"))
story.append(P("Gestiona el mapa de mesas del restaurante y el ciclo de vida completo de atención: apertura, pedidos, pago y cierre."))
story.append(spacer())

mod_mesa = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "MesaController", "CRUD mesas, abrir, agregar/eliminar producto, pago"],
    ["Service", "MesaService", "Ciclo de vida mesa: abrir → pedidos → pago → cerrar → generar Venta"],
    ["Entity", "Mesa", "Número, capacidad, estado (LIBRE/OCUPADA/RESERVADA), totales, posición X/Y"],
    ["Entity", "PedidoMesa", "Línea de pedido: producto, cantidad, precioUnitario, subtotal"],
]
story.append(info_table(mod_mesa, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(spacer())

story.append(BOX("<b>Ciclo de vida de una Mesa:</b><br/>"
                 "1. <b>Crear mesa</b> → estado LIBRE<br/>"
                 "2. <b>Abrir mesa</b> → estado OCUPADA, horaApertura=now<br/>"
                 "3. <b>Agregar productos</b> → se crean PedidoMesa, se suma al total<br/>"
                 "4. <b>Actualizar propina</b> (opcional)<br/>"
                 "5. <b>Procesar pago</b> → se crea una Venta, se limpia la mesa<br/>"
                 "6. <b>Mesa vuelve a LIBRE</b> → lista para siguiente cliente"))
story.append(page_break())

# --- VENTA ---
story.append(H2("5.7 Módulo de Ventas"))
story.append(P("Registra el historial de ventas completadas y genera estadísticas para reportes. Las ventas se crean automáticamente al pagar una mesa."))
story.append(spacer())

mod_venta = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "VentaController", "Listar, buscar con filtros, estadísticas, resumen por día"],
    ["Service", "VentaService", "Consultas, filtros combinados, cálculo de estadísticas"],
    ["Entity", "Venta", "Fecha, hora, subtotal, impuestos, descuentos, total, métodoPago, propina"],
    ["Entity", "DetalleVenta", "Producto, cantidad, precioUnitario, subtotal. Precio se guarda histórico"],
]
story.append(info_table(mod_venta, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(spacer())
story.append(BOX("<b>Nota:</b><br/>"
                 "· Las ventas NO se crean directamente desde VentaService<br/>"
                 "· Se generan automáticamente en MesaService.procesarPago()<br/>"
                 "· El precio se guarda al momento de la venta (histórico)<br/>"
                 "· La anulación es lógica: cambia estado a ANULADA"))
story.append(page_break())

# --- PROVEEDOR ---
story.append(H2("5.8 Módulo de Proveedores"))
story.append(P("Gestiona los proveedores que suministran productos al restaurante y las facturas de compra asociadas."))
story.append(spacer())

mod_prov = [
    ["Capa", "Clase", "Responsabilidad"],
    ["Controller", "ProveedorController", "CRUD, búsqueda, filtro por categoría"],
    ["Service", "ProveedorService", "Valida RUC/email únicos, soft delete"],
    ["Entity", "Proveedor", "Nombre, RUC (único), contacto, email (único), categoría, activo"],
    ["Entity", "FacturaProveedor", "Número, fechas, subtotal, impuestos, total, estado"],
    ["Entity", "DetalleFactura", "Cantidad, precioUnitario, subtotal, producto"],
]
story.append(info_table(mod_prov, col_widths=[3 * cm, 5 * cm, 8 * cm]))
story.append(spacer())

story.append(H3("Estados de Factura"))
story.append(bullet_list([
    "<b>PENDIENTE:</b> Recién registrada, aún no actualiza inventario",
    "<b>PROCESADA:</b> Ya sumó los productos al inventario",
    "<b>ANULADA:</b> Cancelada, revirtió cambios en inventario",
]))
story.append(page_break())

# ---------- 6. FLUJOS DE EJEMPLO ----------
story.append(H1("6. Flujos de Ejemplo (Para la Exposición)"))
story.append(P("Estos tres flujos son ideales para explicar en la exposición cómo interactúan las capas."))
story.append(spacer())

story.append(H2("6.1 Flujo 1: Proceso de Login"))
story.append(numbered_list([
    "El usuario ingresa email y contraseña en el frontend",
    "Frontend envía POST /auth/login con body JSON → LoginRequest",
    "AuthController recibe la petición y activa @Valid (valida @NotBlank)",
    "AuthController delega a authService.login(request)",
    "AuthService busca el usuario en UsuarioRepository.findByUsername()",
    "Si existe, verifica que esté activo (usuario.getActivo() == true)",
    "Si está activo, compara contraseñas: passwordEncoder.matches(plain, hash)",
    "Si coincide, construye LoginResponse con Builder y retorna éxito",
    "AuthController recibe la respuesta y retorna HTTP 200 OK o 401 UNAUTHORIZED",
]))
story.append(spacer())

story.append(H2("6.2 Flujo 2: Venta Completa en Mesa (El flujo estrella)"))
story.append(numbered_list([
    "El mesero abre la mesa: POST /mesas/{id}/abrir → estado cambia a OCUPADA",
    "Agrega productos: POST /mesas/{id}/productos con PedidoMesaDTO",
    "MesaService valida que el producto exista y esté activo",
    "Crea PedidoMesa con subtotal = precio × cantidad",
    "Suma el subtotal al total de la mesa",
    "Cuando el cliente pide la cuenta, el mesero procesa pago: POST /mesas/pago",
    "MesaService valida que haya pedidos y calcular totales (subtotal + propina)",
    "Crea una Venta con todos los datos del pago",
    "Convierte cada PedidoMesa en DetalleVenta",
    "Guarda la venta (cascade guarda los detalles)",
    "Registra salida de inventario por cada producto vendido",
    "Limpia la mesa: elimina pedidos, reinicia totales, estado = LIBRE",
]))
story.append(spacer())

story.append(H2("6.3 Flujo 3: Compra a Proveedor y Entrada de Inventario"))
story.append(numbered_list([
    "El administrador registra una factura: POST /facturas con CrearFacturaDTO",
    "Incluye proveedorId, número de factura, fecha y lista de detalles",
    "Cada detalle tiene: productoId, cantidad, precioUnitario",
    "El servicio valida que el proveedor y los productos existan",
    "Crea FacturaProveedor con estado PENDIENTE",
    "Crea DetalleFactura por cada línea",
    "Al procesar la factura, registra ENTRADA en inventario por cada producto",
    "Actualiza cantidad y recalcula precio promedio ponderado",
    "Crea MovimientoInventario con motivo COMPRA",
    "Cambia estado de factura a PROCESADA",
]))
story.append(page_break())

# ---------- 7. BASE DE DATOS ----------
story.append(H1("7. Modelo de Base de Datos"))
story.append(P("El sistema utiliza JPA/Hibernate con generación automática de tablas. A continuación las entidades principales y sus relaciones."))
story.append(spacer())

story.append(BOX("""
<b>RELACIONES PRINCIPALES:</b><br/><br/>

<b>Producto (1)  ────► (N) Inventario</b><br/>
&nbsp;&nbsp;Un producto tiene exactamente un inventario (@OneToOne)<br/><br/>

<b>Categoria (1)  ────► (N) Producto</b><br/>
&nbsp;&nbsp;Una categoría tiene muchos productos (@OneToMany / @ManyToOne)<br/><br/>

<b>Proveedor (1)  ────► (N) FacturaProveedor</b><br/>
&nbsp;&nbsp;Un proveedor emite muchas facturas (@OneToMany / @ManyToOne)<br/><br/>

<b>FacturaProveedor (1)  ────► (N) DetalleFactura</b><br/>
&nbsp;&nbsp;Una factura tiene muchas líneas de producto (@OneToMany mappedBy, cascade=ALL)<br/><br/>

<b>Mesa (1)  ────► (N) PedidoMesa</b><br/>
&nbsp;&nbsp;Una mesa tiene muchos pedidos durante su ciclo de vida<br/><br/>

<b>Venta (1)  ────► (N) DetalleVenta</b><br/>
&nbsp;&nbsp;Una venta tiene muchas líneas de producto vendido<br/><br/>

<b>Rol (1)  ────► (N) Usuario</b><br/>
&nbsp;&nbsp;Un rol puede estar asignado a muchos usuarios (@ManyToOne)<br/><br/>

<b>Inventario (1)  ────► (N) MovimientoInventario</b><br/>
&nbsp;&nbsp;Un inventario tiene muchos movimientos históricos
"""))
story.append(page_break())

# ---------- 8. CONFIGURACIONES CLAVE ----------
story.append(H1("8. Configuraciones Clave"))
story.append(spacer())

story.append(H2("8.1 Seguridad (SecurityConfig)"))
story.append(P("Configura la cadena de filtros de Spring Security. Actualmente en modo desarrollo permite todas las peticiones. Define tres beans clave:"))
story.append(bullet_list([
    "<b>SecurityFilterChain:</b> Desactiva CSRF, configura CORS, permite todas las requests",
    "<b>CorsConfigurationSource:</b> Orígenes, métodos y headers permitidos",
    "<b>PasswordEncoder (BCrypt):</b> Hashea contraseñas de forma segura",
]))
story.append(spacer())

story.append(H2("8.2 CORS (CorsConfig)"))
story.append(P("Permite que el frontend (normalmente en http://localhost:5173 con Vite/React) se comunique con el backend (http://localhost:8080). Sin CORS, el navegador bloquearía las peticiones por seguridad."))
story.append(spacer())

story.append(H2("8.3 Inicialización de Datos (DataInitializer)"))
story.append(P("Implementa CommandLineRunner para insertar datos automáticamente al iniciar la aplicación. Crea roles (ADMIN, VENDEDOR, AUDITOR) y usuarios de prueba con contraseñas hasheadas."))
story.append(page_break())

# ---------- 9. PUNTOS CLAVE PARA LA EXPOSICIÓN ----------
story.append(H1("9. Puntos Clave para la Exposición"))
story.append(P("Responde estas preguntas para demostrar dominio del proyecto:"))
story.append(spacer())

story.append(H2("9.1 Preguntas Frecuentes del Jurado"))
faq = [
    ["Pregunta", "Respuesta sugerida"],
    ["¿Por qué usan DTOs en vez de Entities directamente?", "Por seguridad (no exponer campos sensibles), desacoplamiento y control de qué datos viajan en la API."],
    ["¿Por qué BigDecimal y no double?", "BigDecimal evita errores de redondeo en cálculos monetarios. double usa punto flotante binario que es impreciso."],
    ["¿Qué es el soft delete?", "En vez de borrar físicamente, marca activo=false. Preserva historial e integridad referencial."],
    ["¿Cómo funciona la inyección de dependencias?", "Spring crea los beans y los inyecta por constructor (@RequiredArgsConstructor). Facilita testing y reduce acoplamiento."],
    ["¿Qué es @Transactional?", "Garantiza que todas las operaciones dentro del método sean atómicas. Si falla una, se hace rollback."],
    ["¿Cómo se validan los datos de entrada?", "Con Bean Validation (@NotBlank, @Email, @Size) activadas por @Valid en el Controller."],
    ["¿Qué patrón usa LoginResponse?", "Patrón Builder para construir objetos con muchos campos opcionales de forma legible."],
    ["¿Cómo documentan la API?", "Con SpringDoc OpenAPI. Swagger UI disponible en /swagger-ui.html"],
]
story.append(info_table(faq, col_widths=[7 * cm, 9 * cm]))
story.append(spacer())

story.append(H2("9.2 Highlights Técnicos para Mencionar"))
story.append(bullet_list([
    "<b>Arquitectura por capas</b> claramente separada (Controller-Service-Repository-Entity)",
    "<b>Soft delete</b> en todas las entidades mediante herencia de BaseEntity",
    "<b>ApiResponse genérica</b> que estandariza todas las respuestas del backend",
    "<b>Manejo global de excepciones</b> con @RestControllerAdvice",
    "<b>Precio promedio ponderado</b> en inventario para valorización contable",
    "<b>Ciclo de vida completo de mesa</b>: apertura, pedidos, pago, cierre, generación de venta",
    "<b>Auditoría de inventario</b> mediante MovimientoInventario con stock anterior/posterior",
    "<b>Validaciones declarativas</b> con Bean Validation (@Valid)",
    "<b>Documentación automática</b> con Swagger/OpenAPI",
]))
story.append(page_break())

# ---------- 10. CONCLUSIONES ----------
story.append(H1("10. Conclusiones"))
story.append(P("Este proyecto demuestra la aplicación de conceptos fundamentales del desarrollo backend empresarial con Spring Boot:"))
story.append(spacer())

story.append(numbered_list([
    "<b>Separación de responsabilidades:</b> Cada capa tiene una función clara y única.",
    "<b>Seguridad:</b> Contraseñas hasheadas, soft delete, validaciones de entrada.",
    "<b>Mantenibilidad:</b> Uso de DTOs, inyección por constructor, Lombok para reducir boilerplate.",
    "<b>Escalabilidad:</b> Modularización por dominios permite agregar funcionalidades sin afectar el resto.",
    "<b>Trabajo en equipo:</b> La API REST documentada permite que frontend y backend trabajen en paralelo.",
]))
story.append(spacer())

story.append(BOX("<b>Consejo para la exposición:</b><br/>"
                 "· Muestra primero el <b>Swagger UI</b> corriendo (http://localhost:8080/api/swagger-ui.html)<br/>"
                 "· Explica el <b>diagrama de capas</b> con un dibujo en el pizarrón<br/>"
                 "· Elige UN flujo completo (recomendado: Venta en Mesa) y síguelo paso a paso<br/>"
                 "· Menciona los <b>patrones de diseño</b> que usaste: Builder, DTO, MVC, Soft Delete<br/>"
                 "· Si te preguntan algo que no recuerdas, apóyate en los comentarios del código"))
story.append(Spacer(1, 2 * cm))
story.append(P("<b>¡Éxitos en tu exposición!</b>", ParagraphStyle('Final', parent=style_title, fontSize=16, textColor=colors.HexColor('#1a5276'))))

# ============================================================
# GENERAR PDF
# ============================================================
output_path = "Guia_Exposicion_POS_Restaurante.pdf"
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
