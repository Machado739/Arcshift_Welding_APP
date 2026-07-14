package com.example.arcshiftwelding.ui.Screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase

class LoginViewModelFactory(
    private val database: ArcshiftWeldingDatabase,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                database = database,
                context = context.applicationContext
            ) as T
        }

        throw IllegalArgumentException(
            "LoginViewModelFactory no puede crear: ${modelClass.name}"
        )
    }
}
