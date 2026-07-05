package com.viajeoptimo.app.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onStartSession: () -> Unit,
    onEndSession: () -> Unit,
    onSettings: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val session by viewModel.activeSession.collectAsState()
    val config by viewModel.vehicleConfig.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Viaje Óptimo") },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            if (session == null) {
                NoSessionContent(
                    hasConfig = config != null,
                    onStartSession = onStartSession
                )
            } else {
                ActiveSessionContent(
                    session = session!!,
                    onEndSession = onEndSession
                )
            }
        }
    }
}

@Composable
private fun NoSessionContent(hasConfig: Boolean, onStartSession: () -> Unit) {
    Text(
        text = "Sin turno activo",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Iniciá un turno para que el semáforo evalúe las ofertas automáticamente.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = onStartSession,
        enabled = hasConfig,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Iniciar turno")
    }
    if (!hasConfig) {
        Text(
            text = "Completá la configuración del vehículo primero",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ActiveSessionContent(
    session: com.viajeoptimo.app.domain.model.ActiveSession,
    onEndSession: () -> Unit
) {
    val startTime = remember(session.startTimeMs) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(session.startTimeMs))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Turno activo", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
            Text("Inicio: $startTime", style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold)
            Text(
                "Combustible: $${session.fuelPricePerLiter}/L",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Text(
        text = "El semáforo está activo. Cuando llegue una oferta en Didi, verás el borde de color.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(Modifier.height(48.dp))

    OutlinedButton(
        onClick = onEndSession,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
    ) {
        Text("Cerrar turno")
    }
}
