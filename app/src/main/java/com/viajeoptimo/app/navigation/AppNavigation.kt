package com.viajeoptimo.app.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.viajeoptimo.app.ViajeOptimoApp
import com.viajeoptimo.app.ui.screens.main.MainScreen
import com.viajeoptimo.app.ui.screens.onboarding.OnboardingScreen
import com.viajeoptimo.app.ui.screens.session.SessionStartScreen
import com.viajeoptimo.app.ui.screens.settings.SettingsScreen
import com.viajeoptimo.app.ui.screens.setup.SetupScreen
import com.viajeoptimo.app.ui.screens.summary.SummaryScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private object Route {
    const val ONBOARDING = "onboarding"
    const val SETUP = "setup"
    const val MAIN = "main"
    const val SESSION_START = "session_start"
    const val SUMMARY = "summary"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(onRequestScreenCapture: () -> Unit = {}) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val startDestination = remember {
        val dataStore = (context.applicationContext as ViajeOptimoApp).dataStore
        val hasConfig = runBlocking { dataStore.vehicleConfig.first() } != null
        if (hasConfig) Route.MAIN else Route.ONBOARDING
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Route.ONBOARDING) {
            OnboardingScreen(
                onPermissionsGranted = {
                    navController.navigate(Route.SETUP) {
                        popUpTo(Route.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.SETUP) {
            SetupScreen(
                onSaved = {
                    navController.navigate(Route.MAIN) {
                        popUpTo(Route.SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.MAIN) {
            MainScreen(
                onStartSession = { navController.navigate(Route.SESSION_START) },
                onEndSession = { navController.navigate(Route.SUMMARY) },
                onSettings = { navController.navigate(Route.SETTINGS) },
                onRequestScreenCapture = onRequestScreenCapture
            )
        }

        composable(Route.SESSION_START) {
            SessionStartScreen(
                onSessionStarted = {
                    navController.popBackStack()
                    onRequestScreenCapture()
                }
            )
        }

        composable(Route.SUMMARY) {
            SummaryScreen(
                onDone = {
                    navController.navigate(Route.MAIN) {
                        popUpTo(Route.MAIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
