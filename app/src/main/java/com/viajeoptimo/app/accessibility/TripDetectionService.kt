package com.viajeoptimo.app.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.viajeoptimo.app.accessibility.parser.DidiTripParser
import com.viajeoptimo.app.accessibility.parser.TripOfferParser
import com.viajeoptimo.app.overlay.OverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TripDetectionService : AccessibilityService() {

    private val parsers: List<TripOfferParser> = listOf(DidiTripParser())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var overlayManager: OverlayManager? = null

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
        overlayManager = OverlayManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = event.packageName?.toString() ?: return
        Log.d("ViajeOptimo", "evento pkg=$pkg type=${event.eventType}")
        val parser = parsers.find { it.targetPackageName == pkg } ?: return
        val root = rootInActiveWindow ?: return

        logTree(root, 0)

        scope.launch {
            if (parser.isOfferVisible(root)) {
                TripEventBus.emit(parser.parse(root))
            } else {
                TripEventBus.emit(null)
            }
        }
    }

    private fun logTree(node: AccessibilityNodeInfo, depth: Int) {
        val indent = "  ".repeat(depth)
        val id = node.viewIdResourceName ?: ""
        val text = node.text?.toString() ?: ""
        val cls = node.className?.toString()?.substringAfterLast('.') ?: ""
        if (id.isNotEmpty() || text.isNotEmpty()) {
            Log.d("ViajeOptimo", "$indent[$cls] id=$id text=\"$text\"")
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            logTree(child, depth + 1)
            child.recycle()
        }
    }

    override fun onInterrupt() {
        overlayManager?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager?.dismiss()
    }
}
