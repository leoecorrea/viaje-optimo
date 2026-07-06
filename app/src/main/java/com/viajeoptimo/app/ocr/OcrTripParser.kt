package com.viajeoptimo.app.ocr

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.viajeoptimo.app.domain.model.TripOffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OcrTripParser {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun parse(bitmap: Bitmap): TripOffer? {
        val text = runCatching { recognizeText(bitmap) }.getOrNull() ?: return null
        return extractOffer(text)
    }

    private suspend fun recognizeText(bitmap: Bitmap): String = suspendCoroutine { cont ->
        recognizer.process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener { cont.resume(it.text) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    private fun extractOffer(text: String): TripOffer? {
        Log.d("ViajeOptimo", "OCR texto reconocido:\n$text")
        val income = Regex("""\$\s*(\d[\d.,]*)""").find(text)
            ?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull()
            ?: return null

        val distances = Regex("""(\d+[.,]\d+|\d+)\s*km""", RegexOption.IGNORE_CASE)
            .findAll(text)
            .mapNotNull { it.groupValues[1].replace(",", ".").toDoubleOrNull() }
            .toList()

        val pickupKm = distances.getOrNull(0) ?: 0.0
        val tripKm = distances.getOrNull(1) ?: distances.getOrNull(0) ?: return null

        val durationMin = Regex("""(\d+)\s*min""", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        return TripOffer(
            offeredGrossIncome = income,
            pickupDistanceKm = pickupKm,
            tripDistanceKm = tripKm,
            estimatedDurationMinutes = durationMin
        )
    }
}
