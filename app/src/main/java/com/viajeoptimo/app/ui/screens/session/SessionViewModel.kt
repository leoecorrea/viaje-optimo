package com.viajeoptimo.app.ui.screens.session

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viajeoptimo.app.ViajeOptimoApp
import com.viajeoptimo.app.domain.model.ActiveSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = (app as ViajeOptimoApp).dataStore

    private val _started = MutableStateFlow(false)
    val started: StateFlow<Boolean> = _started.asStateFlow()

    val lastFuelPrice = dataStore.fuelPrice

    fun startSession(fuelPriceStr: String) {
        val price = fuelPriceStr.toDoubleOrNull() ?: return
        viewModelScope.launch {
            dataStore.saveFuelPrice(price)
            dataStore.startSession(
                ActiveSession(startTimeMs = System.currentTimeMillis(), fuelPricePerLiter = price)
            )
            _started.emit(true)
        }
    }
}
