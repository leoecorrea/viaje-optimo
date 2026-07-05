package com.viajeoptimo.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val config by viewModel.vehicleConfig.collectAsState()
    val thresholds by viewModel.thresholds.collectAsState()

    var consumption by remember(config) { mutableStateOf(config?.consumptionLitersPer100km?.toString() ?: "") }
    var wearCost by remember(config) { mutableStateOf(config?.wearCostPerKm?.toString() ?: "") }
    var insurance by remember(config) { mutableStateOf(config?.monthlyInsurance?.toString() ?: "") }
    var registration by remember(config) { mutableStateOf(config?.annualRegistration?.toString() ?: "") }
    var targetIncome by remember(config) { mutableStateOf(config?.targetNetIncomePerHour?.toString() ?: "") }

    var minPerHour by remember(thresholds) { mutableStateOf(thresholds.minGrossIncomePerHour.toString()) }
    var minPerKm by remember(thresholds) { mutableStateOf(thresholds.minGrossIncomePerKm.toString()) }
    var maxPickup by remember(thresholds) { mutableStateOf(thresholds.maxPickupDistanceKm.toString()) }
    var minDuration by remember(thresholds) { mutableStateOf(thresholds.minTripDurationMinutes.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mi vehículo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            SettingField("Consumo (L / 100 km)", consumption) { consumption = it }
            SettingField("Desgaste por km ($)", wearCost) { wearCost = it }
            SettingField("Seguro mensual ($)", insurance) { insurance = it }
            SettingField("Patente anual ($)", registration) { registration = it }
            SettingField("Objetivo neto / hora ($)", targetIncome) { targetIncome = it }

            Button(
                onClick = { viewModel.saveVehicleConfig(consumption, wearCost, insurance, registration, targetIncome) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar vehículo") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Umbrales del semáforo", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Text(
                "Dorado = cumple los 4. Verde = cumple los 3 primeros. Amarillo = cumple ganancia/hora y ganancia/km.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SettingField("Ganancia bruta mínima / hora ($)", minPerHour) { minPerHour = it }
            SettingField("Ganancia bruta mínima / km ($)", minPerKm) { minPerKm = it }
            SettingField("Distancia pickup máxima (km)", maxPickup) { maxPickup = it }
            SettingField("Duración mínima del viaje (min)", minDuration, isInt = true) { minDuration = it }

            Button(
                onClick = { viewModel.saveThresholds(minPerHour, minPerKm, maxPickup, minDuration) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar umbrales") }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingField(
    label: String,
    value: String,
    isInt: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isInt) KeyboardType.Number else KeyboardType.Decimal
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
