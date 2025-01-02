package com.emirkanmaz.ecodes.ui.textrecognition.resultpage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.base.BaseNavigationEvent
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.utils.extensions.dpToPx
import com.emirkanmaz.ecodes.utils.stringprovider.StringProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val stringProvider: StringProvider
): BaseViewModel<BaseNavigationEvent>() {

    private val _recognizedText = MutableStateFlow<String>("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _processedBitmap = MutableStateFlow<Bitmap?>(null)
    val processedBitmap: StateFlow<Bitmap?> = _processedBitmap.asStateFlow()

    fun processImage(bitmap: Bitmap, targetWords: List<String>) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer
                    .process(image)
                    .addOnSuccessListener { visionText ->
                        _recognizedText.value = visionText.text
                        _processedBitmap.value = drawBoundingBoxes(bitmap, visionText, targetWords)
                    }

            } catch (e: Exception) {
                setError(true, stringProvider.getString(R.string.text_recognition_failed))
            } finally {
                setLoading(false)
            }
        }
    }

    private fun drawBoundingBoxes(
        originalBitmap: Bitmap,
        visionText: Text,
        targetWords: List<String>
    ): Bitmap {
        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4.dpToPx().toFloat()
        }

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    if (targetWords.any { it.equals(element.text, ignoreCase = true) }) {
                        element.boundingBox?.let { box ->
                            canvas.drawRect(box, paint)
                        }
                    }
                }
            }
        }

        return mutableBitmap
    }
}