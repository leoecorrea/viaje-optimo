package com.viajeoptimo.app.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.viajeoptimo.app.domain.model.SemaphoreColor

class SemaphoreBorderView(context: Context, color: SemaphoreColor) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 28f
        this.color = when (color) {
            SemaphoreColor.GOLD -> Color.parseColor("#FFD700")
            SemaphoreColor.GREEN -> Color.parseColor("#4CAF50")
            SemaphoreColor.YELLOW -> Color.parseColor("#FFC107")
            SemaphoreColor.RED -> Color.parseColor("#F44336")
        }
    }

    override fun onDraw(canvas: Canvas) {
        val inset = paint.strokeWidth / 2f
        canvas.drawRect(inset, inset, width - inset, height - inset, paint)
    }
}
