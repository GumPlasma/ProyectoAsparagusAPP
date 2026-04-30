from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.lib.colors import HexColor, black, white
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, Image
from reportlab.lib import colors
from reportlab.pdfbase import pdfmetrics
from datetime import datetime

# Crear el PDF
doc = SimpleDocTemplate(
    "documentacion_tablas_principales.pdf",
    pagesize=letter,
    rightMargin=0.5*inch,
    leftMargin=0.5*inch,
    topMargin=0.5*inch,
    bottomMargin=0.5*inch
)

# Container para los elementos
story = []

# Estilos personalizados
title_style = ParagraphStyle(
    'CustomTitle',
    parent=getSampleStyleSheet()['Heading1'],
    fontSize=18,
    textColor=HexColor('#1a1a2e'),
    spaceAfter=12,
    alignment=1  # Center
)

subtitle_style = ParagraphStyle(
    'CustomSubtitle',
    parent=getSampleStyleSheet()['Heading2'],
    fontSize=14,
    textColor=HexColor('#16213e'),
    spaceAfter=10,
    spaceBefore=12
)

normal_style = ParagraphStyle(
    'CustomNormal',
    parent=getSampleStyleSheet()['Normal'],
    fontSize=10,
    textColor=HexColor('#333333'),
    leading=12
)

code_style = ParagraphStyle(
    'Code',
    parent=getSampleStyleSheet()['Code'],
    fontSize=9,
    textColor=HexColor('#0f3460'),
    backColor=HexColor('#f5f5f5'),
    leftIndent=20,
    rightIndent=20
)

# ============================================
# PORTADA / HEADER
# ============================================
story.append(Spacer(1, 0.3*inch))

header_text = Paragraph("SISTEMA POS - RESTAURANTE", title_style)
story.append(header_text)
story.append(Paragraph("Documentación de Tablas Principales", subtitle_style))
story.append(Spacer(1, 0.2*inch))

info_text = Paragraph(
    f"Fecha de generación: {datetime.now().strftime('%Y-%m-%d')}<br/>"
    f"Framework: Spring Boot 3.2.0 (Java 17)<br/>"
    f"Base de datos: SQL Server / MySQL",
    normal_style
)
story.append(info_text)
story.append(Spacer(1, 0.3*inch))

# ============================================
# INTRODUCCIÓN
# ============================================
intro = Paragraph(
    "Este documento describe las 4 tablas principales del sistema POS para restaurante. "
    "Cada tabla representa una entidad fundamental para la gestión de ventas, productos, "
    "inventario y mesas del establecimiento.",
    normal_style
)
story.append(intro)
story.append(Spacer(1, 0.3*inch))

# ============================================
# TABLA 1: PRODUCTO
# ============================================
story.append(Paragraph("1. TABLA: PRODUCTO", subtitle_style))
story.append(Paragraph(
    "Representa los productos del menú del restaurante. Cada producto pertenece a una categoría "
    "y contiene información sobre precios, disponibilidad y preparación.",
    normal_style
))
story.append(Spacer(1, 6))

# Definir datos de la tabla
producto_data = [
    ['Atributo', 'Tipo', 'Nullable', 'Descripción'],
    ['id', 'Long', 'No', 'Identificador único autoincremental'],
    ['codigo', 'String(50)', 'Sí', 'Código interno / código de barras'],
    ['nombre', 'String(150)', 'No', 'Nombre del producto'],
    ['descripcion', 'TEXT', 'Sí', 'Descripción detallada e ingredientes'],
    ['precio', 'BigDecimal(10,2)', 'No', 'Precio de venta al público'],
    ['costo', 'BigDecimal(10,2)', 'Sí', 'Costo para cálculo de ganancias'],
    ['imagen_url', 'String(500)', 'Sí', 'URL de imagen del producto'],
    ['disponible', 'Boolean', 'Sí', 'Indica si está disponible para venta'],
    ['requiere_preparacion', 'Boolean', 'Sí', 'Si requiere preparación en cocina'],
    ['tiempo_preparacion', 'Integer', 'Sí', 'Tiempo estimado en minutos'],
    ['categoria_id', 'Long', 'No', 'Foreign Key hacia tabla CATEGORIA'],
]

