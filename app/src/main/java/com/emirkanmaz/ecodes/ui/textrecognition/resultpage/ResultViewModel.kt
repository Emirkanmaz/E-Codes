package com.emirkanmaz.ecodes.ui.textrecognition.resultpage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.viewModelScope
import com.emirkanmaz.ecodes.R
import com.emirkanmaz.ecodes.base.BaseViewModel
import com.emirkanmaz.ecodes.data.ECodeRepository
import com.emirkanmaz.ecodes.domain.models.ecode.ECodeItemUI
import com.emirkanmaz.ecodes.ui.textrecognition.resultpage.navigationevent.ResultNavigationEvent
import com.emirkanmaz.ecodes.utils.extensions.cleanText
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
    private val eCodeRepository: ECodeRepository,
    private val stringProvider: StringProvider
): BaseViewModel<ResultNavigationEvent>() {

    private val _processedBitmap = MutableStateFlow<Bitmap?>(null)
    val processedBitmap: StateFlow<Bitmap?> = _processedBitmap.asStateFlow()

    private val _matchedECodes = MutableStateFlow<List<ECodeItemUI>?>(emptyList())
    val matchedECodes: StateFlow<List<ECodeItemUI>?> = _matchedECodes.asStateFlow()

    fun removeItem(eCode: ECodeItemUI) {
        _matchedECodes.value = _matchedECodes.value?.filterNot { it == eCode }
    }

    init {
        setLoading(true)
    }

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val eCodesList = mutableListOf<ECodeItemUI>()
                        val processedTexts = mutableSetOf<String>()

                        for (block in visionText.textBlocks) {

                            val lineText = block.text.cleanText()
                            val eCode = eCodeRepository.searchECode(lineText)
                            if (eCode != null && !processedTexts.contains(lineText)) {
                                eCodesList.add(eCode)
                                processedTexts.add(lineText)
                                continue
                            }

                            for (line in block.lines) {

                                val lineText = line.text.cleanText()
                                val lineECode = eCodeRepository.searchECode(lineText)
                                if (lineECode != null && !processedTexts.contains(lineText)) {
                                    eCodesList.add(lineECode)
                                    processedTexts.add(lineText)
                                    continue
                                }

                                val segments = line.text.split(",").flatMap { segment ->
                                    segment.split(" ")
                                }

                                for (word in segments) {
                                    val cleanWord = word.cleanText()
                                    if (cleanWord.isNotEmpty() && !processedTexts.contains(cleanWord)) {
                                        val wordECode = eCodeRepository.searchECode(cleanWord)
                                        if (wordECode != null) {
                                            eCodesList.add(wordECode)
                                            processedTexts.add(cleanWord)
                                        }
                                    }
                                }
                            }
                        }

                        _matchedECodes.value = eCodesList.distinctBy { it.eCode }
                        _processedBitmap.value = drawBoundingBoxes(bitmap, visionText, eCodesList)
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
        eCodesList: List<ECodeItemUI>
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
                val cleanLineText = line.text.cleanText()
                if (eCodesList.any {
                        it.names.tr.equals(cleanLineText, ignoreCase = true) ||
                                it.names.en.equals(cleanLineText, ignoreCase = true) ||
                                it.eCode.equals(cleanLineText, ignoreCase = true)
                    }) {
                    line.boundingBox?.let { box ->
                        canvas.drawRect(box, paint)
                    }
                    continue
                }

                for (element in line.elements) {
                    val cleanElementText = element.text.cleanText()
                    if (eCodesList.any {
                            it.names.tr.equals(cleanElementText, ignoreCase = true) ||
                                    it.names.en.equals(cleanElementText, ignoreCase = true) ||
                                    it.eCode.equals(cleanElementText, ignoreCase = true)
                        }) {
                        element.boundingBox?.let { box ->
                            canvas.drawRect(box, paint)
                        }
                    }
                }
            }
        }

        return mutableBitmap
    }

    fun navigateToDetail(eCode: String){
        viewModelScope.launch{
            navigateTo(ResultNavigationEvent.NavigateToDetail(eCode))
        }
    }

    fun navigateCrop() {
        viewModelScope.launch{
            navigateTo(ResultNavigationEvent.NavigateToCropPage)
        }
    }
}