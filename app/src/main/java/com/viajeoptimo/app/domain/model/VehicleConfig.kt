package com.viajeoptimo.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class VehicleConfig(
    val consumptionLitersPer100km: Double,
    val wearCostPerKm: Double,
    val monthlyInsurance: Double,
    val annualRegistration: Double,
    val targetNetIncomePerHour: Double
)
