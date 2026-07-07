package com.example.arcshiftwelding.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.DetalleCotizacionDao
import com.example.arcshiftwelding.data.local.dao.EmpleadoDao
import com.example.arcshiftwelding.data.local.dao.GastoDao
import com.example.arcshiftwelding.data.local.dao.IngresoDao
import com.example.arcshiftwelding.data.local.dao.MovimientoInventarioDao
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoEmpleadoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoMaterialDao
import com.example.arcshiftwelding.data.local.entity.CategoriaProductoEntity
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.EmpresaEntity
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity
import com.example.arcshiftwelding.data.local.entity.UsuarioEntity


@Database(
    entities = [
        ClienteEntity::class,
        CotizacionEntity::class,
        DetalleCotizacionEntity::class,
        GastoEntity::class,
        IngresoEntity::class,
        MovimientoInventarioEntity::class,
        ProductoEntity::class,
        CategoriaProductoEntity::class,
        EmpleadoEntity::class,
        EmpresaEntity::class,
        ProyectoEntity::class,
        UsuarioEntity::class,
        ProyectoEmpleadoEntity::class,
        ProyectoMaterialEntity::class

               ],
    version = 15,
    exportSchema = false
)
abstract class ArcshiftWeldingDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoInventarioDao(): MovimientoInventarioDao
    abstract fun gastoDao(): GastoDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun clienteDao(): ClienteDao
    abstract fun empleadoDao(): EmpleadoDao
    abstract fun cotizacionDao(): CotizacionDao
    abstract fun detalleCotizacionDao(): DetalleCotizacionDao
    abstract fun proyectoEmpleadoDao(): ProyectoEmpleadoDao
    abstract fun proyectoMaterialDao(): ProyectoMaterialDao
    abstract fun proyectoDao(): ProyectoDao

    companion object {
        @Volatile
        private var INSTANCE: ArcshiftWeldingDatabase? = null

        fun getDatabase(context: Context): ArcshiftWeldingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArcshiftWeldingDatabase::class.java,
                    "arcshift_welding_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}