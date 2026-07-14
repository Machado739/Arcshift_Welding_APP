package com.example.arcshiftwelding.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.arcshiftwelding.ui.viewmodel.ReporteDetalleUi
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ANCHO_PAGINA = 595
private const val ALTO_PAGINA = 842
private const val MARGEN = 40f

fun generarReportePdf(
    context: Context,
    titulo: String,
    periodo: String,
    reportes: List<ReporteDetalleUi>
): Result<File> = runCatching {
    require(reportes.isNotEmpty()) { "No hay información para generar el reporte." }

    val archivo = crearArchivoReporte(context, titulo, "pdf")
    val documento = PdfDocument()
    val escritor = EscritorReportePdf(documento)

    try {
        escritor.encabezado(titulo, periodo)

        reportes.forEachIndexed { indice, reporte ->
            if (indice > 0) escritor.separacion(10f)
            escritor.seccion(reporte.titulo)
            escritor.texto(reporte.descripcion)
            escritor.dato(reporte.etiquetaPrincipal, reporte.valorPrincipal, destacado = true)
            escritor.dato(reporte.etiquetaSecundaria, reporte.valorSecundario)
            reporte.metricas.forEach { metrica ->
                escritor.dato(metrica.titulo, metrica.valor)
            }

            if (reporte.registros.isNotEmpty()) {
                escritor.subseccion("Registros")
                val registrosPdf = reporte.registros.take(300)
                if (reporte.registros.size > registrosPdf.size) {
                    escritor.texto(
                        "Se muestran los 300 registros más recientes de ${reporte.registros.size}. " +
                            "El archivo CSV contiene el listado completo."
                    )
                }
                escritor.encabezadoTabla()
                registrosPdf.forEach { registro ->
                    escritor.registro(
                        titulo = registro.titulo,
                        descripcion = registro.descripcion,
                        valor = registro.valor,
                        fecha = registro.fecha,
                        estado = registro.estado
                    )
                }
            }
        }

        escritor.pieFinal("Generado el ${fechaHoraActual()}")
        escritor.cerrar()

        FileOutputStream(archivo).use { salida ->
            documento.writeTo(salida)
        }
    } finally {
        documento.close()
    }

    archivo
}

fun generarReporteCsv(
    context: Context,
    titulo: String,
    periodo: String,
    reportes: List<ReporteDetalleUi>
): Result<File> = runCatching {
    require(reportes.isNotEmpty()) { "No hay información para generar el reporte." }

    val archivo = crearArchivoReporte(context, titulo, "csv")
    val contenido = buildString {
        append('\uFEFF')
        appendLine("Reporte;${csv(titulo)}")
        appendLine("Periodo;${csv(periodo)}")
        appendLine("Generado;${csv(fechaHoraActual())}")
        appendLine()

        reportes.forEach { reporte ->
            appendLine(csv(reporte.titulo))
            appendLine("Indicador;Valor")
            appendLine("${csv(reporte.etiquetaPrincipal)};${csv(reporte.valorPrincipal)}")
            appendLine("${csv(reporte.etiquetaSecundaria)};${csv(reporte.valorSecundario)}")
            reporte.metricas.forEach { metrica ->
                appendLine("${csv(metrica.titulo)};${csv(metrica.valor)}")
            }
            appendLine()
            appendLine("Título;Descripción;Valor;Fecha;Estado")
            reporte.registros.forEach { registro ->
                appendLine(
                    listOf(
                        registro.titulo,
                        registro.descripcion,
                        registro.valor,
                        registro.fecha,
                        registro.estado
                    ).joinToString(";") { csv(it) }
                )
            }
            appendLine()
        }
    }

    archivo.writeText(contenido, Charsets.UTF_8)
    archivo
}

