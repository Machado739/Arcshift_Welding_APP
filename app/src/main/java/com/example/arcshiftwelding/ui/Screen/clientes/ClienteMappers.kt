package com.example.arcshiftwelding.ui.Screen.clientes

import androidx.compose.ui.graphics.Color
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun ClienteEntity.toUi(
    cantidadCotizaciones: Int = 0
): ClienteUI {
    return ClienteUI(
        id = id,
        nombre = nombre,
        telefono = telefono.ifBlank { "Sin teléfono" },
        ubicacion = direccion.ifBlank { "Sin dirección" },
        correo = correo.ifBlank { "Sin correo" },
        fotoUri = fotoUri,
        cotizaciones = cantidadCotizaciones,
        estado = estatus,
        tipo = "Cliente desde: ${fechaRegistro.formatearFechaCliente()}",
        color = obtenerColorCliente(estatus)
    )
}

fun ClienteEntity.toDetalleUi(): ClienteDetalleUI {
    return ClienteDetalleUI(
        id = id,
        nombre = nombre,
        empresa = empresa.ifBlank { "Sin empresa" },
        telefono = telefono.ifBlank { "Sin teléfono" },
        correo = correo.ifBlank { "Sin correo" },
        direccion = direccion.ifBlank { "Sin dirección" },
        rfc = rfc.ifBlank { "Sin RFC" },
        tipoCliente = tipoCliente,
        registradoPor = "Administrador",
        fechaRegistro = fechaRegistro.formatearFechaCliente(),
        ultimaActualizacion = ultimaActualizacion.formatearFechaCliente(),
        estado = estatus,
        personaContacto = personaContacto.ifBlank { "Sin contacto" },
        ultimaActividad = ultimaActualizacion.formatearFechaCliente(),
        totalCotizaciones = 0,
        totalIngresos = 0,
        totalProyectos = 0,
        totalFacturado = 0.0,
        notas = notas.ifBlank { "Sin notas registradas." },
        fotoUri = fotoUri
    )
}

fun ClienteEntity.toEditarUi(): ClienteEditarUI {
    return ClienteEditarUI(
        id = id,
        nombre = nombre,
        empresa = empresa,
        tipoCliente = tipoCliente,
        estatus = estatus,
        telefono = telefono,
        correo = correo,
        direccion = direccion,
        rfc = rfc,
        personaContacto = personaContacto,
        cargo = cargo,
        notas = notas,
        clienteActivo = clienteActivo,
        recibeCotizaciones = recibeCotizaciones,
        contactoWhatsapp = contactoWhatsapp,
        contactoLlamadas = contactoLlamadas,
        contactoCorreo = contactoCorreo,
        fotoUri = fotoUri
    )
}

fun obtenerColorCliente(estatus: String): Color {
    return when (estatus) {
        "Activo" -> Color(0xFF16A34A)
        "Inactivo" -> Color(0xFF64748B)
        "Pendiente" -> Color(0xFFEAB308)
        else -> Color(0xFF2563EB)
    }
}

private fun Long.formatearFechaCliente(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))
}