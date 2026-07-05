package com.viajeoptimo.app.ui.screens.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viajeoptimo.app.ViajeOptimoApp
import com.viajeoptimo.app.domain.model.VehicleConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetupViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = (app as ViajeOptimoApp).dataStore

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    val existingConfig = dataStore.vehicleConfig

    fun save(
        consumption: String,
        wearCost: String,
        insurance: String,
        registration: String,
        targetIncome: String
    ) {
        val config = VehicleConfig(
            consumptionLitersPer100km = consumption.toDoubleOrNull() ?: return,
            wearCostPerKm = wearCost.toDoubleOrNull() ?: return,
            monthlyInsurance = insurance.toDoubleOrNull() ?: return,
            annualRegistration = registration.toDoubleOrNull() ?: return,
            targetNetIncomePerHour = targetIncome.toDoubleOrNull() ?: return
        )
        viewModelScope.launch {
            dataStore.saveVehicleConfig(config)
            _saved.emit(true)
        }
    }
}
