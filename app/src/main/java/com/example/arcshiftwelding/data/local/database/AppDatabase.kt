package com.example.arcshiftwelding.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.entities.ProductoEntity

@Database(
    entities = [ProductoEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
}