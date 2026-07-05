package com.viajeoptimo.app.domain.model

data class TripEvaluation(
    val offer: TripOffer,
    val grossIncomePerHour: Double,
    val grossIncomePerKm: Double,
    val costPerKm: Double,
    val netIncome: Double,
    val semaphore: SemaphoreColor
)
