package com.example.arcshiftwelding.ui.Screen.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientesViewModel(
    private val clienteDao: ClienteDao
) : ViewModel() {

    val clientes: StateFlow<List<ClienteUI>> =
        clienteDao.obtenerClientesActivos()
            .map { lista ->
                lista.map { cliente ->
                    cliente.toUi()
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun obtenerClienteDetalle(clienteId: Int): StateFlow<ClienteDetalleUI?> {
        return clienteDao.obtenerClientePorId(clienteId)
            .map { cliente ->
                cliente?.toDetalleUi()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
    }

    fun obtenerClienteConCotizaciones(clienteId: Int) =
        clienteDao.obtenerClienteConCotizaciones(clienteId)

    fun obtenerClienteEditar(clienteId: Int): StateFlow<ClienteEditarUI?> {
        return clienteDao.obtenerClientePorId(clienteId)
            .map { cliente ->
                cliente?.toEditarUi()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
    }

    fun guardarCliente(
        nombre: String,
        empresa: String,
        tipoCliente: String,
        estatus: String,
        telefono: String,
        correo: String,
        direccion: String,
        rfc: String,
        personaContacto: String,
        cargo: String,
        notas: String,
        clienteActivo: Boolean,
        recibeCotizaciones: Boolean,
        contactoWhatsapp: Boolean,
        contactoLlamadas: Boolean,
        contactoCorreo: Boolean,
        onGuardado: () -> Unit
    ) {
        if (nombre.isBlank()) return

        viewModelScope.launch {
            val fechaActual = System.currentTimeMillis()

            clienteDao.insertarCliente(
                ClienteEntity(
                    nombre = nombre.trim(),
                    empresa = empresa.trim(),
                    tipoCliente = tipoCliente.ifBlank { "Cliente general" },
                    estatus = if (clienteActivo) estatus else "Inactivo",
                    telefono = telefono.trim(),
                    correo = correo.trim(),
                    direccion = direccion.trim(),
                    rfc = rfc.trim(),
                    personaContacto = personaContacto.trim(),
                    cargo = cargo.trim(),
                    notas = notas.trim(),
                    clienteActivo = clienteActivo,
                    recibeCotizaciones = recibeCotizaciones,
                    contactoWhatsapp = contactoWhatsapp,
                    contactoLlamadas = contactoLlamadas,
                    contactoCorreo = contactoCorreo,
                    fechaRegistro = fechaActual,
                    ultimaActualizacion = fechaActual,
                    eliminado = false
                )
            )

            onGuardado()
        }
    }

    fun actualizarCliente(
        clienteId: Int,
        nombre: String,
        empresa: String,
        tipoCliente: String,
        estatus: String,
        telefono: String,
        correo: String,
        direccion: String,
        rfc: String,
        personaContacto: String,
        cargo: String,
        notas: String,
        clienteActivo: Boolean,
        recibeCotizaciones: Boolean,
        contactoWhatsapp: Boolean,
        contactoLlamadas: Boolean,
        contactoCorreo: Boolean,
        onActualizado: () -> Unit
    ) {
        if (nombre.isBlank()) return

        viewModelScope.launch {
            val clienteActual = clienteDao.obtenerClientePorIdUnaVez(clienteId) ?: return@launch

            clienteDao.actualizarCliente(
                clienteActual.copy(
                    nombre = nombre.trim(),
                    empresa = empresa.trim(),
                    tipoCliente = tipoCliente.ifBlank { "Cliente general" },
                    estatus = if (clienteActivo) estatus else "Inactivo",
                    telefono = telefono.trim(),
                    correo = correo.trim(),
                    direccion = direccion.trim(),
                    rfc = rfc.trim(),
                    personaContacto = personaContacto.trim(),
                    cargo = cargo.trim(),
                    notas = notas.trim(),
                    clienteActivo = clienteActivo,
                    recibeCotizaciones = recibeCotizaciones,
                    contactoWhatsapp = contactoWhatsapp,
                    contactoLlamadas = contactoLlamadas,
                    contactoCorreo = contactoCorreo,
                    ultimaActualizacion = System.currentTimeMillis()
                )
            )

            onActualizado()
        }
    }

    fun eliminarCliente(
        clienteId: Int,
        onEliminado: () -> Unit
    ) {
        viewModelScope.launch {
            clienteDao.eliminarCliente(
                clienteId = clienteId,
                fecha = System.currentTimeMillis()
            )

            onEliminado()
        }
    }
}

class ClientesViewModelFactory(
    private val clienteDao: ClienteDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientesViewModel(clienteDao) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}