package com.viajeoptimo.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import com.viajeoptimo.app.accessibility.TripEventBus
import com.viajeoptimo.app.data.AppDataStore
import com.viajeoptimo.app.domain.model.SemaphoreColor
import com.viajeoptimo.app.domain.model.TripOffer
import com.viajeoptimo.app.domain.usecase.EvaluateTripUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val dataStore = AppDataStore(context)
    private val evaluateTrip = EvaluateTripUseCase()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var overlayView: SemaphoreBorderView? = null

    init {
        scope.launch {
            TripEventBus.tripDetected.collect { offer ->
                if (offer == null) dismiss() else showForOffer(offer)
            }
        }
    }

    private suspend fun showForOffer(offer: TripOffer) {
        val config = dataStore.vehicleConfig.first() ?: return
        val fuelPrice = dataStore.fuelPrice.first() ?: return
        val thresholds = dataStore.thresholds.first()

        val evaluation = evaluateTrip.evaluate(offer, config, fuelPrice, thresholds)
        show(evaluation.semaphore)
    }

    private fun show(color: SemaphoreColor) {
        if (!Settings.canDrawOverlays(context)) return

        dismiss()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        val view = SemaphoreBorderView(context, color)
        overlayView = view
        windowManager.addView(view, params)
    }

    fun dismiss() {
        overlayView?.let {
            runCatching { windowManager.removeView(it) }
            overlayView = null
        }
    }
}
