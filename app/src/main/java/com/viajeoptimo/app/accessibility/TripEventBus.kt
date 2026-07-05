package com.viajeoptimo.app.accessibility

import com.viajeoptimo.app.domain.model.TripOffer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object TripEventBus {
    private val _tripDetected = MutableSharedFlow<TripOffer?>(replay = 1)
    val tripDetected = _tripDetected.asSharedFlow()

    suspend fun emit(offer: TripOffer?) {
        _tripDetected.emit(offer)
    }
}
