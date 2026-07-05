package com.viajeoptimo.app.ui.screens.summary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    onDone: () -> Unit,
    viewModel: SummaryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    var grossIncome by remember { mutableStateOf("") }
    var totalKm by remember { mutableStateOf("") }

    LaunchedEffect(grossIncome, totalKm) {
        if (grossIncome.isNotEmpty() && totalKm.isNotEmpty()) {
            viewModel.calculate(grossIncome, totalKm)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cierre de turno") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("¿Cuánto ganaste hoy?", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = grossIncome,
                onValueChange = { grossIncome = it },
                label = { Text("Ganancia bruta ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = totalKm,
                onValueChange = { totalKm = it },
                label = { Text("Kilómetros recorridos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.grossIncome > 0) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                Text("Desglose", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)

                SummaryRow("Ganancia bruta", state.grossIncome)
                SummaryRow("Combustible", -state.fuelCost, isDeduction = true)
                SummaryRow("Desgaste (neumáticos / aceite)", -state.wearCost, isDeduction = true)
                SummaryRow("Seguro (prorrateado)", -state.insuranceCost, isDeduction = true)
                SummaryRow("Patente (prorrateada)", -state.registrationCost, isDeduction = true)

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ganancia neta real", fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium)
                    Text(
                        "$ %.2f".format(state.netIncome),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (state.netIncome >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.saveAndClose() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar y cerrar turno")
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, amount: Double, isDeduction: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "$ %.2f".format(amount),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDeduction) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
