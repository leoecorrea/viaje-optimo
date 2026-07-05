package com.viajeoptimo.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Thresholds(
    val minGrossIncomePerHour: Double = 2000.0,
    val minGrossIncomePerKm: Double = 150.0,
    val maxPickupDistanceKm: Double = 3.0,
    val minTripDurationMinutes: Int = 10
)
