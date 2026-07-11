package com.example.arcshiftwelding.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.arcshiftwelding.data.local.relation.CotizacionCompleta
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PDF_PAGE_WIDTH = 595
private const val PDF_PAGE_HEIGHT = 842
private const val PDF_MARGIN = 32f
private const val PDF_CONTENT_WIDTH = PDF_PAGE_WIDTH - (PDF_MARGIN * 2f)

private val COLOR_NAVY = Color.rgb(15, 23, 42)
private val COLOR_BLUE = Color.rgb(37, 99, 235)
private val COLOR_BLUE_LIGHT = Color.rgb(239, 246, 255)
private val COLOR_SLATE = Color.rgb(71, 85, 105)
private val COLOR_MUTED = Color.rgb(100, 116, 139)
private val COLOR_BORDER = Color.rgb(203, 213, 225)
private val COLOR_SURFACE = Color.rgb(248, 250, 252)
private val COLOR_GREEN = Color.rgb(21, 128, 61)
private val COLOR_GREEN_LIGHT = Color.rgb(240, 253, 244)
private val COLOR_AMBER = Color.rgb(180, 83, 9)
private val COLOR_AMBER_LIGHT = Color.rgb(255, 251, 235)
private val COLOR_RED = Color.rgb(185, 28, 28)
private val COLOR_RED_LIGHT = Color.rgb(254, 242, 242)

fun generarYCompartirPdfCotizacion(
    context: Context,
    cotizacionCompleta: CotizacionCompleta
): Result<File> = runCatching {
    val cotizacion = cotizacionCompleta.cotizacion
    val cliente = cotizacionCompleta.cliente

    val carpeta = File(context.filesDir, "cotizaciones_pdf").apply {
        if (!exists() && !mkdirs()) {
            error("No fue posible crear la carpeta de cotizaciones.")
        }
    }

    val folioSeguro = cotizacion.folio
        .ifBlank { "cotizacion_${cotizacion.id}" }
        .replace(Regex("[^A-Za-z0-9_-]"), "_")

    val archivoPdf = File(
        carpeta,
        "ARCSHIFT_WELDING_COTIZACION_$folioSeguro.pdf"
    )

    val documento = PdfDocument()
    val escritor = CotizacionPdfWriter(
        documento = documento,
        folio = cotizacion.folio.ifBlank { "Sin folio" }
    )

    try {
        escritor.cabecera(
            estado = cotizacion.estado.ifBlank { "Pendiente" }
        )

        escritor.tarjetaDatosCotizacion(
            fechaCreacion = cotizacion.fecha,
            vigencia = cotizacion.vigencia.ifBlank { "No especificada" },
            fechaAprobacion = cotizacion.fechaAprobacion.ifBlank { "No aplica" }
        )

        escritor.seccion("Cliente")
        escritor.tarjetaCliente(
            nombre = cliente?.nombre?.ifBlank { "Cliente no especificado" }
                ?: "Cliente no especificado",
            contacto = cliente?.personaContacto?.ifBlank { "No especificado" }
                ?: "No especificado",
            telefono = cliente?.telefono?.ifBlank { "No especificado" }
                ?: "No especificado",
            correo = cliente?.correo?.ifBlank { "No especificado" }
                ?: "No especificado"
        )

        escritor.seccion("Trabajo cotizado")
        escritor.tarjetaTrabajo(
            proyecto = cotizacion.proyecto.ifBlank { "Sin proyecto relacionado" },
            descripcion = cotizacion.descripcionTrabajo.ifBlank {
                "Sin descripción del trabajo."
            }
        )

        escritor.seccion("Conceptos")
        escritor.encabezadoConceptos()

        if (cotizacionCompleta.detalles.isEmpty()) {
            escritor.filaSinConceptos()
        } else {
            cotizacionCompleta.detalles.forEachIndexed { indice, detalle ->
                escritor.concepto(
                    indice = indice,
                    descripcion = detalle.descripcion,
                    cantidad = detalle.cantidad,
                    unidad = detalle.unidad,
                    precioUnitario = detalle.precioUnitario,
                    importe = detalle.total
                )
            }
        }

        escritor.bloqueCierre(
            subtotal = cotizacion.subtotal,
            descuento = cotizacion.descuento,
            descuentoPorcentaje = cotizacion.descuentoPorcentaje,
            iva = cotizacion.iva,
            ivaPorcentaje = cotizacion.ivaPorcentaje,
            total = cotizacion.total,
            anticipo = cotizacion.anticipo,
            anticipoPorcentaje = cotizacion.anticipoPorcentaje,
            saldo = cotizacion.saldo,
            observaciones = cotizacion.observaciones.ifBlank {
                "Sin observaciones registradas."
            },
            vigencia = cotizacion.vigencia.ifBlank { "No especificada" },
            fechaActualizacion = cotizacion.fechaActualizacion.ifBlank {
                cotizacion.fecha
            }
        )

        escritor.cerrar()

        FileOutputStream(archivoPdf).use { salida ->
            documento.writeTo(salida)
        }
    } finally {
        documento.close()
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivoPdf
    )

    val compartir = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(
            Intent.EXTRA_SUBJECT,
            "Cotización ${cotizacion.folio} - ARCSHIFT WELDING"
        )
        putExtra(
            Intent.EXTRA_TEXT,
            "Se adjunta la cotización ${cotizacion.folio} de ARCSHIFT WELDING."
        )
        clipData = ClipData.newRawUri("Cotización", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(compartir, "Compartir cotización")
    )

    archivoPdf
}

