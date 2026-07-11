package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val empresa: String = "",
    val tipoCliente: String = "Cliente general",
    val estatus: String = "Activo",
    val telefono: String = "",
    val correo: String = "",
    val direccion: String = "",
    val rfc: String = "",
    val personaContacto: String = "",
    val cargo: String = "",
    val notas: String = "",
    val fotoUri: String = "",
    val clienteActivo: Boolean = true,
    val recibeCotizaciones: Boolean = true,
    val contactoWhatsapp: Boolean = true,
    val contactoLlamadas: Boolean = true,
    val contactoCorreo: Boolean = false,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val ultimaActualizacion: Long = System.currentTimeMillis(),
    val eliminado: Boolean = false
)