producto_table = Table(producto_data, colWidths=[1.2*inch, 0.9*inch, 0.7*inch, 2.2*inch])
producto_table.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), HexColor('#16213e')),
    ('TEXTCOLOR', (0, 0), (-1, 0), white),
    ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
    ('FONTSIZE', (0, 0), (-1, 0), 10),
    ('FONTSIZE', (0, 1), (-1, -1), 9),
    ('BOTTOMPADDING', (0, 0), (-1, 0), 10),
    ('TOPPADDING', (0, 0), (-1, 0), 10),
    ('BACKGROUND', (0, 1), (-1, -1), HexColor('#f9f9f9')),
    ('GRID', (0, 0), (-1, -1), 0.5, HexColor('#dddddd')),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1), [white, HexColor('#f5f5f5')]),
]))
story.append(producto_table)
story.append(Spacer(1, 12))

story.append(Paragraph("Ejemplo de uso:", code_style))
story.append(Spacer(1, 4))
story.append(Paragraph(
    "Producto: Hamburguesa Clásica | Precio: $15.00 | Categoría: Platos Principales",
    code_style
))
story.append(Spacer(1, 0.2*inch))

# ============================================
# TABLA 2: VENTA
# ============================================
story.append(Paragraph("2. TABLA: VENTA", subtitle_style))
story.append(Paragraph(
    "Representa las transacciones de venta realizadas. Contiene información del cliente, "
    "vendedor, métodos de pago, impuestos y totales. Una venta puede tener múltiples "
    "productos a través de la tabla DETALLE_VENTA.",
    normal_style
))
story.append(Spacer(1, 6))

venta_data = [
    ['Atributo', 'Tipo', 'Nullable', 'Descripción'],
    ['id', 'Long', 'No', 'Identificador único autoincremental'],
    ['numero_comprobante', 'String(20)', 'Sí', 'Número único del comprobante'],
    ['tipo_comprobante', 'String(20)', 'Sí', 'TICKET, FACTURA, BOLETA'],
    ['fecha', 'LocalDate', 'No', 'Fecha de la venta'],
    ['hora', 'LocalDateTime', 'Sí', 'Hora exacta de la venta'],
    ['tipo_venta', 'String(20)', 'Sí', 'LLEVAR, COMER_AQUI, DELIVERY'],
    ['estado', 'String(20)', 'Sí', 'PENDIENTE, COMPLETADA, ANULADA'],
    ['subtotal', 'BigDecimal(12,2)', 'No', 'Subtotal antes de impuestos'],
    ['porcentaje_impuesto', 'BigDecimal(5,2)', 'Sí', 'Porcentaje de impuesto (ej: 18%)'],
    ['monto_impuesto', 'BigDecimal(12,2)', 'Sí', 'Valor calculado del impuesto'],
    ['descuento', 'BigDecimal(12,2)', 'Sí', 'Descuento aplicado'],
    ['total', 'BigDecimal(12,2)', 'No', 'Total final de la venta'],
    ['metodo_pago', 'String(20)', 'Sí', 'EFECTIVO, TARJETA, TRANSFERENCIA'],
    ['monto_recibido', 'BigDecimal(12,2)', 'Sí', 'Monto recibido del cliente'],
    ['vuelto', 'BigDecimal(12,2)', 'Sí', 'Cambio devuelto al cliente'],
    ['mesa_numero', 'Integer', 'Sí', 'Número de mesa asociada'],
    ['propina', 'BigDecimal(12,2)', 'Sí', 'Propina opcional'],
    ['observaciones', 'TEXT', 'Sí', 'Notas adicionales de la venta'],
    ['cliente_id', 'Long', 'Sí', 'Foreign Key hacia tabla CLIENTE'],
    ['vendedor_id', 'Long', 'Sí', 'Foreign Key hacia tabla USUARIO'],
]

