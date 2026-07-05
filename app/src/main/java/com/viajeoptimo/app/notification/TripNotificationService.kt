package com.viajeoptimo.app.notification

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.viajeoptimo.app.accessibility.TripEventBus
import com.viajeoptimo.app.capture.ScreenCaptureService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TripNotificationService : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val DIDI_PACKAGE = "com.didiglobal.driver"
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d("ViajeOptimo", "notif recibida de: ${sbn.packageName}")
        if (sbn.packageName == DIDI_PACKAGE) {
            Log.d("ViajeOptimo", "notif de Didi → disparando captura")
            val captureIntent = Intent(this, ScreenCaptureService::class.java).apply {
                action = ScreenCaptureService.ACTION_CAPTURE
            }
            startService(captureIntent)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName == DIDI_PACKAGE) {
            scope.launch {
                TripEventBus.emit(null)
            }
        }
    }
}
