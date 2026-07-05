package com.viajeoptimo.app.accessibility.parser

import android.view.accessibility.AccessibilityNodeInfo
import com.viajeoptimo.app.domain.model.TripOffer

interface TripOfferParser {
    val targetPackageName: String
    fun isOfferVisible(rootNode: AccessibilityNodeInfo): Boolean
    fun parse(rootNode: AccessibilityNodeInfo): TripOffer?
}
