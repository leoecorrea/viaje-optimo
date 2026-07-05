package com.viajeoptimo.app.accessibility.parser

import android.view.accessibility.AccessibilityNodeInfo
import com.viajeoptimo.app.domain.model.TripOffer

class DidiTripParser : TripOfferParser {

    // PASO PENDIENTE: reemplazar con el package name real de la app de Didi conductor.
    // Para encontrarlo: abrir la app Didi conductor en el dispositivo y ejecutar en Android Studio:
    //   adb shell dumpsys window | grep -i "mCurrentFocus"
    // El resultado muestra algo como: mCurrentFocus=Window{... com.didi.xxx/...}
    override val targetPackageName = "com.didiglobal.driver"

    // PASO PENDIENTE: completar después de inspeccionar el árbol de accesibilidad con Layout Inspector.
    // Pasos para inspeccionar:
    //   1. Conectar dispositivo con USB debugging
    //   2. Abrir Android Studio → View → Tool Windows → Layout Inspector
    //   3. Seleccionar el proceso de Didi cuando aparezca una oferta
    //   4. Copiar los viewIdResourceName de los nodos con ganancia, distancia pickup y duración
    private val NODE_OFFER_MODAL = "TODO_VIEW_ID_DEL_MODAL_DE_OFERTA"
    private val NODE_INCOME = "TODO_VIEW_ID_GANANCIA_OFRECIDA"
    private val NODE_PICKUP_DISTANCE = "TODO_VIEW_ID_DISTANCIA_PICKUP"
    private val NODE_TRIP_DISTANCE = "TODO_VIEW_ID_DISTANCIA_VIAJE"
    private val NODE_DURATION = "TODO_VIEW_ID_DURACION_MINUTOS"

    override fun isOfferVisible(rootNode: AccessibilityNodeInfo): Boolean {
        return findNodeById(rootNode, NODE_OFFER_MODAL) != null
    }

    override fun parse(rootNode: AccessibilityNodeInfo): TripOffer? {
        val income = extractDouble(rootNode, NODE_INCOME) ?: return null
        val pickupKm = extractDouble(rootNode, NODE_PICKUP_DISTANCE) ?: return null
        val tripKm = extractDouble(rootNode, NODE_TRIP_DISTANCE) ?: return null
        val durationMin = extractInt(rootNode, NODE_DURATION) ?: return null

        return TripOffer(
            offeredGrossIncome = income,
            pickupDistanceKm = pickupKm,
            tripDistanceKm = tripKm,
            estimatedDurationMinutes = durationMin
        )
    }

    private fun findNodeById(root: AccessibilityNodeInfo, viewId: String): AccessibilityNodeInfo? {
        if (root.viewIdResourceName == viewId) return root
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            val result = findNodeById(child, viewId)
            if (result != null) return result
            child.recycle()
        }
        return null
    }

    private fun extractDouble(root: AccessibilityNodeInfo, viewId: String): Double? {
        val node = findNodeById(root, viewId) ?: return null
        return parseNumeric(node.text?.toString() ?: return null)
    }

    private fun extractInt(root: AccessibilityNodeInfo, viewId: String): Int? {
        val node = findNodeById(root, viewId) ?: return null
        return parseNumeric(node.text?.toString() ?: return null)?.toInt()
    }

    private fun parseNumeric(text: String): Double? {
        return text.replace(Regex("[^0-9.,]"), "").replace(",", ".").toDoubleOrNull()
    }
}
