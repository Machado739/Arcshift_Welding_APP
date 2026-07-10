package com.example.arcshiftwelding.data.repository

import androidx.room.withTransaction
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.ProyectoCostoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import java.time.LocalTime
import java.time.format.DateTimeFormatter
class ProyectoRepository(
    private val database: ArcshiftWeldingDatabase
) {
    private val productoDao = database.productoDao()
    private val proyectoMaterialDao = database.proyectoMaterialDao()
    private val proyectoEmpleadoDao = database.proyectoEmpleadoDao()
    private val proyectoCostoDao = database.proyectoCostoDao()
    private val proyectoDao = database.proyectoDao()
    private val movimientoInventarioDao = database.movimientoInventarioDao()
    suspend fun registrarMaterialUsado(
        proyectoId: Int,
        productoId: Int,
        cantidadUsada: Int,
        fechaUso: String,
        observaciones: String
    ): Result<Unit> {
        return try {
            database.withTransaction {
                val producto = productoDao.obtenerProductoPorId(productoId)
                    ?: throw IllegalStateException("Producto no encontrado")

                val actualizado = productoDao.descontarStockSiDisponible(
                    productoId = productoId,
                    cantidad = cantidadUsada
                )

                if (actualizado == 0) {
                    throw IllegalStateException("Stock insuficiente")
                }

                val costoUnitario = producto.precioCompra
                val subtotal = cantidadUsada * costoUnitario

                val material = ProyectoMaterialEntity(
                    proyectoId = proyectoId,
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    codigoProducto = producto.codigo,
                    categoria = producto.categoria,
                    cantidadUsada = cantidadUsada.toDouble(),
                    unidad = producto.unidad,
                    costoUnitario = costoUnitario,
                    subtotal = subtotal,
                    fechaUso = fechaUso,
                    observaciones = observaciones
                )

                proyectoMaterialDao.insertar(material)

                val proyecto = proyectoDao.obtenerProyectoPorIdDirecto(proyectoId)

                val referenciaProyecto = proyecto?.nombre
                    ?.takeIf { it.isNotBlank() }
                    ?: "Proyecto #$proyectoId"

                val movimiento = MovimientoInventarioEntity(
                    productoId = producto.id,
                    clienteId = null,
                    cotizacionId = null,
                    tipo = "Salida",
                    cantidad = cantidadUsada,
                    stockAnterior = producto.stock,
                    stockNuevo = producto.stock - cantidadUsada,
                    unidad = producto.unidad,
                    fecha = fechaUso,
                    hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    usuario = "",
                    referencia = referenciaProyecto,
                    observaciones = buildString {
                        append("Material usado en proyecto: $referenciaProyecto")
                        if (observaciones.isNotBlank()) {
                            append(". ${observaciones.trim()}")
                        }
                    }
                )

                movimientoInventarioDao.insertarMovimiento(movimiento)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarMaterialUsado(materialId: Int): Result<Unit> {
        return try {
            database.withTransaction {
                val material = proyectoMaterialDao.obtenerPorId(materialId)
                    ?: throw IllegalStateException("Material no encontrado")

                val producto = productoDao.obtenerProductoPorId(material.productoId)
                    ?: throw IllegalStateException("Producto no encontrado")

                productoDao.regresarStock(
                    productoId = material.productoId,
                    cantidad = material.cantidadUsada.toInt()
                )

                val cantidadDevuelta = material.cantidadUsada.toInt()
                val proyecto = proyectoDao.obtenerProyectoPorIdDirecto(material.proyectoId)

                val referenciaProyecto = proyecto?.nombre
                    ?.takeIf { it.isNotBlank() }
                    ?: "Proyecto #${material.proyectoId}"
                val movimiento = MovimientoInventarioEntity(
                    productoId = material.productoId,
                    clienteId = null,
                    cotizacionId = null,
                    tipo = "Entrada",
                    cantidad = cantidadDevuelta,
                    stockAnterior = producto.stock,
                    stockNuevo = producto.stock + cantidadDevuelta,
                    unidad = material.unidad,
                    fecha = obtenerFechaActualMovimientoProyecto(),
                    hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    usuario = "",
                    referencia = "Devolución - $referenciaProyecto",
                    observaciones = "Se eliminó material usado del proyecto: $referenciaProyecto."
                )

                movimientoInventarioDao.insertarMovimiento(movimiento)

                proyectoMaterialDao.eliminar(material)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun asignarEmpleado(
        proyectoEmpleado: ProyectoEmpleadoEntity
    ) {
        proyectoEmpleadoDao.insertar(proyectoEmpleado)
    }

    suspend fun agregarCosto(
        costo: ProyectoCostoEntity
    ) {
        proyectoCostoDao.insertar(costo)
    }

    private fun obtenerFechaActualMovimientoProyecto(): String {
        return java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
    }
}