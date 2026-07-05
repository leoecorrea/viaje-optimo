package com.viajeoptimo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.viajeoptimo.app.navigation.AppNavigation
import com.viajeoptimo.app.ui.theme.ViajeOptimoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViajeOptimoTheme {
                AppNavigation()
            }
        }
    }
}
