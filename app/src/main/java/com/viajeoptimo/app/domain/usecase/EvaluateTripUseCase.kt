package com.viajeoptimo.app.domain.usecase

import com.viajeoptimo.app.domain.model.*

class EvaluateTripUseCase {

    fun evaluate(
        offer: TripOffer,
        config: VehicleConfig,
        fuelPricePerLiter: Double,
        thresholds: Thresholds
    ): TripEvaluation {
        val fuelCostPerKm = (config.consumptionLitersPer100km / 100.0) * fuelPricePerLiter
        val costPerKm = fuelCostPerKm + config.wearCostPerKm

        val durationHours = offer.estimatedDurationMinutes / 60.0
        val grossIncomePerHour = if (durationHours > 0) offer.offeredGrossIncome / durationHours else 0.0
        val grossIncomePerKm = if (offer.tripDistanceKm > 0) offer.offeredGrossIncome / offer.tripDistanceKm else 0.0

        val tripCost = costPerKm * offer.tripDistanceKm
        val netIncome = offer.offeredGrossIncome - tripCost

        val meetsIncomePerHour = grossIncomePerHour >= thresholds.minGrossIncomePerHour
        val meetsIncomePerKm = grossIncomePerKm >= thresholds.minGrossIncomePerKm
        val meetsPickup = offer.pickupDistanceKm <= thresholds.maxPickupDistanceKm
        val meetsDuration = offer.estimatedDurationMinutes >= thresholds.minTripDurationMinutes

        val semaphore = when {
            meetsIncomePerHour && meetsIncomePerKm && meetsPickup && meetsDuration -> SemaphoreColor.GOLD
            meetsIncomePerHour && meetsIncomePerKm && meetsPickup -> SemaphoreColor.GREEN
            meetsIncomePerHour && meetsIncomePerKm -> SemaphoreColor.YELLOW
            else -> SemaphoreColor.RED
        }

        return TripEvaluation(
            offer = offer,
            grossIncomePerHour = grossIncomePerHour,
            grossIncomePerKm = grossIncomePerKm,
            costPerKm = costPerKm,
            netIncome = netIncome,
            semaphore = semaphore
        )
    }
}
