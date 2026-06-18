package com.example.arcshiftwelding.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arcshiftwelding.data.repository.MovimientoInventarioRepository

class MovimientoInventarioViewModelFactory(
    private val repository: MovimientoInventarioRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovimientoInventarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovimientoInventarioViewModel(repository) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}