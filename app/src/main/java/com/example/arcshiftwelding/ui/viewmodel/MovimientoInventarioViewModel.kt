package com.example.arcshiftwelding.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import com.example.arcshiftwelding.data.repository.MovimientoInventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovimientoInventarioViewModel(
    private val repository: MovimientoInventarioRepository
) : ViewModel() {

    private val _movimientosRecientes = MutableStateFlow<List<MovimientoInventarioEntity>>(emptyList())
    val movimientosRecientes: StateFlow<List<MovimientoInventarioEntity>> = _movimientosRecientes

    private val _movimientosProducto = MutableStateFlow<List<MovimientoInventarioEntity>>(emptyList())
    val movimientosProducto: StateFlow<List<MovimientoInventarioEntity>> = _movimientosProducto

    fun cargarMovimientosRecientes(productoId: Int) {
        viewModelScope.launch {
            repository.obtenerMovimientosRecientes(productoId).collect { movimientos ->
                _movimientosRecientes.value = movimientos
            }
        }
    }

    fun cargarMovimientosProducto(productoId: Int) {
        viewModelScope.launch {
            repository.obtenerMovimientosPorProducto(productoId).collect { movimientos ->
                _movimientosProducto.value = movimientos
            }
        }
    }

    fun insertarMovimiento(movimiento: MovimientoInventarioEntity) {
        viewModelScope.launch {
            repository.insertarMovimiento(movimiento)
        }
    }
}