package com.viajeoptimo.app.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viajeoptimo.app.ViajeOptimoApp
import com.viajeoptimo.app.domain.model.Thresholds
import com.viajeoptimo.app.domain.model.VehicleConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = (app as ViajeOptimoApp).dataStore

    val vehicleConfig = dataStore.vehicleConfig.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val thresholds = dataStore.thresholds.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Thresholds()
    )

    fun saveVehicleConfig(
        consumption: String, wearCost: String,
        insurance: String, registration: String, targetIncome: String
    ) {
        val config = VehicleConfig(
            consumptionLitersPer100km = consumption.toDoubleOrNull() ?: return,
            wearCostPerKm = wearCost.toDoubleOrNull() ?: return,
            monthlyInsurance = insurance.toDoubleOrNull() ?: return,
            annualRegistration = registration.toDoubleOrNull() ?: return,
            targetNetIncomePerHour = targetIncome.toDoubleOrNull() ?: return
        )
        viewModelScope.launch { dataStore.saveVehicleConfig(config) }
    }

    fun saveThresholds(
        minPerHour: String, minPerKm: String,
        maxPickup: String, minDuration: String
    ) {
        val t = Thresholds(
            minGrossIncomePerHour = minPerHour.toDoubleOrNull() ?: return,
            minGrossIncomePerKm = minPerKm.toDoubleOrNull() ?: return,
            maxPickupDistanceKm = maxPickup.toDoubleOrNull() ?: return,
            minTripDurationMinutes = minDuration.toIntOrNull() ?: return
        )
        viewModelScope.launch { dataStore.saveThresholds(t) }
    }
}
