package com.viajeoptimo.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ActiveSession(
    val startTimeMs: Long,
    val fuelPricePerLiter: Double
)
