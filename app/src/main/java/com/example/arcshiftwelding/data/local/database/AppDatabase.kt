package com.example.arcshiftwelding.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.arcshiftwelding.data.local.dao.CategoriaProductoDao
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.DetalleCotizacionDao
import com.example.arcshiftwelding.data.local.dao.EmpleadoDao
import com.example.arcshiftwelding.data.local.dao.EmpresaDao
import com.example.arcshiftwelding.data.local.dao.GastoDao
import com.example.arcshiftwelding.data.local.dao.IngresoDao
import com.example.arcshiftwelding.data.local.dao.MovimientoInventarioDao
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoDao
import com.example.arcshiftwelding.data.local.dao.UsuarioDao
import com.example.arcshiftwelding.data.local.entities.ClienteEntity
import com.example.arcshiftwelding.data.local.entities.CotizacionEntity
import com.example.arcshiftwelding.data.local.entities.DetalleCotizacionEntity
import com.example.arcshiftwelding.data.local.entities.EmpleadoEntity
import com.example.arcshiftwelding.data.local.entities.GastoEntity
import com.example.arcshiftwelding.data.local.entities.IngresoEntity
import com.example.arcshiftwelding.data.local.entities.ProductoEntity

@Database(
    entities = [
        ProductoEntity::class,
        GastoEntity::class,
        IngresoEntity::class,
        ClienteEntity::class,
        EmpleadoEntity::class,
        CotizacionEntity::class,
            DetalleCotizacionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun gastoDao(): GastoDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun clienteDao(): ClienteDao
    abstract fun empleadoDao(): EmpleadoDao
    abstract fun cotizacionDao(): CotizacionDao

    abstract fun detalleCotizacionDao(): DetalleCotizacionDao
    abstract fun movimientoInventarioDao(): MovimientoInventarioDao
    abstract fun categoriaProductoDao(): CategoriaProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun empresaDao(): EmpresaDao
    abstract fun proyectoDao(): ProyectoDao
}