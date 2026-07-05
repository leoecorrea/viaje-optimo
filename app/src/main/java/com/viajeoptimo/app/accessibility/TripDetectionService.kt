package com.viajeoptimo.app.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import com.viajeoptimo.app.overlay.OverlayManager

class TripDetectionService : AccessibilityService() {

    private var overlayManager: OverlayManager? = null

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
        overlayManager = OverlayManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Detection is handled by TripNotificationService + ScreenCaptureService via OCR
    }

    override fun onInterrupt() {
        overlayManager?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager?.dismiss()
    }
}
