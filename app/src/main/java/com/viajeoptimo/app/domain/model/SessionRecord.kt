package com.viajeoptimo.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SessionRecord(
    val id: String,
    val date: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val fuelPricePerLiter: Double,
    val totalKm: Double,
    val grossIncome: Double,
    val fuelCost: Double,
    val wearCost: Double,
    val insuranceCost: Double,
    val registrationCost: Double,
    val netIncome: Double
)
