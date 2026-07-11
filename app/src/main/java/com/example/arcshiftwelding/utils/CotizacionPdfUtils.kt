package com.example.arcshiftwelding.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
private const val PDF_MARGIN = 42f

fun generarYCompartirPdfCotizacion(
    context: Context,
    cotizacionCompleta: CotizacionCompleta
): Result<File> = runCatching {
    val cotizacion = cotizacionCompleta.cotizacion
    val cliente = cotizacionCompleta.cliente
    val archivos = deserializarComprobantes(cotizacion.archivosAdjuntosJson)

    val carpeta = File(context.filesDir, "cotizaciones_pdf").apply {
        if (!exists() && !mkdirs()) {
            error("No fue posible crear la carpeta de cotizaciones.")
        }
    }

    val nombreSeguro = cotizacion.folio
        .ifBlank { "cotizacion_${cotizacion.id}" }
        .replace(Regex("[^A-Za-z0-9_-]"), "_")

    val archivoPdf = File(carpeta, "$nombreSeguro.pdf")
    val documento = PdfDocument()
    val escritor = CotizacionPdfWriter(documento)

    try {
        escritor.titulo("ARCSHIFT WELDING")
        escritor.subtitulo("COTIZACIÓN")
        escritor.espacio(7f)

        escritor.seccion("Datos de la cotización")
        escritor.dato("Folio", cotizacion.folio.ifBlank { "Sin folio" })
        escritor.dato("Estado", cotizacion.estado)
        escritor.dato("Fecha de creación", cotizacion.fecha)
        escritor.dato("Vigencia", cotizacion.vigencia.ifBlank { "No especificada" })

        if (cotizacion.fechaAprobacion.isNotBlank()) {
            escritor.dato("Fecha de aprobación", cotizacion.fechaAprobacion)
        }

        escritor.dato(
            "Última actualización",
            cotizacion.fechaActualizacion.ifBlank { cotizacion.fecha }
        )

        escritor.seccion("Cliente")
        escritor.dato("Nombre", cliente?.nombre ?: "Cliente no encontrado")
        escritor.dato(
            "Contacto",
            cliente?.personaContacto?.ifBlank { "No especificado" } ?: "No especificado"
        )
        escritor.dato(
            "Teléfono",
            cliente?.telefono?.ifBlank { "No especificado" } ?: "No especificado"
        )
        escritor.dato(
            "Correo",
            cliente?.correo?.ifBlank { "No especificado" } ?: "No especificado"
        )

        escritor.seccion("Trabajo")
        escritor.dato(
            "Proyecto",
            cotizacion.proyecto.ifBlank { "Sin proyecto relacionado" }
        )
        escritor.textoConEtiqueta(
            "Descripción",
            cotizacion.descripcionTrabajo.ifBlank { "Sin descripción" }
        )

        escritor.seccion("Conceptos cotizados")
        escritor.encabezadoConceptos()

        if (cotizacionCompleta.detalles.isEmpty()) {
            escritor.texto("No hay conceptos registrados.", color = Color.DKGRAY)
        } else {
            cotizacionCompleta.detalles.forEach { detalle ->
                escritor.concepto(
                    descripcion = detalle.descripcion,
                    cantidad = detalle.cantidad,
                    unidad = detalle.unidad,
                    precioUnitario = detalle.precioUnitario,
                    importe = detalle.total
                )
            }
        }

        escritor.seccion("Resumen financiero")
        escritor.monto("Subtotal", cotizacion.subtotal)
        if (cotizacion.descuento > 0.0) {
            escritor.monto(
                "Descuento (${formatearNumeroPdf(cotizacion.descuentoPorcentaje)}%)",
                -cotizacion.descuento
            )
        }
        escritor.monto(
            "IVA (${formatearNumeroPdf(cotizacion.ivaPorcentaje)}%)",
            cotizacion.iva
        )
        escritor.monto("Total", cotizacion.total, destacado = true)
        escritor.monto(
            "Anticipo sugerido (${formatearNumeroPdf(cotizacion.anticipoPorcentaje)}%)",
            cotizacion.anticipo
        )
        escritor.monto("Saldo restante", cotizacion.saldo, destacado = true)

        escritor.seccion("Observaciones")
        escritor.texto(
            cotizacion.observaciones.ifBlank { "Sin observaciones registradas." }
        )

        escritor.seccion("Archivos adjuntos")
        if (archivos.isEmpty()) {
            escritor.texto("No se adjuntaron archivos a la cotización.")
        } else {
            archivos.forEachIndexed { index, adjunto ->
                escritor.texto(
                    "${index + 1}. ${adjunto.nombre} (${adjunto.tipo})",
                    color = Color.DKGRAY
                )
            }
        }

        escritor.pieFinal(
            "Documento generado el ${fechaHoraGeneracionPdf()}"
        )
        escritor.cerrarPagina()

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
        putExtra(Intent.EXTRA_SUBJECT, "Cotización ${cotizacion.folio}")
        putExtra(
            Intent.EXTRA_TEXT,
            "Se adjunta la cotización ${cotizacion.folio} de Arcshift Welding."
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
    private val documento: PdfDocument
) {
    private val pintura = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pagina: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var numeroPagina = 0
    private var y = PDF_MARGIN

    init {
        nuevaPagina()
    }

    fun titulo(texto: String) {
        asegurarEspacio(30f)
        configurarPintura(18f, Color.rgb(15, 23, 42), negrita = true)
        canvasActual().drawText(texto, PDF_MARGIN, y, pintura)
        y += 25f
    }

    fun subtitulo(texto: String) {
        asegurarEspacio(24f)
        configurarPintura(14f, Color.rgb(37, 99, 235), negrita = true)
        canvasActual().drawText(texto, PDF_MARGIN, y, pintura)
        y += 22f
    }

    fun seccion(texto: String) {
        asegurarEspacio(34f)
        y += 8f
        configurarPintura(11f, Color.rgb(15, 23, 42), negrita = true)
        canvasActual().drawText(texto, PDF_MARGIN, y, pintura)
        y += 6f
        pintura.color = Color.rgb(203, 213, 225)
        pintura.strokeWidth = 1f
        canvasActual().drawLine(PDF_MARGIN, y, PDF_PAGE_WIDTH - PDF_MARGIN, y, pintura)
        y += 15f
    }

    fun dato(etiqueta: String, valor: String) {
        asegurarEspacio(19f)
        configurarPintura(9f, Color.rgb(71, 85, 105), negrita = true)
        canvasActual().drawText("$etiqueta:", PDF_MARGIN, y, pintura)

        configurarPintura(9f, Color.rgb(15, 23, 42), negrita = false)
        val inicioValor = PDF_MARGIN + 120f
        val anchoValor = PDF_PAGE_WIDTH - PDF_MARGIN - inicioValor
        val lineas = envolverTexto(valor, anchoValor)
        lineas.forEachIndexed { index, linea ->
            if (index > 0) asegurarEspacio(14f)
            canvasActual().drawText(linea, inicioValor, y, pintura)
            y += 14f
        }
        y += 2f
    }

    fun textoConEtiqueta(etiqueta: String, valor: String) {
        asegurarEspacio(18f)
        configurarPintura(9f, Color.rgb(71, 85, 105), negrita = true)
        canvasActual().drawText("$etiqueta:", PDF_MARGIN, y, pintura)
        y += 14f
        texto(valor)
    }

    fun texto(
        texto: String,
        color: Int = Color.rgb(15, 23, 42)
    ) {
        configurarPintura(9f, color, negrita = false)
        envolverTexto(texto, PDF_PAGE_WIDTH - (PDF_MARGIN * 2)).forEach { linea ->
            asegurarEspacio(14f)
            canvasActual().drawText(linea, PDF_MARGIN, y, pintura)
            y += 14f
        }
        y += 2f
    }

    fun encabezadoConceptos() {
        asegurarEspacio(23f)
        configurarPintura(8f, Color.rgb(71, 85, 105), negrita = true)
        canvasActual().drawText("Concepto", PDF_MARGIN, y, pintura)
        canvasActual().drawText("Cant.", 335f, y, pintura)
        canvasActual().drawText("Unidad", 385f, y, pintura)
        canvasActual().drawText("P. unitario", 445f, y, pintura)
        canvasActual().drawText("Importe", 520f, y, pintura)
        y += 7f
        pintura.color = Color.rgb(226, 232, 240)
        canvasActual().drawLine(PDF_MARGIN, y, PDF_PAGE_WIDTH - PDF_MARGIN, y, pintura)
        y += 12f
    }

    fun concepto(
        descripcion: String,
        cantidad: Double,
        unidad: String,
        precioUnitario: Double,
        importe: Double
    ) {
        configurarPintura(8f, Color.rgb(15, 23, 42), negrita = false)
        val lineas = envolverTexto(descripcion, 280f)
        val alto = maxOf(1, lineas.size) * 12f + 8f
        asegurarEspacio(alto)

        val yInicial = y
        lineas.forEach { linea ->
            canvasActual().drawText(linea, PDF_MARGIN, y, pintura)
            y += 12f
        }

        canvasActual().drawText(formatearNumeroPdf(cantidad), 335f, yInicial, pintura)
        canvasActual().drawText(unidad.take(10), 385f, yInicial, pintura)
        canvasActual().drawText(formatearMonedaPdf(precioUnitario), 445f, yInicial, pintura)
        canvasActual().drawText(formatearMonedaPdf(importe), 520f, yInicial, pintura)
        y = maxOf(y, yInicial + 12f) + 7f
    }

    fun monto(etiqueta: String, importe: Double, destacado: Boolean = false) {
        asegurarEspacio(18f)
        configurarPintura(
            if (destacado) 10f else 9f,
            if (destacado) Color.rgb(21, 128, 61) else Color.rgb(15, 23, 42),
            negrita = destacado
        )
        canvasActual().drawText(etiqueta, PDF_MARGIN, y, pintura)

        val valor = formatearMonedaPdf(importe)
        canvasActual().drawText(
            valor,
            PDF_PAGE_WIDTH - PDF_MARGIN - pintura.measureText(valor),
            y,
            pintura
        )
        y += 16f
    }

    fun espacio(valor: Float) {
        asegurarEspacio(valor)
        y += valor
    }

    fun pieFinal(texto: String) {
        asegurarEspacio(30f)
        y += 10f
        configurarPintura(7f, Color.GRAY, negrita = false)
        canvasActual().drawText(texto, PDF_MARGIN, y, pintura)
        y += 12f
    }

    fun cerrarPagina() {
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
    }

    private fun asegurarEspacio(altura: Float) {
        if (y + altura > PDF_PAGE_HEIGHT - 55f) {
            nuevaPagina()
        }
    }

    private fun envolverTexto(texto: String, anchoMaximo: Float): List<String> {
        if (texto.isBlank()) return listOf("-")

        val resultado = mutableListOf<String>()
        texto.split('\n').forEach { parrafo ->
            val palabras = parrafo.trim().split(Regex("\\s+"))
            var linea = ""

            palabras.forEach { palabra ->
                val candidata = if (linea.isBlank()) palabra else "$linea $palabra"
                if (pintura.measureText(candidata) <= anchoMaximo || linea.isBlank()) {
                    linea = candidata
                } else {
                    resultado += linea
                    linea = palabra
                }
            }

            if (linea.isNotBlank()) resultado += linea
        }

        return resultado.ifEmpty { listOf("-") }
    }

    private fun configurarPintura(
        tamano: Float,
        color: Int,
        negrita: Boolean
    ) {
        pintura.textSize = tamano
        pintura.color = color
        pintura.isFakeBoldText = negrita
        pintura.style = Paint.Style.FILL
    }

    private fun canvasActual(): Canvas {
        return requireNotNull(canvas) { "La página PDF no está disponible." }
    }

    private fun dibujarPiePagina(canvas: Canvas, numero: Int) {
        configurarPintura(7f, Color.GRAY, negrita = false)
        val texto = "Arcshift Welding · Página $numero"
        canvas.drawText(
            texto,
            PDF_PAGE_WIDTH - PDF_MARGIN - pintura.measureText(texto),
            PDF_PAGE_HEIGHT - 25f,
            pintura
        )
    }
}

private fun formatearMonedaPdf(valor: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(valor)
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
