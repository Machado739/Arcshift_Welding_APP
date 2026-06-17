package com.example.arcshiftwelding.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    val productos: StateFlow<List<ProductoEntity>> =
        _textoBusqueda.flatMapLatest { texto ->
            if (texto.isBlank()) {
                repository.productos
            } else {
                repository.buscarProductos(texto)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val productosBajoStock: StateFlow<List<ProductoEntity>> =
        repository.productosBajoStock.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _productoSeleccionado = MutableStateFlow<ProductoEntity?>(null)
    val productoSeleccionado: StateFlow<ProductoEntity?> = _productoSeleccionado

    fun cambiarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto
    }

    fun cargarProductoPorId(productoId: Int) {
        viewModelScope.launch {
            _productoSeleccionado.value = repository.obtenerProductoPorId(productoId)
        }
    }

    fun insertarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            repository.insertarProducto(producto)
        }
    }

    fun actualizarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            repository.actualizarProducto(producto)
        }
    }

    fun eliminarProducto(producto: ProductoEntity) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
        }
    }

    fun desactivarProducto(productoId: Int) {
        viewModelScope.launch {
            repository.desactivarProducto(productoId)
        }
    }

    fun reponerStock(productoId: Int, cantidad: Int) {
        if (cantidad <= 0) return

        viewModelScope.launch {
            repository.reponerStock(productoId, cantidad)
        }
    }

    fun reportarSalida(productoId: Int, cantidad: Int) {
        if (cantidad <= 0) return

        viewModelScope.launch {
            repository.reportarSalida(productoId, cantidad)
        }
    }
}