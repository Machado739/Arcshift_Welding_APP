package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.ProyectoDao

class ProyectosViewModelFactory(
    private val proyectoDao: ProyectoDao,
    private val clienteDao: ClienteDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProyectosViewModel::class.java)) {
            return ProyectosViewModel(
                proyectoDao = proyectoDao,
                clienteDao = clienteDao
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}