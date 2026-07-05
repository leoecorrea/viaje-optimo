package com.viajeoptimo.app.domain.model

data class TripOffer(
    val offeredGrossIncome: Double,
    val pickupDistanceKm: Double,
    val tripDistanceKm: Double,
    val estimatedDurationMinutes: Int
)
