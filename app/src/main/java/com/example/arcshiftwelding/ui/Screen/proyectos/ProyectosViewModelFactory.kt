package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.repository.ProyectoRepository

class ProyectosViewModelFactory(
    private val database: ArcshiftWeldingDatabase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProyectosViewModel::class.java)) {
            return ProyectosViewModel(
                proyectoDao = database.proyectoDao(),
                clienteDao = database.clienteDao(),
                cotizacionDao = database.cotizacionDao(),
                empleadoDao = database.empleadoDao(),
                productoDao = database.productoDao(),
                gastoDao = database.gastoDao(),
                proyectoEmpleadoDao = database.proyectoEmpleadoDao(),
                proyectoMaterialDao = database.proyectoMaterialDao(),
                proyectoCostoDao = database.proyectoCostoDao(),
                proyectoAvanceDao = database.proyectoAvanceDao(),
                proyectoRepository = ProyectoRepository(database)
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }


}