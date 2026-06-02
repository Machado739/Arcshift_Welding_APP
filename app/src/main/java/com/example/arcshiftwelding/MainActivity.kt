package com.example.arcshiftwelding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.example.arcshiftwelding.data.local.database.AppDatabase
import com.example.arcshiftwelding.navigation.AppNavigation
import com.example.arcshiftwelding.ui.theme.ArcshiftWeldingTheme
import kotlin.jvm.java


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArcshiftWeldingTheme {
                AppNavigation()
            }
        }
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "arcshift_welding_db"
        ).build()
    }
}