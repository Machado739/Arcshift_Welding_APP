package com.example.arcshiftwelding.data.repository

import androidx.room.withTransaction
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.ProyectoCostoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity

class ProyectoRepository(
    private val database: ArcshiftWeldingDatabase
) {
    private val productoDao = database.productoDao()
    private val proyectoMaterialDao = database.proyectoMaterialDao()
    private val proyectoEmpleadoDao = database.proyectoEmpleadoDao()
    private val proyectoCostoDao = database.proyectoCostoDao()

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

                productoDao.regresarStock(
                    productoId = material.productoId,
                    cantidad = material.cantidadUsada.toInt()
                )

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
}