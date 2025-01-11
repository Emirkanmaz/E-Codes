package com.emirkanmaz.ecodes.ui.textrecognition.shared

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedImageViewModel @Inject constructor() : ViewModel() {
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    fun setBitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun clearBitmap() {
        _bitmap.value = null
    }
}