private class CotizacionPdfWriter(
    private val documento: PdfDocument,
    private val folio: String
) {
    private val pintura = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pagina: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var numeroPagina = 0
    private var y = PDF_MARGIN

    init {
        nuevaPagina()
    }

    fun cabecera(estado: String) {
        val canvas = canvasActual()

        configurarPintura(
            tamano = 10f,
            color = COLOR_NAVY,
            negrita = false
        )
        canvas.drawRect(
            0f,
            0f,
            PDF_PAGE_WIDTH.toFloat(),
            98f,
            pintura
        )

        configurarPintura(
            tamano = 15f,
            color = COLOR_BLUE,
            negrita = true
        )
        canvas.drawCircle(55f, 43f, 20f, pintura)

        configurarPintura(
            tamano = 13f,
            color = Color.WHITE,
            negrita = true,
            alineacion = Paint.Align.CENTER
        )
        canvas.drawText("AW", 55f, 47.5f, pintura)

        configurarPintura(
            tamano = 18f,
            color = Color.WHITE,
            negrita = true
        )
        canvas.drawText("ARCSHIFT WELDING", 86f, 40f, pintura)

        configurarPintura(
            tamano = 8f,
            color = Color.rgb(191, 219, 254),
            negrita = false
        )
        canvas.drawText("FABRICACIÓN Y SERVICIOS DE SOLDADURA", 86f, 57f, pintura)

        configurarPintura(
            tamano = 15f,
            color = Color.WHITE,
            negrita = true,
            alineacion = Paint.Align.RIGHT
        )
        canvas.drawText(
            "COTIZACIÓN",
            PDF_PAGE_WIDTH - PDF_MARGIN,
            36f,
            pintura
        )

        configurarPintura(
            tamano = 11f,
            color = Color.rgb(219, 234, 254),
            negrita = true,
            alineacion = Paint.Align.RIGHT
        )
        canvas.drawText(
            folio,
            PDF_PAGE_WIDTH - PDF_MARGIN,
            54f,
            pintura
        )

        val estadoNormalizado = estado.trim().lowercase(Locale("es", "MX"))
        val colorEstado = when {
            estadoNormalizado.contains("aprobad") -> COLOR_GREEN
            estadoNormalizado.contains("rechaz") || estadoNormalizado.contains("cancel") -> COLOR_RED
            else -> COLOR_AMBER
        }
        val fondoEstado = when {
            estadoNormalizado.contains("aprobad") -> COLOR_GREEN_LIGHT
            estadoNormalizado.contains("rechaz") || estadoNormalizado.contains("cancel") -> COLOR_RED_LIGHT
            else -> COLOR_AMBER_LIGHT
        }

        configurarPintura(8f, fondoEstado, negrita = true)
        val anchoEstado = maxOf(76f, pintura.measureText(estado.uppercase()) + 24f)
        val izquierdaEstado = PDF_PAGE_WIDTH - PDF_MARGIN - anchoEstado
        canvas.drawRoundRect(
            RectF(
                izquierdaEstado,
                66f,
                PDF_PAGE_WIDTH - PDF_MARGIN,
                87f
            ),
            11f,
            11f,
            pintura
        )

        configurarPintura(
            tamano = 8f,
            color = colorEstado,
            negrita = true,
            alineacion = Paint.Align.CENTER
        )
        canvas.drawText(
            estado.uppercase(),
            izquierdaEstado + (anchoEstado / 2f),
            80f,
            pintura
        )

        y = 112f
    }

    fun tarjetaDatosCotizacion(
        fechaCreacion: String,
        vigencia: String,
        fechaAprobacion: String
    ) {
        val alto = 52f
        asegurarEspacio(alto + 6f)
        val top = y
        dibujarTarjeta(top, alto, COLOR_SURFACE)

        val anchoColumna = PDF_CONTENT_WIDTH / 3f
        dibujarDatoCompacto(
            etiqueta = "FECHA DE EMISIÓN",
            valor = fechaCreacion.ifBlank { "No especificada" },
            x = PDF_MARGIN + 16f,
            top = top + 13f,
            ancho = anchoColumna - 24f
        )
        dibujarDatoCompacto(
            etiqueta = "VÁLIDA HASTA",
            valor = vigencia,
            x = PDF_MARGIN + anchoColumna + 10f,
            top = top + 13f,
            ancho = anchoColumna - 24f
        )
        dibujarDatoCompacto(
            etiqueta = "FECHA DE APROBACIÓN",
            valor = fechaAprobacion,
            x = PDF_MARGIN + (anchoColumna * 2f) + 4f,
            top = top + 13f,
            ancho = anchoColumna - 20f
        )

        configurarPintura(1f, COLOR_BORDER, negrita = false)
        pintura.strokeWidth = 0.7f
        canvasActual().drawLine(
            PDF_MARGIN + anchoColumna,
            top + 9f,
            PDF_MARGIN + anchoColumna,
            top + alto - 9f,
            pintura
        )
        canvasActual().drawLine(
            PDF_MARGIN + (anchoColumna * 2f),
            top + 9f,
            PDF_MARGIN + (anchoColumna * 2f),
            top + alto - 9f,
            pintura
        )

        y += alto + 6f
    }

    fun seccion(texto: String) {
        asegurarEspacio(24f)
        y += 4f

        configurarPintura(10f, COLOR_BLUE, negrita = true)
        canvasActual().drawRoundRect(
            RectF(PDF_MARGIN, y - 9f, PDF_MARGIN + 4f, y + 2f),
            2f,
            2f,
            pintura
        )

        configurarPintura(11f, COLOR_NAVY, negrita = true)
        canvasActual().drawText(texto, PDF_MARGIN + 12f, y, pintura)
        y += 6f

        configurarPintura(1f, COLOR_BORDER, negrita = false)
        pintura.strokeWidth = 0.7f
        canvasActual().drawLine(
            PDF_MARGIN,
            y,
            PDF_PAGE_WIDTH - PDF_MARGIN,
            y,
            pintura
        )
        y += 10f
    }

    fun tarjetaCliente(
        nombre: String,
        contacto: String,
        telefono: String,
        correo: String
    ) {
        configurarPintura(9f, COLOR_NAVY, negrita = false)
        val anchoValor = (PDF_CONTENT_WIDTH / 2f) - 34f
        val nombreLineas = envolverTexto(nombre, anchoValor)
        val contactoLineas = envolverTexto(contacto, anchoValor)
        val telefonoLineas = envolverTexto(telefono, anchoValor)
        val correoLineas = envolverTexto(correo, anchoValor)

        val altoFila1 = maxOf(nombreLineas.size, contactoLineas.size) * 11f + 20f
        val altoFila2 = maxOf(telefonoLineas.size, correoLineas.size) * 11f + 20f
        val alto = altoFila1 + altoFila2 + 12f

        asegurarEspacio(alto)
        val top = y
        dibujarTarjeta(top, alto, Color.WHITE)

        val mitad = PDF_MARGIN + (PDF_CONTENT_WIDTH / 2f)
        dibujarCampoTarjeta(
            etiqueta = "CLIENTE",
            lineas = nombreLineas,
            x = PDF_MARGIN + 16f,
            top = top + 13f,
            ancho = anchoValor
        )
        dibujarCampoTarjeta(
            etiqueta = "CONTACTO",
            lineas = contactoLineas,
            x = mitad + 12f,
            top = top + 13f,
            ancho = anchoValor
        )
        dibujarCampoTarjeta(
            etiqueta = "TELÉFONO",
            lineas = telefonoLineas,
            x = PDF_MARGIN + 16f,
            top = top + altoFila1 + 6f,
            ancho = anchoValor
        )
        dibujarCampoTarjeta(
            etiqueta = "CORREO",
            lineas = correoLineas,
            x = mitad + 12f,
            top = top + altoFila1 + 6f,
            ancho = anchoValor
        )

        configurarPintura(1f, COLOR_BORDER, negrita = false)
        pintura.strokeWidth = 0.7f
        canvasActual().drawLine(
            mitad,
            top + 9f,
            mitad,
            top + alto - 9f,
            pintura
        )
        canvasActual().drawLine(
            PDF_MARGIN + 12f,
            top + altoFila1,
            PDF_PAGE_WIDTH - PDF_MARGIN - 12f,
            top + altoFila1,
            pintura
        )

        y += alto + 4f
    }

    fun tarjetaTrabajo(
        proyecto: String,
        descripcion: String
    ) {
        configurarPintura(9f, COLOR_NAVY, negrita = false)
        val ancho = PDF_CONTENT_WIDTH - 32f
        val proyectoLineas = envolverTexto(proyecto, ancho)
        val descripcionLineas = envolverTexto(descripcion, ancho)
        val alto = 22f +
                (proyectoLineas.size * 11f) +
                18f +
                (descripcionLineas.size * 11f) +
                12f

        asegurarEspacio(alto)
        val top = y
        dibujarTarjeta(top, alto, Color.WHITE)

        dibujarCampoTarjeta(
            etiqueta = "PROYECTO",
            lineas = proyectoLineas,
            x = PDF_MARGIN + 16f,
            top = top + 13f,
            ancho = ancho
        )

        val topDescripcion = top + 32f + (proyectoLineas.size * 11f)
        dibujarCampoTarjeta(
            etiqueta = "DESCRIPCIÓN DEL TRABAJO",
            lineas = descripcionLineas,
            x = PDF_MARGIN + 16f,
            top = topDescripcion,
            ancho = ancho
        )

        y += alto + 4f
    }

    fun encabezadoConceptos() {
        asegurarEspacio(26f)
        configurarPintura(8f, COLOR_BLUE, negrita = true)
        canvasActual().drawRoundRect(
            RectF(
                PDF_MARGIN,
                y,
                PDF_PAGE_WIDTH - PDF_MARGIN,
                y + 23f
            ),
            5f,
            5f,
            pintura
        )

        configurarPintura(7.5f, Color.WHITE, negrita = true)
        canvasActual().drawText("DESCRIPCIÓN", PDF_MARGIN + 10f, y + 15f, pintura)
        canvasActual().drawText("CANT.", 337f, y + 15f, pintura)
        canvasActual().drawText("UNIDAD", 386f, y + 15f, pintura)
        dibujarTextoDerecha("P. UNITARIO", 503f, y + 15f)
        dibujarTextoDerecha("IMPORTE", PDF_PAGE_WIDTH - PDF_MARGIN - 9f, y + 15f)
        y += 27f
    }

    fun concepto(
        indice: Int,
        descripcion: String,
        cantidad: Double,
        unidad: String,
        precioUnitario: Double,
        importe: Double
    ) {
        configurarPintura(8f, COLOR_NAVY, negrita = false)
        val lineas = envolverTexto(descripcion.ifBlank { "Sin descripción" }, 275f)
        val alto = maxOf(26f, (lineas.size * 11f) + 13f)

        if (!hayEspacio(alto)) {
            nuevaPagina()
            seccion("Conceptos (continuación)")
            encabezadoConceptos()
        }

        val top = y
        if (indice % 2 != 0) {
            configurarPintura(8f, COLOR_SURFACE, negrita = false)
            canvasActual().drawRect(
                PDF_MARGIN,
                top,
                PDF_PAGE_WIDTH - PDF_MARGIN,
                top + alto,
                pintura
            )
        }

        configurarPintura(8f, COLOR_NAVY, negrita = false)
        var yTexto = top + 15f
        lineas.forEach { linea ->
            canvasActual().drawText(linea, PDF_MARGIN + 10f, yTexto, pintura)
            yTexto += 11f
        }

        canvasActual().drawText(
            formatearNumeroPdf(cantidad),
            337f,
            top + 15f,
            pintura
        )
        canvasActual().drawText(
            unidad.ifBlank { "-" }.take(11),
            386f,
            top + 15f,
            pintura
        )
        dibujarTextoDerecha(
            formatearMonedaPdf(precioUnitario),
            503f,
            top + 15f
        )
        dibujarTextoDerecha(
            formatearMonedaPdf(importe),
            PDF_PAGE_WIDTH - PDF_MARGIN - 9f,
            top + 15f,
            negrita = true
        )

        configurarPintura(1f, Color.rgb(226, 232, 240), negrita = false)
        pintura.strokeWidth = 0.6f
        canvasActual().drawLine(
            PDF_MARGIN,
            top + alto,
            PDF_PAGE_WIDTH - PDF_MARGIN,
            top + alto,
            pintura
        )

        y += alto
    }

    fun filaSinConceptos() {
        asegurarEspacio(34f)
        configurarPintura(8.5f, COLOR_MUTED, negrita = false)
        canvasActual().drawText(
            "No hay conceptos registrados.",
            PDF_MARGIN + 10f,
            y + 17f,
            pintura
        )
        y += 30f
    }

    fun bloqueCierre(
        subtotal: Double,
        descuento: Double,
        descuentoPorcentaje: Double,
        iva: Double,
        ivaPorcentaje: Double,
        total: Double,
        anticipo: Double,
        anticipoPorcentaje: Double,
        saldo: Double,
        observaciones: String,
        vigencia: String,
        fechaActualizacion: String
    ) {
        val separacion = 12f
        val anchoResumen = 272f
        val anchoIzquierdo = PDF_CONTENT_WIDTH - anchoResumen - separacion

        configurarPintura(8.5f, COLOR_NAVY, negrita = false)
        val lineasObservaciones = envolverTexto(
            observaciones,
            anchoIzquierdo - 28f
        )

        // Para observaciones extensas se conserva el flujo vertical y paginado.
        if (lineasObservaciones.size > 11) {
            resumenFinanciero(
                subtotal = subtotal,
                descuento = descuento,
                descuentoPorcentaje = descuentoPorcentaje,
                iva = iva,
                ivaPorcentaje = ivaPorcentaje,
                total = total,
                anticipo = anticipo,
                anticipoPorcentaje = anticipoPorcentaje,
                saldo = saldo
            )
            seccion("Observaciones")
            tarjetaTexto(observaciones)
            notaVigencia(vigencia)
            datoGeneracion(fechaActualizacion)
            return
        }

        val altoResumen = calcularAltoResumen(descuento)
        val altoObservaciones = maxOf(
            58f,
            34f + (lineasObservaciones.size * 12f)
        )
        val altoVigencia = 43f
        val altoIzquierdo = altoObservaciones + 8f + altoVigencia
        val altoBloque = maxOf(altoResumen, altoIzquierdo)

        asegurarEspacio(altoBloque + 38f)
        y += 10f
        val top = y
        val leftResumen = PDF_MARGIN + anchoIzquierdo + separacion

        dibujarTarjetaPersonalizada(
            left = PDF_MARGIN,
            top = top,
            width = anchoIzquierdo,
            height = altoObservaciones,
            fondo = Color.WHITE
        )

        configurarPintura(9.5f, COLOR_NAVY, negrita = true)
        canvasActual().drawText(
            "Observaciones",
            PDF_MARGIN + 14f,
            top + 20f,
            pintura
        )

        configurarPintura(8.5f, COLOR_NAVY, negrita = false)
        var yTexto = top + 39f
        lineasObservaciones.forEach { linea ->
            canvasActual().drawText(
                linea,
                PDF_MARGIN + 14f,
                yTexto,
                pintura
            )
            yTexto += 12f
        }

        val topVigencia = top + altoObservaciones + 8f
        configurarPintura(8f, COLOR_BLUE_LIGHT, negrita = false)
        canvasActual().drawRoundRect(
            RectF(
                PDF_MARGIN,
                topVigencia,
                PDF_MARGIN + anchoIzquierdo,
                topVigencia + altoVigencia
            ),
            7f,
            7f,
            pintura
        )

        configurarPintura(8.5f, COLOR_BLUE, negrita = true)
        canvasActual().drawText(
            "Vigencia de la propuesta",
            PDF_MARGIN + 14f,
            topVigencia + 16f,
            pintura
        )

        configurarPintura(7.5f, COLOR_SLATE, negrita = false)
        val textoVigencia = "Válida hasta: $vigencia"
        envolverTexto(textoVigencia, anchoIzquierdo - 28f)
            .take(2)
            .forEachIndexed { indice, linea ->
                canvasActual().drawText(
                    linea,
                    PDF_MARGIN + 14f,
                    topVigencia + 30f + (indice * 10f),
                    pintura
                )
            }

        dibujarResumenFinanciero(
            left = leftResumen,
            top = top,
            ancho = anchoResumen,
            subtotal = subtotal,
            descuento = descuento,
            descuentoPorcentaje = descuentoPorcentaje,
            iva = iva,
            ivaPorcentaje = ivaPorcentaje,
            total = total,
            anticipo = anticipo,
            anticipoPorcentaje = anticipoPorcentaje,
            saldo = saldo
        )

        y = top + altoBloque + 4f
        datoGeneracion(fechaActualizacion)
    }

    fun resumenFinanciero(
        subtotal: Double,
        descuento: Double,
        descuentoPorcentaje: Double,
        iva: Double,
        ivaPorcentaje: Double,
        total: Double,
        anticipo: Double,
        anticipoPorcentaje: Double,
        saldo: Double
    ) {
        val alto = calcularAltoResumen(descuento)
        asegurarEspacio(alto + 12f)

        y += 10f
        val ancho = 275f
        val left = PDF_PAGE_WIDTH - PDF_MARGIN - ancho
        val top = y

        dibujarResumenFinanciero(
            left = left,
            top = top,
            ancho = ancho,
            subtotal = subtotal,
            descuento = descuento,
            descuentoPorcentaje = descuentoPorcentaje,
            iva = iva,
            ivaPorcentaje = ivaPorcentaje,
            total = total,
            anticipo = anticipo,
            anticipoPorcentaje = anticipoPorcentaje,
            saldo = saldo
        )

        y += alto + 4f
    }

    fun tarjetaTexto(texto: String) {
        configurarPintura(8.5f, COLOR_NAVY, negrita = false)
        val lineas = envolverTexto(texto, PDF_CONTENT_WIDTH - 32f)
        val alto = maxOf(42f, (lineas.size * 12f) + 24f)
        asegurarEspacio(alto)

        val top = y
        dibujarTarjeta(top, alto, Color.WHITE)
        configurarPintura(8.5f, COLOR_NAVY, negrita = false)
        var yTexto = top + 18f
        lineas.forEach { linea ->
            canvasActual().drawText(linea, PDF_MARGIN + 16f, yTexto, pintura)
            yTexto += 12f
        }
        y += alto + 4f
    }

    fun notaVigencia(vigencia: String) {
        asegurarEspacio(47f)
        y += 5f
        val top = y
        configurarPintura(8f, COLOR_BLUE_LIGHT, negrita = false)
        canvasActual().drawRoundRect(
            RectF(
                PDF_MARGIN,
                top,
                PDF_PAGE_WIDTH - PDF_MARGIN,
                top + 36f
            ),
            6f,
            6f,
            pintura
        )

        configurarPintura(9f, COLOR_BLUE, negrita = true)
        canvasActual().drawText(
            "Vigencia de la propuesta",
            PDF_MARGIN + 15f,
            top + 15f,
            pintura
        )

        configurarPintura(8f, COLOR_SLATE, negrita = false)
        canvasActual().drawText(
            "Los precios y condiciones indicados son válidos hasta: $vigencia.",
            PDF_MARGIN + 15f,
            top + 28f,
            pintura
        )
        y += 41f
    }

    fun datoGeneracion(fechaActualizacion: String) {
        asegurarEspacio(27f)
        y += 5f
        configurarPintura(7f, COLOR_MUTED, negrita = false)
        canvasActual().drawText(
            "Última actualización: $fechaActualizacion",
            PDF_MARGIN,
            y,
            pintura
        )
        y += 10f
        canvasActual().drawText(
            "Documento generado el ${fechaHoraGeneracionPdf()}",
            PDF_MARGIN,
            y,
            pintura
        )
        y += 6f
    }

    fun cerrar() {
        pagina?.let { paginaActual ->
            dibujarPiePagina(canvasActual(), numeroPagina)
            documento.finishPage(paginaActual)
        }
        pagina = null
        canvas = null
    }

    private fun nuevaPagina() {
        pagina?.let { paginaAnterior ->
            dibujarPiePagina(canvasActual(), numeroPagina)
            documento.finishPage(paginaAnterior)
        }

        numeroPagina += 1
        pagina = documento.startPage(
            PdfDocument.PageInfo.Builder(
                PDF_PAGE_WIDTH,
                PDF_PAGE_HEIGHT,
                numeroPagina
            ).create()
        )
        canvas = pagina?.canvas
        y = PDF_MARGIN

        if (numeroPagina > 1) {
            dibujarEncabezadoContinuacion()
        }
    }

    private fun dibujarEncabezadoContinuacion() {
        configurarPintura(9f, COLOR_NAVY, negrita = false)
        canvasActual().drawRect(
            0f,
            0f,
            PDF_PAGE_WIDTH.toFloat(),
            58f,
            pintura
        )

        configurarPintura(11f, Color.WHITE, negrita = true)
        canvasActual().drawText(
            "ARCSHIFT WELDING",
            PDF_MARGIN,
            34f,
            pintura
        )

        configurarPintura(
            9f,
            Color.rgb(191, 219, 254),
            negrita = true,
            alineacion = Paint.Align.RIGHT
        )
        canvasActual().drawText(
            "COTIZACIÓN $folio",
            PDF_PAGE_WIDTH - PDF_MARGIN,
            34f,
            pintura
        )
        y = 68f
    }

    private fun dibujarTarjeta(top: Float, alto: Float, fondo: Int) {
        dibujarTarjetaPersonalizada(
            left = PDF_MARGIN,
            top = top,
            width = PDF_CONTENT_WIDTH,
            height = alto,
            fondo = fondo
        )
    }

    private fun dibujarTarjetaPersonalizada(
        left: Float,
        top: Float,
        width: Float,
        height: Float,
        fondo: Int
    ) {
        configurarPintura(1f, fondo, negrita = false)
        pintura.style = Paint.Style.FILL
        canvasActual().drawRoundRect(
            RectF(left, top, left + width, top + height),
            7f,
            7f,
            pintura
        )

        configurarPintura(1f, COLOR_BORDER, negrita = false)
        pintura.style = Paint.Style.STROKE
        pintura.strokeWidth = 0.8f
        canvasActual().drawRoundRect(
            RectF(left, top, left + width, top + height),
            7f,
            7f,
            pintura
        )
        pintura.style = Paint.Style.FILL
    }

    private fun dibujarDatoCompacto(
        etiqueta: String,
        valor: String,
        x: Float,
        top: Float,
        ancho: Float
    ) {
        configurarPintura(7f, COLOR_MUTED, negrita = true)
        canvasActual().drawText(etiqueta, x, top, pintura)

        configurarPintura(9f, COLOR_NAVY, negrita = true)
        val lineas = envolverTexto(valor, ancho).take(2)
        var yTexto = top + 15f
        lineas.forEach { linea ->
            canvasActual().drawText(linea, x, yTexto, pintura)
            yTexto += 11f
        }
    }

    private fun dibujarCampoTarjeta(
        etiqueta: String,
        lineas: List<String>,
        x: Float,
        top: Float,
        ancho: Float
    ) {
        configurarPintura(7f, COLOR_MUTED, negrita = true)
        canvasActual().drawText(etiqueta, x, top, pintura)

        configurarPintura(9f, COLOR_NAVY, negrita = false)
        var yTexto = top + 14f
        lineas.forEach { linea ->
            canvasActual().drawText(linea, x, yTexto, pintura)
            yTexto += 11f
        }
    }

    private fun calcularAltoResumen(descuento: Double): Float {
        val filas = 5 + if (descuento > 0.0) 1 else 0
        return 34f + (filas * 15f) + 25f
    }

    private fun dibujarResumenFinanciero(
        left: Float,
        top: Float,
        ancho: Float,
        subtotal: Double,
        descuento: Double,
        descuentoPorcentaje: Double,
        iva: Double,
        ivaPorcentaje: Double,
        total: Double,
        anticipo: Double,
        anticipoPorcentaje: Double,
        saldo: Double
    ) {
        val alto = calcularAltoResumen(descuento)

        dibujarTarjetaPersonalizada(
            left = left,
            top = top,
            width = ancho,
            height = alto,
            fondo = COLOR_SURFACE
        )

        configurarPintura(10f, COLOR_NAVY, negrita = true)
        canvasActual().drawText(
            "Resumen financiero",
            left + 16f,
            top + 21f,
            pintura
        )

        var filaY = top + 40f
        filaResumen("Subtotal", subtotal, left, ancho, filaY)
        filaY += 15f

        if (descuento > 0.0) {
            filaResumen(
                "Descuento (${formatearNumeroPdf(descuentoPorcentaje)}%)",
                -descuento,
                left,
                ancho,
                filaY
            )
            filaY += 15f
        }

        filaResumen(
            "IVA (${formatearNumeroPdf(ivaPorcentaje)}%)",
            iva,
            left,
            ancho,
            filaY
        )
        filaY += 15f

        configurarPintura(1f, COLOR_BORDER, negrita = false)
        pintura.strokeWidth = 0.8f
        canvasActual().drawLine(
            left + 16f,
            filaY - 5f,
            left + ancho - 16f,
            filaY - 5f,
            pintura
        )

        filaResumen(
            etiqueta = "TOTAL",
            importe = total,
            left = left,
            ancho = ancho,
            yFila = filaY + 6f,
            destacado = true
        )
        filaY += 27f

        filaResumen(
            "Anticipo sugerido (${formatearNumeroPdf(anticipoPorcentaje)}%)",
            anticipo,
            left,
            ancho,
            filaY
        )
        filaY += 15f
        filaResumen(
            "Saldo restante",
            saldo,
            left,
            ancho,
            filaY,
            destacadoSecundario = true
        )
    }

    private fun filaResumen(
        etiqueta: String,
        importe: Double,
        left: Float,
        ancho: Float,
        yFila: Float,
        destacado: Boolean = false,
        destacadoSecundario: Boolean = false
    ) {
        val color = when {
            destacado -> COLOR_GREEN
            destacadoSecundario -> COLOR_BLUE
            else -> COLOR_NAVY
        }
        val tamano = if (destacado) 11f else 8f

        configurarPintura(
            tamano = tamano,
            color = color,
            negrita = destacado || destacadoSecundario
        )
        canvasActual().drawText(etiqueta, left + 16f, yFila, pintura)

        dibujarTextoDerecha(
            texto = formatearMonedaPdf(importe),
            x = left + ancho - 16f,
            yTexto = yFila,
            color = color,
            negrita = destacado || destacadoSecundario,
            tamano = tamano
        )
    }

    private fun dibujarTextoDerecha(
        texto: String,
        x: Float,
        yTexto: Float,
        color: Int = COLOR_NAVY,
        negrita: Boolean = false,
        tamano: Float = 8f
    ) {
        configurarPintura(
            tamano = tamano,
            color = color,
            negrita = negrita,
            alineacion = Paint.Align.RIGHT
        )
        canvasActual().drawText(texto, x, yTexto, pintura)
        pintura.textAlign = Paint.Align.LEFT
    }

    private fun asegurarEspacio(altura: Float) {
        if (!hayEspacio(altura)) {
            nuevaPagina()
        }
    }

    private fun hayEspacio(altura: Float): Boolean {
        return y + altura <= PDF_PAGE_HEIGHT - 58f
    }

    private fun envolverTexto(
        texto: String,
        anchoMaximo: Float
    ): List<String> {
        if (texto.isBlank()) return listOf("-")

        val resultado = mutableListOf<String>()
        texto.split('\n').forEach { parrafo ->
            val palabras = parrafo.trim().split(Regex("\\s+"))
            var linea = ""

            palabras.forEach { palabra ->
                val candidata = if (linea.isBlank()) palabra else "$linea $palabra"
                if (
                    pintura.measureText(candidata) <= anchoMaximo ||
                    linea.isBlank()
                ) {
                    linea = candidata
                } else {
                    resultado += linea
                    linea = palabra
                }
            }

            if (linea.isNotBlank()) {
                resultado += linea
            }
        }

        return resultado.ifEmpty { listOf("-") }
    }

    private fun configurarPintura(
        tamano: Float,
        color: Int,
        negrita: Boolean,
        alineacion: Paint.Align = Paint.Align.LEFT
    ) {
        pintura.textSize = tamano
        pintura.color = color
        pintura.isFakeBoldText = negrita
        pintura.style = Paint.Style.FILL
        pintura.textAlign = alineacion
        pintura.strokeWidth = 1f
    }

    private fun canvasActual(): Canvas {
        return requireNotNull(canvas) {
            "La página PDF no está disponible."
        }
    }

    private fun dibujarPiePagina(canvas: Canvas, numero: Int) {
        configurarPintura(1f, COLOR_BORDER, negrita = false)
        pintura.strokeWidth = 0.7f
        canvas.drawLine(
            PDF_MARGIN,
            PDF_PAGE_HEIGHT - 44f,
            PDF_PAGE_WIDTH - PDF_MARGIN,
            PDF_PAGE_HEIGHT - 44f,
            pintura
        )

        configurarPintura(7f, COLOR_MUTED, negrita = false)
        canvas.drawText(
            "ARCSHIFT WELDING · Cotización $folio",
            PDF_MARGIN,
            PDF_PAGE_HEIGHT - 27f,
            pintura
        )

        configurarPintura(
            7f,
            COLOR_MUTED,
            negrita = false,
            alineacion = Paint.Align.RIGHT
        )
        canvas.drawText(
            "Página $numero",
            PDF_PAGE_WIDTH - PDF_MARGIN,
            PDF_PAGE_HEIGHT - 27f,
            pintura
        )
        pintura.textAlign = Paint.Align.LEFT
    }
}

private fun formatearMonedaPdf(valor: Double): String {
    return NumberFormat
        .getCurrencyInstance(Locale("es", "MX"))
        .format(valor)
}

private fun formatearNumeroPdf(valor: Double): String {
    return if (valor % 1.0 == 0.0) {
        valor.toInt().toString()
    } else {
        "%.2f".format(Locale("es", "MX"), valor)
    }
}

private fun fechaHoraGeneracionPdf(): String {
    return SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale("es", "MX")
    ).format(Date())
}