venta_table = Table(venta_data, colWidths=[1.1*inch, 0.8*inch, 0.6*inch, 2.0*inch])
venta_table.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), HexColor('#16213e')),
    ('TEXTCOLOR', (0, 0), (-1, 0), white),
    ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
    ('FONTSIZE', (0, 0), (-1, 0), 10),
    ('FONTSIZE', (0, 1), (-1, -1), 9),
    ('BOTTOMPADDING', (0, 0), (-1, 0), 10),
    ('TOPPADDING', (0, 0), (-1, 0), 10),
    ('BACKGROUND', (0, 1), (-1, -1), HexColor('#f9f9f9')),
    ('GRID', (0, 0), (-1, -1), 0.5, HexColor('#dddddd')),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1), [white, HexColor('#f5f5f5')]),
]))
story.append(venta_table)
story.append(Spacer(1, 12))

story.append(Paragraph("Relación con DETALLE_VENTA: Una venta tiene muchos detalles (1:N)", code_style))
story.append(Spacer(1, 0.2*inch))

# ============================================
# TABLA 3: DETALLE_VENTA
# ============================================
story.append(Paragraph("3. TABLA: DETALLE_VENTA", subtitle_style))
story.append(Paragraph(
    "Representa cada línea de producto dentro de una venta. Contiene la cantidad, "
    "precio unitario (congelado al momento de la venta), subtotal y estado de preparación. "
    "Esta tabla permite mantener el historial aunque los precios del producto cambien.",
    normal_style
))
story.append(Spacer(1, 6))

detalle_data = [
    ['Atributo', 'Tipo', 'Nullable', 'Descripción'],
    ['id', 'Long', 'No', 'Identificador único autoincremental'],
    ['cantidad', 'Integer', 'No', 'Cantidad vendida del producto'],
    ['precio_unitario', 'BigDecimal(10,2)', 'No', 'Precio al momento de la venta'],
    ['subtotal', 'BigDecimal(12,2)', 'No', 'cantidad × precio_unitario'],
    ['descuento', 'BigDecimal(10,2)', 'Sí', 'Descuento en esta línea'],
    ['notas', 'String(255)', 'Sí', 'Notas especiales (ej: sin cebolla)'],
    ['estado_preparacion', 'String(20)', 'Sí', 'PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO'],
    ['venta_id', 'Long', 'No', 'Foreign Key hacia tabla VENTA'],
    ['producto_id', 'Long', 'No', 'Foreign Key hacia tabla PRODUCTO'],
]

detalle_table = Table(detalle_data, colWidths=[1.0*inch, 0.8*inch, 0.6*inch, 2.6*inch])
detalle_table.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), HexColor('#16213e')),
    ('TEXTCOLOR', (0, 0), (-1, 0), white),
    ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
    ('FONTSIZE', (0, 0), (-1, 0), 10),
    ('FONTSIZE', (0, 1), (-1, -1), 9),
    ('BOTTOMPADDING', (0, 0), (-1, 0), 10),
    ('TOPPADDING', (0, 0), (-1, 0), 10),
    ('BACKGROUND', (0, 1), (-1, -1), HexColor('#f9f9f9')),
    ('GRID', (0, 0), (-1, -1), 0.5, HexColor('#dddddd')),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1), [white, HexColor('#f5f5f5')]),
]))
story.append(detalle_table)
story.append(Spacer(1, 12))

story.append(Paragraph("Métodos importantes: calcularSubtotal(), enviarACocina(), marcarListo(), marcarEntregado()", code_style))
story.append(Spacer(1, 0.2*inch))