private fun crearArchivoReporte(
    context: Context,
    titulo: String,
    extension: String
): File {
    val carpeta = File(context.filesDir, "reportes").apply {
        if (!exists() && !mkdirs()) {
            error("No fue posible crear la carpeta de reportes.")
        }
    }
    val nombre = titulo
        .uppercase(Locale.ROOT)
        .replace(Regex("[^A-Z0-9]+"), "_")
        .trim('_')
        .ifBlank { "REPORTE" }
    val marcaTiempo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
    return File(carpeta, "ARCSHIFT_${nombre}_$marcaTiempo.$extension")
}

fun compartirArchivoReporte(
    context: Context,
    archivo: File,
    mimeType: String,
    asunto: String
) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, asunto)
        clipData = ClipData.newRawUri(asunto, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir reporte"))
}

private fun csv(valor: String): String {
    return "\"${valor.replace("\"", "\"\"")}\""
}

private fun fechaHoraActual(): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "MX")).format(Date())
}

private class EscritorReportePdf(
    private val documento: PdfDocument
) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pagina: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var numeroPagina = 0
    private var y = MARGEN

    init {
        nuevaPagina()
    }

    fun encabezado(titulo: String, periodo: String) {
        asegurarEspacio(70f)
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(15, 23, 42)
        canvasActual().drawRoundRect(
            MARGEN,
            y,
            ANCHO_PAGINA - MARGEN,
            y + 58f,
            10f,
            10f,
            paint
        )
        configurar(17f, Color.WHITE, true)
        canvasActual().drawText("ARCSHIFT WELDING", MARGEN + 16f, y + 24f, paint)
        configurar(11f, Color.rgb(191, 219, 254), true)
        canvasActual().drawText(titulo, MARGEN + 16f, y + 44f, paint)
        configurar(9f, Color.WHITE, false)
        val ancho = paint.measureText(periodo)
        canvasActual().drawText(periodo, ANCHO_PAGINA - MARGEN - 16f - ancho, y + 35f, paint)
        y += 72f
    }

    fun seccion(texto: String) {
        asegurarEspacio(28f)
        configurar(13f, Color.rgb(15, 23, 42), true)
        canvasActual().drawText(texto, MARGEN, y, paint)
        y += 7f
        paint.color = Color.rgb(203, 213, 225)
        paint.strokeWidth = 1f
        canvasActual().drawLine(MARGEN, y, ANCHO_PAGINA - MARGEN, y, paint)
        y += 14f
    }

    fun subseccion(texto: String) {
        asegurarEspacio(24f)
        y += 5f
        configurar(10f, Color.rgb(37, 99, 235), true)
        canvasActual().drawText(texto, MARGEN, y, paint)
        y += 16f
    }

    fun texto(texto: String) {
        configurar(9f, Color.rgb(71, 85, 105), false)
        envolver(texto, ANCHO_PAGINA - MARGEN * 2).forEach { linea ->
            asegurarEspacio(13f)
            canvasActual().drawText(linea, MARGEN, y, paint)
            y += 13f
        }
        y += 3f
    }

    fun dato(etiqueta: String, valor: String, destacado: Boolean = false) {
        asegurarEspacio(18f)
        configurar(9f, Color.rgb(71, 85, 105), false)
        canvasActual().drawText(etiqueta, MARGEN, y, paint)
        configurar(
            if (destacado) 11f else 9f,
            if (destacado) Color.rgb(21, 128, 61) else Color.rgb(15, 23, 42),
            true
        )
        val ancho = paint.measureText(valor)
        canvasActual().drawText(valor, ANCHO_PAGINA - MARGEN - ancho, y, paint)
        y += 17f
    }

    fun encabezadoTabla() {
        asegurarEspacio(25f)
        paint.color = Color.rgb(239, 246, 255)
        paint.style = Paint.Style.FILL
        canvasActual().drawRect(MARGEN, y - 12f, ANCHO_PAGINA - MARGEN, y + 9f, paint)
        configurar(8f, Color.rgb(30, 64, 175), true)
        canvasActual().drawText("Descripción", MARGEN + 6f, y, paint)
        canvasActual().drawText("Fecha", 400f, y, paint)
        canvasActual().drawText("Valor", 485f, y, paint)
        y += 16f
    }

    fun registro(
        titulo: String,
        descripcion: String,
        valor: String,
        fecha: String,
        estado: String
    ) {
        configurar(8f, Color.rgb(15, 23, 42), true)
        val lineasTitulo = envolver(titulo, 325f).take(2)
        configurar(7f, Color.rgb(71, 85, 105), false)
        val lineasDescripcion = envolver(
            listOf(descripcion, estado).filter { it.isNotBlank() }.joinToString(" · "),
            325f
        ).take(2)
        val alto = 12f * (lineasTitulo.size + lineasDescripcion.size).coerceAtLeast(2) + 8f
        asegurarEspacio(alto)
        val inicio = y

        configurar(8f, Color.rgb(15, 23, 42), true)
        lineasTitulo.forEach { linea ->
            canvasActual().drawText(linea, MARGEN + 6f, y, paint)
            y += 11f
        }
        configurar(7f, Color.rgb(100, 116, 139), false)
        lineasDescripcion.forEach { linea ->
            canvasActual().drawText(linea, MARGEN + 6f, y, paint)
            y += 10f
        }

        configurar(7f, Color.rgb(71, 85, 105), false)
        canvasActual().drawText(fecha.take(16), 400f, inicio, paint)
        configurar(8f, Color.rgb(37, 99, 235), true)
        val valorCorto = valor.take(18)
        canvasActual().drawText(
            valorCorto,
            ANCHO_PAGINA - MARGEN - paint.measureText(valorCorto),
            inicio,
            paint
        )
        y = maxOf(y, inicio + alto)
        paint.color = Color.rgb(226, 232, 240)
        paint.strokeWidth = 0.7f
        canvasActual().drawLine(MARGEN, y - 4f, ANCHO_PAGINA - MARGEN, y - 4f, paint)
    }

    fun separacion(valor: Float) {
        asegurarEspacio(valor)
        y += valor
    }

    fun pieFinal(texto: String) {
        asegurarEspacio(24f)
        y += 8f
        configurar(7f, Color.GRAY, false)
        canvasActual().drawText(texto, MARGEN, y, paint)
    }

    fun cerrar() {
        finalizarPagina()
    }

    private fun asegurarEspacio(alto: Float) {
        if (y + alto > ALTO_PAGINA - 48f) nuevaPagina()
    }

    private fun nuevaPagina() {
        finalizarPagina()
        numeroPagina += 1
        pagina = documento.startPage(
            PdfDocument.PageInfo.Builder(ANCHO_PAGINA, ALTO_PAGINA, numeroPagina).create()
        )
        canvas = pagina?.canvas
        y = MARGEN
    }

    private fun finalizarPagina() {
        val actual = pagina ?: return
        configurar(7f, Color.GRAY, false)
        val pie = "ARCSHIFT WELDING · Página $numeroPagina"
        canvasActual().drawText(
            pie,
            ANCHO_PAGINA - MARGEN - paint.measureText(pie),
            ALTO_PAGINA - 22f,
            paint
        )
        documento.finishPage(actual)
        pagina = null
        canvas = null
    }

    private fun configurar(tamano: Float, color: Int, negrita: Boolean) {
        paint.textSize = tamano
        paint.color = color
        paint.isFakeBoldText = negrita
        paint.style = Paint.Style.FILL
    }

    private fun envolver(texto: String, anchoMaximo: Float): List<String> {
        if (texto.isBlank()) return listOf("-")
        val resultado = mutableListOf<String>()
        texto.lines().forEach { parrafo ->
            var linea = ""
            parrafo.trim().split(Regex("\\s+")).forEach { palabra ->
                val candidata = if (linea.isBlank()) palabra else "$linea $palabra"
                if (paint.measureText(candidata) <= anchoMaximo || linea.isBlank()) {
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

    private fun canvasActual(): Canvas = requireNotNull(canvas)
}
