package com.example.arcshiftwelding.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.arcshiftwelding.data.local.dao.MovimientoInventarioDao
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity

@Database(
    entities = [
        ProductoEntity::class,
        MovimientoInventarioEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ArcshiftWeldingDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoInventarioDao(): MovimientoInventarioDao


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