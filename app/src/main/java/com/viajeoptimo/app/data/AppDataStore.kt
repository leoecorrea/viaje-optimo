package com.viajeoptimo.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.viajeoptimo.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "viaje_optimo")

class AppDataStore(private val context: Context) {

    companion object {
        private val KEY_VEHICLE_CONFIG = stringPreferencesKey("vehicle_config")
        private val KEY_THRESHOLDS = stringPreferencesKey("thresholds")
        private val KEY_FUEL_PRICE = doublePreferencesKey("fuel_price")
        private val KEY_ACTIVE_SESSION = stringPreferencesKey("active_session")
        private val KEY_SESSION_HISTORY = stringPreferencesKey("session_history")
    }

    // --- Configuración del vehículo (Módulo 1) ---

    val vehicleConfig: Flow<VehicleConfig?> = context.dataStore.data.map { prefs ->
        prefs[KEY_VEHICLE_CONFIG]?.let { Json.decodeFromString(it) }
    }

    suspend fun saveVehicleConfig(config: VehicleConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_VEHICLE_CONFIG] = Json.encodeToString(config)
        }
    }

    // --- Umbrales del semáforo ---

    val thresholds: Flow<Thresholds> = context.dataStore.data.map { prefs ->
        prefs[KEY_THRESHOLDS]?.let { Json.decodeFromString(it) } ?: Thresholds()
    }

    suspend fun saveThresholds(thresholds: Thresholds) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THRESHOLDS] = Json.encodeToString(thresholds)
        }
    }

    // --- Precio de combustible del día (Módulo 2) ---

    val fuelPrice: Flow<Double?> = context.dataStore.data.map { prefs ->
        prefs[KEY_FUEL_PRICE]
    }

    suspend fun saveFuelPrice(price: Double) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FUEL_PRICE] = price
        }
    }

    // --- Sesión activa ---

    val activeSession: Flow<ActiveSession?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ACTIVE_SESSION]?.let { Json.decodeFromString(it) }
    }

    suspend fun startSession(session: ActiveSession) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVE_SESSION] = Json.encodeToString(session)
        }
    }

    suspend fun endSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACTIVE_SESSION)
        }
    }

    // --- Historial de jornadas (Módulo 6) ---

    val sessionHistory: Flow<List<SessionRecord>> = context.dataStore.data.map { prefs ->
        prefs[KEY_SESSION_HISTORY]?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    suspend fun saveSessionRecord(record: SessionRecord) {
        context.dataStore.edit { prefs ->
            val current: List<SessionRecord> =
                prefs[KEY_SESSION_HISTORY]?.let { Json.decodeFromString(it) } ?: emptyList()
            prefs[KEY_SESSION_HISTORY] = Json.encodeToString(current + record)
        }
    }
}
