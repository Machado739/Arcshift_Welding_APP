package com.example.arcshiftwelding.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.DetalleCotizacionDao
import com.example.arcshiftwelding.data.local.dao.EmpleadoDao
import com.example.arcshiftwelding.data.local.dao.GastoDao
import com.example.arcshiftwelding.data.local.dao.IngresoDao
import com.example.arcshiftwelding.data.local.dao.MovimientoInventarioDao
import com.example.arcshiftwelding.data.local.dao.PagoProgramadoDao
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoCostoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoAvanceDao
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
import com.example.arcshiftwelding.data.local.entity.PagoProgramadoEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoCostoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoAvanceEntity
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
        ProyectoMaterialEntity::class,
        PagoProgramadoEntity::class,
        ProyectoCostoEntity::class,
        ProyectoAvanceEntity::class

               ],
    version = 30,
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
    abstract fun pagoProgramadoDao(): PagoProgramadoDao
    abstract fun proyectoCostoDao(): ProyectoCostoDao
    abstract fun proyectoAvanceDao(): ProyectoAvanceDao


    companion object {
        @Volatile
        private var INSTANCE: ArcshiftWeldingDatabase? = null

        private val MIGRATION_25_26 = object : Migration(25, 26) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE gastos ADD COLUMN comprobanteUri TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE gastos ADD COLUMN tipoComprobante TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE gastos ADD COLUMN nombreComprobante TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_26_27 = object : Migration(26, 27) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE clientes ADD COLUMN fotoUri TEXT NOT NULL DEFAULT ''"
                )
            }
        }


        private val MIGRATION_27_28 = object : Migration(27, 28) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE gastos ADD COLUMN comprobantesJson TEXT NOT NULL DEFAULT '[]'"
                )
                database.execSQL(
                    "ALTER TABLE ingresos ADD COLUMN comprobantesJson TEXT NOT NULL DEFAULT '[]'"
                )
            }
        }

        private val MIGRATION_28_29 = object : Migration(28, 29) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE cotizaciones ADD COLUMN fechaAprobacion TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE cotizaciones ADD COLUMN fechaActualizacion TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE cotizaciones ADD COLUMN archivosAdjuntosJson TEXT NOT NULL DEFAULT '[]'"
                )
            }
        }

        private val MIGRATION_29_30 = object : Migration(29, 30) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE empleados ADD COLUMN fotoUri TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE proyectos ADD COLUMN imagenesJson TEXT NOT NULL DEFAULT '[]'"
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS proyecto_avances (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        proyectoId INTEGER NOT NULL,
                        porcentaje INTEGER NOT NULL,
                        fecha TEXT NOT NULL,
                        comentario TEXT NOT NULL DEFAULT '',
                        fotosJson TEXT NOT NULL DEFAULT '[]',
                        FOREIGN KEY(proyectoId) REFERENCES proyectos(id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_proyecto_avances_proyectoId ON proyecto_avances(proyectoId)"
                )
            }
        }

        fun getDatabase(context: Context): ArcshiftWeldingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArcshiftWeldingDatabase::class.java,
                    "arcshift_welding_database"
                )
                    .addMigrations(
                        MIGRATION_25_26,
                        MIGRATION_26_27,
                        MIGRATION_27_28,
                        MIGRATION_28_29,
                        MIGRATION_29_30
                    )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}