package com.example.arcshiftwelding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.arcshiftwelding.navigation.AppNavigation
import com.example.arcshiftwelding.ui.theme.ArcshiftWeldingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ArcshiftWeldingTheme {
                AppNavigation()
            }
        }
    }
}