# ============================================
# TABLA 4: INVENTARIO
# ============================================
story.append(Paragraph("4. TABLA: INVENTARIO", subtitle_style))
story.append(Paragraph(
    "Gestiona el stock de cada producto. Contiene la cantidad actual, límites mínimos/máximos, "
    "ubicación física y precio promedio. El stock se actualiza mediante la tabla "
    "MOVIMIENTO_INVENTARIO, nunca se modifica directamente.",
    normal_style
))
story.append(Spacer(1, 6))

inventario_data = [
    ['Atributo', 'Tipo', 'Nullable', 'Descripción'],
    ['id', 'Long', 'No', 'Identificador único autoincremental'],
    ['cantidad', 'Integer', 'No', 'Stock actual del producto'],
    ['stock_minimo', 'Integer', 'Sí', 'Límite mínimo para alerta (default: 5)'],
    ['stock_maximo', 'Integer', 'Sí', 'Límite máximo (default: 100)'],
    ['ubicacion', 'String(100)', 'Sí', 'Ubicación física (estante, refrigerador)'],
    ['ultimo_movimiento', 'LocalDateTime', 'Sí', 'Fecha del último movimiento'],
    ['precio_promedio', 'BigDecimal(10,2)', 'Sí', 'Precio promedio ponderado'],
    ['producto_id', 'Long', 'No', 'Foreign Key única hacia PRODUCTO (1:1)'],
]

inventario_table = Table(inventario_data, colWidths=[1.0*inch, 0.8*inch, 0.6*inch, 2.6*inch])
inventario_table.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), HexColor('#16213e')),
    ('TEXTCOLOR', (0, 0), (-1, 0), white),
    ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
    ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
    ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
    ('FONTSIZE', (0, 0), (-1, 0), 10),
    ('FONTSIZE', (0, 1), (-1, -1), 9),
    ('BOTTOMPADDING', (0, 0), (-1, 0), 10),
    ('TOPPADDING', (0, 0), (-1, 0), 10),
    ('BACKGROUND', (0, 1), (-1, -1), HexColor('#f9f9f9')),
    ('GRID', (0, 0), (-1, -1), 0.5, HexColor('#dddddd')),
    ('ROWBACKGROUNDS', (0, 1), (-1, -1), [white, HexColor('#f5f5f5')]),
]))
story.append(inventario_table)
story.append(Spacer(1, 12))

story.append(Paragraph("Métodos importantes: isStockBajo(), hayStock(cantidadSolicitada)", code_style))
story.append(Spacer(1, 0.2*inch))

# ============================================
# DIAGRAMA ER RELACIONAL
# ============================================
story.append(Spacer(1, 0.2*inch))
story.append(Paragraph("RELACIONES ENTRE TABLAS", subtitle_style))

relaciones_text = Paragraph(
    "<b>PRODUCTO → INVENTARIO:</b> Relación 1:1 (un producto tiene un registro de inventario)<br/><br/>"
    "<b>VENTA → DETALLE_VENTA:</b> Relación 1:N (una venta tiene muchos detalles)<br/><br/>"
    "<b>PRODUCTO → DETALLE_VENTA:</b> Relación 1:N (un producto puede estar en muchos detalles)<br/><br/>"
    "<b>CLIENTE → VENTA:</b> Relación 1:N (un cliente puede tener muchas ventas)<br/><br/>"
    "<b>USUARIO → VENTA:</b> Relación 1:N (un vendedor puede realizar muchas ventas)",
    normal_style
)
story.append(relaciones_text)
story.append(Spacer(1, 0.3*inch))

# ============================================
# FOOTER
# ============================================
footer_text = Paragraph(
    "Generado automáticamente desde el código fuente del proyecto POS-RESTAURANTE<br/>"
    "Spring Boot 3.2.0 | Java 17 | JPA/Hibernate",
    ParagraphStyle('Footer', parent=normal_style, alignment=1, fontSize=8, textColor=HexColor('#666666'))
)
story.append(footer_text)

# Build PDF
doc.build(story)
print("PDF generado exitosamente: documentacion_tablas_principales.pdf")
