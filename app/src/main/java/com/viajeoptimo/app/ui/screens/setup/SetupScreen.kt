package com.viajeoptimo.app.ui.screens.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onSaved: () -> Unit,
    viewModel: SetupViewModel = viewModel()
) {
    val saved by viewModel.saved.collectAsState()
    val existing by viewModel.existingConfig.collectAsState(initial = null)

    LaunchedEffect(saved) { if (saved) onSaved() }

    var consumption by remember(existing) { mutableStateOf(existing?.consumptionLitersPer100km?.toString() ?: "") }
    var wearCost by remember(existing) { mutableStateOf(existing?.wearCostPerKm?.toString() ?: "") }
    var insurance by remember(existing) { mutableStateOf(existing?.monthlyInsurance?.toString() ?: "") }
    var registration by remember(existing) { mutableStateOf(existing?.annualRegistration?.toString() ?: "") }
    var targetIncome by remember(existing) { mutableStateOf(existing?.targetNetIncomePerHour?.toString() ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi vehículo") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConfigField("Consumo (litros cada 100 km)", consumption) { consumption = it }
            ConfigField("Desgaste por km ($ neumáticos + aceite)", wearCost) { wearCost = it }
            ConfigField("Seguro mensual ($)", insurance) { insurance = it }
            ConfigField("Patente anual ($)", registration) { registration = it }
            ConfigField("Objetivo ganancia neta por hora ($)", targetIncome) { targetIncome = it }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.save(consumption, wearCost, insurance, registration, targetIncome) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
private fun ConfigField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
