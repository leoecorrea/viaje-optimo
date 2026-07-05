package com.viajeoptimo.app.ui.screens.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viajeoptimo.app.ViajeOptimoApp
import com.viajeoptimo.app.domain.model.ActiveSession
import com.viajeoptimo.app.domain.model.VehicleConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val dataStore = (app as ViajeOptimoApp).dataStore

    val vehicleConfig = dataStore.vehicleConfig.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val activeSession = dataStore.activeSession.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
}
