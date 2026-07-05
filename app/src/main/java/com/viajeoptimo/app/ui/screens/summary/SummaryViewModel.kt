package com.viajeoptimo.app.ui.screens.summary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viajeoptimo.app.ViajeOptimoApp
import com.viajeoptimo.app.domain.model.SessionRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class SummaryUiState(
    val grossIncome: Double = 0.0,
    val totalKm: Double = 0.0,
    val fuelCost: Double = 0.0,
    val wearCost: Double = 0.0,
    val insuranceCost: Double = 0.0,
    val registrationCost: Double = 0.0,
    val netIncome: Double = 0.0,
    val saved: Boolean = false
)

class SummaryViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = (app as ViajeOptimoApp).dataStore

    private val _state = MutableStateFlow(SummaryUiState())
    val state: StateFlow<SummaryUiState> = _state.asStateFlow()

    fun calculate(grossIncomeStr: String, totalKmStr: String) {
        viewModelScope.launch {
            val grossIncome = grossIncomeStr.toDoubleOrNull() ?: return@launch
            val totalKm = totalKmStr.toDoubleOrNull() ?: return@launch
            val config = dataStore.vehicleConfig.first() ?: return@launch
            val fuelPrice = dataStore.fuelPrice.first() ?: return@launch
            val session = dataStore.activeSession.first() ?: return@launch

            val fuelCostPerKm = (config.consumptionLitersPer100km / 100.0) * fuelPrice
            val fuelCost = fuelCostPerKm * totalKm
            val wearCost = config.wearCostPerKm * totalKm

            val sessionDurationHours = (System.currentTimeMillis() - session.startTimeMs) / 3_600_000.0
            val sessionDurationDays = sessionDurationHours / 24.0
            val insuranceCost = config.monthlyInsurance / 30.0 * sessionDurationDays.coerceAtLeast(1.0 / 24.0)
            val registrationCost = config.annualRegistration / 365.0 * sessionDurationDays.coerceAtLeast(1.0 / 24.0)

            val netIncome = grossIncome - fuelCost - wearCost - insuranceCost - registrationCost

            _state.value = SummaryUiState(
                grossIncome = grossIncome,
                totalKm = totalKm,
                fuelCost = fuelCost,
                wearCost = wearCost,
                insuranceCost = insuranceCost,
                registrationCost = registrationCost,
                netIncome = netIncome
            )
        }
    }

    fun saveAndClose() {
        viewModelScope.launch {
            val s = _state.value
            val session = dataStore.activeSession.first() ?: return@launch

            val record = SessionRecord(
                id = UUID.randomUUID().toString(),
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                startTimeMs = session.startTimeMs,
                endTimeMs = System.currentTimeMillis(),
                fuelPricePerLiter = session.fuelPricePerLiter,
                totalKm = s.totalKm,
                grossIncome = s.grossIncome,
                fuelCost = s.fuelCost,
                wearCost = s.wearCost,
                insuranceCost = s.insuranceCost,
                registrationCost = s.registrationCost,
                netIncome = s.netIncome
            )
            dataStore.saveSessionRecord(record)
            dataStore.endSession()
            _state.value = s.copy(saved = true)
        }
    }
}
