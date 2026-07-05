package com.viajeoptimo.app.ui.screens.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionStartScreen(
    onSessionStarted: () -> Unit,
    viewModel: SessionViewModel = viewModel()
) {
    val started by viewModel.started.collectAsState()
    val lastPrice by viewModel.lastFuelPrice.collectAsState(initial = null)

    LaunchedEffect(started) { if (started) onSessionStarted() }

    var fuelPrice by remember(lastPrice) {
        mutableStateOf(lastPrice?.toString() ?: "")
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Iniciar turno") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Cuánto vale el litro hoy?",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = fuelPrice,
                onValueChange = { fuelPrice = it },
                label = { Text("Precio del combustible ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { viewModel.startSession(fuelPrice) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Arrancar turno")
            }
        }
    }
}
